# Assignment evidence checklist

Evidence folders contain raw command output and screenshots only when actually captured. A command's presence here does not mean its demonstration succeeded.

## Component 1 — CI pipeline

| Requirement | Status | Command / expected result | Actual result | Evidence |
|---|---|---|---|---|
| Java 17+ | PASS | `java -version`; Java 17 or later | OpenJDK 25.0.2 present; build compiles for Java 17 | `evidence/prerequisites.log` |
| Maven installed | PASS | `mvn -version`; Maven version shown | Maven 3.10.0 installed | `evidence/prerequisites.log` |
| Maven package build | PASS | `mvn -B clean package`; exit 0 and runnable jar | `BUILD SUCCESS`; `target/todo-app-1.0.0.jar` produced | `evidence/component-1/maven-build.log` |
| Automated tests | PASS | `mvn -Dmaven.repo.local=.m2/repository clean verify`; all tests pass | Final audit run passed: 5 tests, 0 failures/errors/skips. Initial normal-cache sandbox run hit the Maven lock restriction; project-cache run could not bind test sockets in the sandbox; rerun with the project cache outside sandbox succeeded. | `evidence/component-1/final-audit-maven-verify-full.log`; earlier `feature-build-test.log` |
| Feature branch creation and commit | PASS | `git log --graph --oneline --decorate`; feature branch and commit shown | Created from fetched `origin/main`; push succeeded and `git ls-remote` confirmed the GitHub ref | `evidence/component-1/git-branch-history.log` |
| Merge into main | PASS | Inspect `main` history after PR merge | GitHub PR #1 merge is ancestor of current `main` | `git log` |
| Jenkins automatic trigger | PASS | Push change; wait for SCM polling/indexing; no manual build/scan | Build #4 console says `Started by an SCM change`; Jenkins waited for anonymous GitHub API quota before checkout | `evidence/component-1/jenkins/jenkins-build-4-console-success.png` |
| Jenkins exact checkout | PASS | Build checks out the pushed SHA | Revision `864f041c3e818198a4a0c3c5d9ad0369b42bcd60` on `main` | Jenkins build #4 page/console |
| Jenkins test stage | PASS | Test stage reports all tests pass | 5 tests, 0 failures, 0 errors, 0 skipped | Jenkins build #4 console; `JENKINS_VERIFICATION.md` |
| Jenkins Maven build | PASS | Build stage ends in `BUILD SUCCESS` | Maven package stage succeeded | `evidence/component-1/jenkins/jenkins-build-4-console-success.png` |
| Jenkins archived artifact | PASS | Build page lists the archived jar | `target/todo-app-1.0.0.jar` (14.44 MiB) and `target/original-todo-app-1.0.0.jar` (14.44 MiB) | Jenkins build #4 page; `JENKINS_VERIFICATION.md` |

## Component 2 — Deployment

