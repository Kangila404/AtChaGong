#!/usr/bin/env bash
set -euo pipefail
umask 077

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
INFRA_DIR="$(cd "${SCRIPT_DIR}/.." && pwd)"
ENV_FILE="${ENV_FILE:-${INFRA_DIR}/.env.prod}"
BACKUP_DIR="${BACKUP_DIR:-${INFRA_DIR}/backups/mysql}"

if [ ! -f "${ENV_FILE}" ]; then
  echo "Missing env file: ${ENV_FILE}" >&2
  exit 1
fi

set -a
# shellcheck disable=SC1090
. "${ENV_FILE}"
set +a

if [ -z "${BACKUP_S3_BUCKET:-}" ]; then
  echo "BACKUP_S3_BUCKET is required in ${ENV_FILE}" >&2
  exit 1
fi

mkdir -p -m 700 "${BACKUP_DIR}"
chmod 700 "${BACKUP_DIR}"
find "${BACKUP_DIR}" -type f -name 'atchagong-mysql-*.sql.gz' -exec chmod 600 {} +

timestamp="$(date -u +%Y%m%dT%H%M%SZ)"
backup_file="${BACKUP_DIR}/atchagong-mysql-${timestamp}.sql.gz"
s3_prefix="${BACKUP_S3_PREFIX:-mysql}"
retention_days="${BACKUP_RETENTION_DAYS:-7}"
upload_retries="${BACKUP_UPLOAD_RETRIES:-3}"
aws_connect_timeout="${AWS_CLI_CONNECT_TIMEOUT:-10}"
aws_read_timeout="${AWS_CLI_READ_TIMEOUT:-60}"
s3_uri="s3://${BACKUP_S3_BUCKET}/${s3_prefix}/$(basename "${backup_file}")"

case "${upload_retries}" in
  ''|*[!0-9]*|0)
    echo "BACKUP_UPLOAD_RETRIES must be a positive integer" >&2
    exit 1
    ;;
esac

case "${retention_days}" in
  ''|*[!0-9]*)
    echo "BACKUP_RETENTION_DAYS must be a non-negative integer" >&2
    exit 1
    ;;
esac

temp_backup_file="$(mktemp "${BACKUP_DIR}/.atchagong-mysql-XXXXXX.sql.gz")"
trap 'rm -f "${temp_backup_file}"' EXIT

docker exec atchagong-mysql sh -c \
  'set -eu; umask 077; dump_config="$(mktemp)"; trap "rm -f \"$dump_config\"" EXIT; printf "[client]\\nuser=root\\npassword=%s\\n" "$MYSQL_ROOT_PASSWORD" > "$dump_config"; mysqldump --defaults-extra-file="$dump_config" --single-transaction --routines --triggers --events "$MYSQL_DATABASE"' \
  | gzip > "${temp_backup_file}"
mv "${temp_backup_file}" "${backup_file}"
chmod 600 "${backup_file}"

for attempt in $(seq 1 "${upload_retries}"); do
  if aws \
    --cli-connect-timeout "${aws_connect_timeout}" \
    --cli-read-timeout "${aws_read_timeout}" \
    s3 cp "${backup_file}" "${s3_uri}"; then
    break
  fi

  if [ "${attempt}" -eq "${upload_retries}" ]; then
    echo "Backup upload failed after ${upload_retries} attempts: ${s3_uri}" >&2
    exit 1
  fi

  echo "Backup upload attempt ${attempt}/${upload_retries} failed; retrying" >&2
  sleep 30
done

find "${BACKUP_DIR}" -type f -name 'atchagong-mysql-*.sql.gz' -mtime "+${retention_days}" -delete

echo "Backup uploaded: ${s3_uri}"
