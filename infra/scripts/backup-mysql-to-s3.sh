#!/usr/bin/env bash
set -euo pipefail

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

mkdir -p "${BACKUP_DIR}"

timestamp="$(date -u +%Y%m%dT%H%M%SZ)"
backup_file="${BACKUP_DIR}/atchagong-mysql-${timestamp}.sql.gz"
s3_prefix="${BACKUP_S3_PREFIX:-mysql}"
retention_days="${BACKUP_RETENTION_DAYS:-7}"

docker exec atchagong-mysql sh -c \
  'mysqldump -uroot -p"$MYSQL_ROOT_PASSWORD" --single-transaction --routines --triggers --events "$MYSQL_DATABASE"' \
  | gzip > "${backup_file}"

aws s3 cp "${backup_file}" "s3://${BACKUP_S3_BUCKET}/${s3_prefix}/$(basename "${backup_file}")"

find "${BACKUP_DIR}" -type f -name 'atchagong-mysql-*.sql.gz' -mtime "+${retention_days}" -delete

echo "Backup uploaded: s3://${BACKUP_S3_BUCKET}/${s3_prefix}/$(basename "${backup_file}")"
