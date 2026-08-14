#!/bin/sh
set -eu
midclt call app.custom.create "$(cat /mnt/Ila/ia3264666/nas-web/truenas-app-payload.json)"