| Requirement | Status | Command / expected result | Actual result | Evidence |
|---|---|---|---|---|
| Ansible deployment requirement as written (systemd) | PARTIAL | Deploy with `ansible/site.yml`; verify enabled/running unit | Supplementary process-based Ubuntu container deployment passed; original systemd role is intact but its runtime was not demonstrated | `ANSIBLE_VERIFICATION.md`, `evidence/component-2/ansible/` |
| Ansible installed | PASS | `ansible --version` | Ansible Core 2.21.6 runs from an isolated Python 3.14.4 virtual environment under `/private/tmp` | `evidence/component-2/ansible/ansible-version.log` |
| Playbook syntax | PASS | Syntax-check `ansible/site.yml` and `ansible/container-demo.yml` | Both playbooks pass syntax validation | `evidence/component-2/ansible/syntax-check.log`, `evidence/component-2/ansible/container-demo-syntax-check.log` |
| Linux target configured | PASS | Configure an Ubuntu target reachable from the Ansible controller | Unprivileged Ubuntu 24.04 SSH target configured; separate systemd target attempt exited 255, so the existing systemd role remains unexecuted | `evidence/component-2/ansible/container-demo-target-start.log`, `evidence/component-2/ansible/target-runtime-diagnostic.log` |
| Inventory connectivity | PASS | `ansible ... -m ansible.builtin.ping` over SSH | `todo-server` returned `pong`; temporary SSH key stayed outside the repository | `evidence/component-2/ansible/container-demo-connectivity-success.log` |
| Role, variable, and handler definition | PASS | Inspect existing role, variables, handler, and service template | Existing `todo_app` role is intact; supplementary playbook also defines variables and a process restart handler | `evidence/component-2/ansible/role-review.log`, `ansible/container-demo.yml` |
| Supplementary handler execution | PASS | Change the JAR on the target and verify the notified handler runs | `Restart Daymark process` ran and reported changed during the successful deployment | `evidence/component-2/ansible/container-demo-deploy.log` |
| Existing systemd handler execution | PENDING | Change relevant systemd role configuration and verify restart handler runs | Existing systemd role was preserved but not executed | `evidence/component-2/ansible/ANSIBLE_VERIFICATION.md` |
| Java installed on Linux target | PASS | Run playbook, then `java -version` on target | OpenJDK 17.0.20.1 installed on Ubuntu 24.04 | `evidence/component-2/ansible/container-demo-runtime-health.log` |
| Daymark process running in container demo | PASS | Inspect target process and PID file | JAR runs as `daymark`; PID check succeeded | `evidence/component-2/ansible/container-demo-runtime-health.log` |
| Daymark systemd service running | PENDING | Run `ansible/site.yml`, then inspect `systemctl status todo-app` | No systemd-capable target was available under the permitted container configuration | `evidence/component-2/ansible/ANSIBLE_VERIFICATION.md` |
| Daymark HTTP health check | PASS | Request `/health`; expect HTTP 200 and `{"status":"UP"}` | Host-mapped container returned HTTP 200 and `{"status":"UP"}` | `evidence/component-2/ansible/container-demo-runtime-health.log` |
| Supplementary deployment idempotence | PASS | Run supplementary playbook twice; second run should have no changes | Second run: `changed=0`, `failed=0` | `evidence/component-2/ansible/container-demo-idempotence.log` |
| Existing systemd role deployment/idempotence | PENDING | Run `ansible/site.yml` twice against a systemd target | Not run; systemd target unavailable | `evidence/component-2/ansible/ANSIBLE_VERIFICATION.md` |
| Ansible raw log collection | PASS | Preserve raw command output | Ansible/Maven/Docker diagnostic logs and role review recorded | `evidence/component-2/ansible/` |
| Ansible screenshot collection | PASS | Capture a genuine terminal screenshot of deployment | Supplementary Ubuntu container deployment/handler recap screenshot exists; systemd runtime remains pending | `evidence/component-2/ansible/Screenshot 2026-11-03 at 1.45.32 AM.png` |
| Docker installed and image built | PASS | `docker version`, `docker build`; image listed | Docker Desktop image build completed for `daymark:1.0`; container health returned HTTP 200 | `evidence/component-2/docker/docker-build.log`, `evidence/component-2/docker/health-response.txt`, Docker screenshots below |
| Docker image/container runtime screenshots | PASS | Capture image build/runtime evidence | Genuine terminal and Docker Desktop screenshots are present | `evidence/component-2/docker/Screenshot 2026-11-02 at 1.08.59 PM.png`, `evidence/component-2/docker/Screenshot 2026-11-02 at 1.12.00 PM.png` |
| Kubernetes tools and local cluster | PASS | `docker version`, `kubectl cluster-info`, `kubectl get nodes` | Kind v0.33.0 on Docker Desktop; Kubernetes v1.37.0 ARM64 node Ready; temporary kubeconfig stored outside Git | `evidence/component-2/kubernetes/prerequisites.log`, `evidence/component-2/kubernetes/cluster-create.log`, `evidence/component-2/kubernetes/cluster-verification.log` |
| Daymark image loaded | PASS | Load the existing image into Kind and confirm pod image | Existing `daymark-todo:1.0.0` (`linux/arm64`, Java 17) loaded; no rebuild | `evidence/component-2/kubernetes/image-load.log` |
| Deployment and Service at 3 replicas | PASS | Apply ConfigMap, Deployment, Service; wait for rollout | Exactly 3 pods Running/Ready, Deployment 3/3, Service `todo-service` NodePort 30070 | `evidence/component-2/kubernetes/deployment-apply-and-ready.log`, `evidence/component-2/kubernetes/scale-3-initial.log`, `evidence/component-2/kubernetes/final-verification.log` |
| Scale 3 → 5 → 3 | PASS | `kubectl scale`; verify ready counts and Service health at each stage | 5/5 Ready then 3/3 Ready; HTTP health succeeded after each scaling action | `evidence/component-2/kubernetes/scale-up-5.log`, `evidence/component-2/kubernetes/scale-down-3.log`, `evidence/component-2/kubernetes/scale-down-final-three.log` |
| Service access, homepage, and API | PASS | `kubectl get svc`; request health, homepage, and task list | `todo-service` exposes 80→7071; port-forward `127.0.0.1:18080`; health HTTP 200, homepage HTTP 200, API list returned JSON | `evidence/component-2/kubernetes/service-http-verification.log`, `evidence/component-2/kubernetes/service-port-forward.log` |
| ConfigMap consumption | PASS | Inspect ConfigMap and runtime environment for all pods | ConfigMap sets `PORT=7071`; each pod reports 7071 and Javalin listens on 7071 | `evidence/component-2/kubernetes/configmap-probes-verification.log` |
| Liveness and readiness probes | PASS | Inspect pod probe definitions, readiness, events, restarts, and `/health` | Both probes use `/health:7071`; pods Ready, restart count 0, no `Unhealthy`/`BackOff` events; health returns `{"status":"UP"}` | `evidence/component-2/kubernetes/configmap-probes-verification.log`, `evidence/component-2/kubernetes/probe-and-final-health.log` |
| Kubernetes terminal screenshot evidence | PASS | Capture genuine cluster, 3/5/3 scaling, and HTTP verification screens | Three genuine terminal screenshots exist for 3 pods, 5 pods with health, and final 3 pods | `evidence/component-2/kubernetes/Screenshot 2026-11-03 at 1.40.40 AM.png`, `evidence/component-2/kubernetes/Screenshot 2026-11-03 at 1.42.32 AM.png`, `evidence/component-2/kubernetes/Screenshot 2026-11-03 at 1.43.01 AM.png` |
| Kubernetes browser UI screenshot | PENDING | Save a genuine screenshot of Daymark opened through the Kubernetes Service | Browser rendering and HTTP homepage were verified, but no UI screenshot file is present | `evidence/component-2/kubernetes/service-http-verification.log`; save as `evidence/component-2/kubernetes/daymark-ui.png` |

## Notes

- Use `PASS`, `FAIL`, `PARTIAL`, or `PENDING` only after recording real execution. Screenshots are genuine captures; the missing Kubernetes UI screenshot remains PENDING.
- Existing untracked screenshots/evidence folders were preserved. Jenkins branch-indexing configuration evidence predates build #4 and is listed separately from the automatic-trigger proof.
- Read-only `git ls-remote origin refs/heads/main refs/heads/feature/daymark-improvement` succeeded: remote `main` is `864f041c3e818198a4a0c3c5d9ad0369b42bcd60`; remote feature branch is `9b7c0bc40fe3578799a42927422152c5e350f835`. No fetch, commit, or push was performed.
- `final-audit-maven-verify.log` and `final-audit-maven-verify-project-cache.log` preserve the initial sandbox-limited failed attempts; `final-audit-maven-verify-full.log` records the successful full verification.
- Other `final-audit-*` diagnostics that show sandbox-limited Docker/Kubernetes access or an initial malformed Ansible syntax-check command are retained. The successful read-only Docker/Kubernetes validations and corrected Ansible syntax checks are in separately named `*-authorized.log` / `*-combined.log` files.
