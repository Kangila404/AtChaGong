#!/usr/bin/env bash
set -euo pipefail

CONTAINER_NAME="${1:-atchagong-server}"
MAX_ATTEMPTS="${2:-30}"

for attempt in $(seq 1 "${MAX_ATTEMPTS}"); do
  status=$(docker inspect --format='{{.State.Health.Status}}' "${CONTAINER_NAME}" 2>/dev/null || true)
  if [ "${status}" = "healthy" ]; then
    echo "${CONTAINER_NAME} is healthy"
    exit 0
  fi
  if [ "${attempt}" -eq "${MAX_ATTEMPTS}" ]; then
    echo "${CONTAINER_NAME} did not become healthy in time" >&2
    exit 1
  fi
  sleep 5
done