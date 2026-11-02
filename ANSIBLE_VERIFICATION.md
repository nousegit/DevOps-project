# Ansible Configuration Management Verification

## Status

The Ansible component now has a successful, repeatable demonstration against an unprivileged Ubuntu 24.04 Docker target. A separate `ansible/container-demo.yml` playbook installs Java, copies the built JAR, starts Daymark as a process under a dedicated user, exercises a restart handler, and verifies `/health`. Its second run reported `changed=0` and `failed=0`.

The existing `ansible/site.yml` and `todo_app` systemd role are preserved and pass syntax validation, but that systemd deployment was not run. The unprivileged Ubuntu systemd container exited with status 255. The automatic approval reviewer rejected the privileged container configuration because host cgroups would be exposed. Therefore the systemd unit's installed/enabled/running state and the existing role's runtime handler remain pending. The process-based container demo is not a substitute for systemd supervision or boot persistence.

## Controller and target environments

- Controller: macOS 27.0.1, Apple Silicon (`arm64`).
- Ansible: Ansible Core 2.21.6, isolated Python 3.14.4 virtual environment at `/private/tmp/daymark-ansible-venv`.
- Ansible temporary directories: `/private/tmp/daymark-ansible-home` and `/private/tmp/daymark-ansible-local`.
- Docker engine: Docker Desktop Linux engine, `aarch64`.
- Linux demo target: Ubuntu 24.04 `arm64` container, SSH on `127.0.0.1:2223` and Daymark port 7070 published on local loopback. Ansible SSH key material was generated under `/private/tmp`; no private key was put in Git or project evidence.
- The deployed process runs as user `daymark` inside the temporary container. It remains available at `http://127.0.0.1:7070/` for local inspection; the SSH key remains outside the repository under `/private/tmp`.
- Systemd target attempt: Ubuntu 24.04 container using `/sbin/init` as PID 1 without elevated host privileges. It exited with code 255. Its Docker logs were empty.

## Inventory and project structure

The checked-in `ansible/inventory.ini` currently has `todo-server ansible_host=127.0.0.1 ansible_connection=local`. On this macOS controller, that points at macOS and is not a suitable target for Linux package management. I left it unchanged and used a temporary SSH inventory under `/private/tmp` for the Ubuntu demo. The SSH private key and temporary inventory are outside the repository.

The existing implementation is:

- `ansible/site.yml`: targets group `todo`, enables privilege escalation, defines `app_user`, `app_port`, JAR source, and destination, then applies the `todo_app` role.
- `ansible/roles/todo_app/tasks/main.yml`: installs `openjdk-17-jre-headless`, creates the `todo` service account and `/opt/todo`, copies the runnable JAR, templates the systemd unit, and enables/starts `todo-app`.
- `ansible/roles/todo_app/handlers/main.yml`: defines `Restart todo app` and restarts the service when notified.
- `ansible/roles/todo_app/templates/todo-app.service.j2`: runs the JAR with `PORT={{ app_port }}` and restarts it on failure.

These findings describe the existing systemd role, which was only reviewed statically. Its handler was not executed. The supplementary container playbook defines its own variables and a process restart handler, which did run after a target JAR change.

## Commands and results

1. `ansible --version` — PASS; Ansible Core 2.21.6.
2. Syntax-checked both `ansible/site.yml` and `ansible/container-demo.yml` — PASS.
3. `mvn -Dmaven.repo.local=.m2/repository clean verify` — PASS, `BUILD SUCCESS`; 5 tests, 0 failures, 0 errors, 0 skipped. The runnable `target/todo-app-1.0.0.jar` was recreated. The project-local Maven cache avoids the sandbox-protected default home cache; the integration tests ran outside the restricted sandbox because socket binding was blocked there.
4. Built `daymark-ansible-target:ubuntu24` — PASS.
5. Started its `/sbin/init` without privileged mode — FAIL as a systemd target: Docker reported `Exited (255)`. A privileged launch with host cgroup namespace and read-write cgroup mount was rejected by the automatic approval reviewer and not executed.
6. Started the Ubuntu container with SSH and ran Ansible's `ping` module — PASS; `todo-server` returned `pong`.
7. Ran `ansible/container-demo.yml` — PASS; Java 17 and `acl` installed, service account and directory created, runnable JAR copied, and process started by the notified handler. The play recap reported `failed=0`.
8. Verified the target's Java version, JAR path/owner, running PID, and host-published `/health` — PASS; HTTP 200 with `{"status":"UP"}`.
9. Ran `ansible/container-demo.yml` a second time — PASS idempotence; recap `changed=0`, `failed=0`.

