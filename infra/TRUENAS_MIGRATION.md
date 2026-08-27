# AtChaGong TrueNAS Migration

## Target Structure

```text
atchagong.kro.kr
  -> home router public IP
  -> router port forwarding 80/443
  -> TrueNAS 192.168.219.169:80/443
  -> edge-nginx container
  -> atchagong-server:8080
```

The AtChaGong stack does not run Caddy on TrueNAS. Public HTTP/HTTPS is owned by the shared `edge-nginx` stack, and AtChaGong only exposes its Spring Boot API inside Docker.

## TrueNAS Stack

Expected Dockge stack path:

```text
/mnt/.ix-apps/app_mounts/dockge/stacks/atchagong
```

Expected external Docker network:

```bash
docker network create proxy
```

The NAS compose file is:

```text
infra/docker-compose.nas.yml
```

It creates:

- `atchagong-server`
- `atchagong-mysql`
- `atchagong-mysql-backup`
- `atchagong-prometheus`
- `atchagong-loki`
- `atchagong-grafana`

Grafana is published on `${GRAFANA_PORT:-30001}`. Router port forwarding should not expose this port publicly.

## edge-nginx

Copy or adapt:

```text
infra/nginx/atchagong.edge-nginx.conf
```

into the existing TrueNAS `edge-nginx` stack.

Before enabling the HTTPS server block, issue a certificate for:

```text
atchagong.kro.kr
```

If the certificate does not exist yet, nginx will fail to reload with the HTTPS block enabled.

## Required GitHub Secrets

Build and registry:

- `DOCKERHUB_USERNAME`
- `DOCKERHUB_TOKEN`

NAS deploy:

- `TAILSCALE_AUTHKEY`
- `NAS_HOST` - usually `100.75.95.118`
- `NAS_USER` - usually `ia3264666`
- `NAS_SSH_KEY`
- `NAS_APP_DIR` - usually `/mnt/.ix-apps/app_mounts/dockge/stacks/atchagong`

## Required NAS `.env`

Keep production application secrets on the NAS stack itself:

```text
/mnt/.ix-apps/app_mounts/dockge/stacks/atchagong/.env
```

The GitHub Actions workflow does not create or overwrite this file. It only passes the newly built `SERVER_IMAGE` value for the current deploy command.

Required values:

- `MYSQL_ROOT_PASSWORD`
- `MYSQL_DATABASE`
- `MYSQL_USER`
- `MYSQL_PASSWORD`
- `JWT_SECRET`
- `GOOGLE_CLIENT_ID`
- `APPLE_CLIENT_ID`
- `KAKAO_CLIENT_ID`
- `APP_REVIEW_LOGIN_ID`
- `APP_REVIEW_PASSWORD`
- `APP_REVIEW_USER_ID`
- `GRAFANA_ADMIN_USER`
- `GRAFANA_ADMIN_PASSWORD`

Optional:

- `GRAFANA_PORT`

## Cutover Checklist

1. Create DNS `A` record for `atchagong.kro.kr` to the home public IP.
2. Keep router 80/443 forwarded to the TrueNAS LAN IP.
3. Deploy the NAS stack.
4. Import the EC2 MySQL dump into `atchagong-mysql`.
5. Add the `atchagong.kro.kr` server block to `edge-nginx`.
6. Issue the TLS certificate and reload `edge-nginx`.
7. Test:

```bash
curl -I https://atchagong.kro.kr/actuator/health
```

8. After the NAS deployment is confirmed stable, disable the old EC2 deployment workflow.
