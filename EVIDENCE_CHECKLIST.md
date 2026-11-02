# Assignment evidence checklist

Evidence folders contain raw command output and screenshots only when actually captured. A command's presence here does not mean its demonstration succeeded.

## Component 1 — CI pipeline

| Requirement | Status | Command / expected result | Actual result | Evidence |
|---|---|---|---|---|
| Java 17+ | PASS | `java -version`; Java 17 or later | OpenJDK 25.0.2 present; build compiles for Java 17 | `evidence/prerequisites.log` |
| Maven installed | PASS | `mvn -version`; Maven version shown | Maven 3.10.0 installed | `evidence/prerequisites.log` |
| Maven package build | PASS | `mvn -B clean package`; exit 0 and runnable jar | `BUILD SUCCESS`; `target/todo-app-1.0.0.jar` produced | `evidence/component-1/maven-build.log` |
| Automated tests | PASS | `mvn -B clean verify`; all tests pass | 5 integration tests passed (UI root, completed-counter script, health, task CRUD, blank-title validation) | `evidence/component-1/feature-build-test.log` |
| Feature branch creation and commit | PASS | `git log --graph --oneline --decorate`; feature branch and commit shown | Created from fetched `origin/main`; pushed branch verified after push | `evidence/component-1/git-branch-history.log` |
| Merge into main | PENDING | PR review and merge into `main` | Intentionally left for project owner | User will complete |
| Jenkins automatic trigger | Pending | Push to configured repo / polling log triggers a run | Jenkins not installed/on PATH; pipeline has SCM polling, but trigger not demonstrated | `evidence/component-1/` |
| Jenkins successful build | Pending | Jenkins console shows build and tests pass | Jenkins unavailable/not checked | `evidence/component-1/` |
| Jenkins archived artifact | Pending | Jenkins artifact browser lists the jar | Not demonstrated | `evidence/component-1/` |

## Component 2 — Deployment

| Requirement | Status | Command / expected result | Actual result | Evidence |
|---|---|---|---|---|
| Ansible installed | Pending | `ansible --version` | Not installed/on PATH | `evidence/prerequisites.log` |
| Ansible role deployment | Pending | `ansible-playbook ...`; successful play recap | Not run | `evidence/component-2/` |
| Docker installed and image built | Pending | `docker version`, `docker build`; image listed | Docker not installed/on PATH | `evidence/prerequisites.log` |
| Kubernetes cluster | Pending | `kubectl get nodes`; node Ready | kubectl/minikube not installed/on PATH | `evidence/prerequisites.log` |
| Deployment at 3 replicas | Pending | Apply manifests and inspect pods | Not run | `evidence/component-2/` |
| Scale 3 → 5 → down | Pending | `kubectl scale`; pod counts match each step | Not run | `evidence/component-2/` |
| Service access and API | Pending | `kubectl get svc` and HTTP request succeeds | Not run | `evidence/component-2/` |
| ConfigMap and probes | Pending | Inspect applied deployment and pod probe status | Configured in files, not demonstrated | `evidence/component-2/` |

## Notes

- Use `PASS`, `FAIL`, or `PENDING` only after recording real execution. Attach command output in the corresponding evidence folder and screenshots when a UI is part of the demonstration.
- GitHub collaboration, webhook configuration, and screenshots require access to the actual remote/Jenkins UI and are pending until captured.