The first demo attempts exposed two configuration issues: Ansible needed the Ubuntu `acl` package to become the non-login `daymark` account, and the background Java command needed an explicit `/opt/daymark` working directory and single-line redirection. Those fixes are in the supplementary playbook. Earlier failed runs remain recorded as diagnostic evidence.

## Deployment checks

| Check | Result | Detail |
|---|---|---|
| Ansible installed | PASS | Isolated Ansible Core 2.21.6 environment |
| Syntax check | PASS | `ansible/site.yml` parses successfully |
| Inventory connectivity | PASS | SSH Ansible ping returned `pong` on Ubuntu 24.04 |
| Existing systemd role, variables, handler definition | PASS (static review) | Checked in `ansible/site.yml` and `ansible/roles/todo_app/`; not executed |
| Supplementary container playbook | PASS | Installs Java/ACL, copies JAR, starts process, and checks health |
| Java runtime on demo target | PASS | OpenJDK 17.0.20.1 |
| JAR copied to demo target | PASS | `/opt/daymark/todo-app.jar`, owned by `daymark` |
| Daymark process running | PASS | PID file and process check succeeded |
| systemd unit installed/enabled/running | PENDING | Existing systemd playbook not run |
| HTTP `/health` | PASS | Host request returned HTTP 200 and `{"status":"UP"}` |
| Supplementary handler execution | PASS | JAR copy notified and ran the process restart handler |
| Existing systemd handler execution | PENDING | Requires the original role to run on a systemd target |
| Supplementary playbook idempotence | PASS | Second play recap: `changed=0`, `failed=0` |
| Existing systemd role idempotence | PENDING | Requires the original playbook to run twice on a systemd target |
| Supplementary deployment screenshot | PASS | Genuine terminal screenshot shows the completed supplementary playbook and handler recap; it does not demonstrate systemd |

## Evidence files

All paths below are relative to the project root:

- `evidence/component-2/ansible/ansible-version.log`
- `evidence/component-2/ansible/syntax-check.log`
- `evidence/component-2/ansible/maven-verify.log`
- `evidence/component-2/ansible/ubuntu-target-image-build.log`
- `evidence/component-2/ansible/target-container-start.log`
- `evidence/component-2/ansible/target-container-status.log`
- `evidence/component-2/ansible/target-runtime-diagnostic.log`
- `evidence/component-2/ansible/target-image-list.log`
- `evidence/component-2/ansible/container-demo-syntax-check.log`
- `evidence/component-2/ansible/container-demo-target-start.log`
- `evidence/component-2/ansible/container-demo-target-info.log`
- `evidence/component-2/ansible/container-demo-connectivity-success.log`
- `evidence/component-2/ansible/container-demo-first-run.log` (initial privilege-escalation issue)
- `evidence/component-2/ansible/container-demo-first-success.log` (handler shell formatting issue)
- `evidence/component-2/ansible/container-demo-deploy.log` (successful handler and deployment recap)
- `evidence/component-2/ansible/container-demo-idempotence.log`
- `evidence/component-2/ansible/container-demo-runtime-health.log`
- `evidence/component-2/ansible/container-demo-ui-homepage.log`
- `evidence/component-2/ansible/container-demo-app-log-and-status.log`
- `evidence/component-2/ansible/container-demo-reset-for-retry.log`
- `evidence/component-2/ansible/container-demo-reset-for-handler.log`
- `evidence/component-2/ansible/container-demo-reset-final.log`
- `evidence/component-2/ansible/role-review.log`
- `evidence/component-2/ansible/Screenshot 2026-11-03 at 1.45.32 AM.png` (genuine screenshot of supplementary playbook result)
- `evidence/component-2/ansible/container-demo-connectivity.log` (initial missing-inventory attempt)

Raw terminal logs and the listed screenshot are genuine. The screenshot documents the successful supplementary process-based deployment only. The UI was inspected, but a separate browser UI screenshot is not present. No screenshot demonstrates the systemd role; its service install/start, handler, and idempotence remain pending until a systemd-capable Ubuntu target is available.

## Limitations and next requirement

The supplementary playbook provides a genuine Ansible configuration/deployment demonstration without systemd, but its process does not have systemd's boot persistence, restart supervision, or service-manager status. The existing systemd deployment and its runtime checks remain pending until a systemd-capable Ubuntu target is available. Daymark's data is in memory; multiple app replicas would each hold independent task lists.
