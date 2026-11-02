# Daymark — Your Tasks, In Focus

Daymark is a lightweight Java application built with Javalin and Maven. It gives users a focused browser-based task manager with a JSON API, and provides a working project for demonstrating source control, continuous integration, configuration management, containerization, and Kubernetes deployment.

**Repository:** [github.com/nousegit/DevOps-project](https://github.com/nousegit/DevOps-project)

## Application

### Problem and objective

Daymark demonstrates a small, understandable task-management application alongside a DevOps workflow. The assignment objective is to show how source changes are tested and packaged, then deployed with automation and container/orchestration tools.

### Technologies

Java 17 language target, Javalin 7.2.4, Maven, JUnit 5, Git/GitHub, Jenkins, Ansible, Docker, Kubernetes, and Kind.

### Features and enhancement

The UI supports adding, completing, filtering, and deleting tasks, with a completed-task counter. The counter is a small usability enhancement and is covered by the integration test suite. Deployment enhancements demonstrated are a consumed Kubernetes ConfigMap and liveness/readiness checks against the application health route. The API provides:

| Method | Path | Purpose |
|---|---|---|
| `GET` | `/` | Browser UI |
| `GET` | `/health` | Health check (`{"status":"UP"}`) |
| `GET` | `/api/todos` | List tasks |
| `POST` | `/api/todos` | Add a task, for example `{"title":"Read a chapter"}` |
| `PATCH` | `/api/todos/{id}` | Update a task title and/or completion state |
| `DELETE` | `/api/todos/{id}` | Delete a task |

The application uses port `7070` by default; set `PORT` to change it. Browser requests and JSON API calls reach the same Javalin process. Jenkins builds, tests, and packages the runnable JAR; the existing Docker, Ansible, and Kubernetes examples deploy that artifact.

```mermaid
flowchart LR
  Dev[Developer] --> Git[GitHub branches and PR]
  Git --> CI[Jenkins SCM-triggered pipeline]
  CI --> Maven[Maven tests and runnable JAR]
  Maven --> Artifact[Archived JAR]
  Artifact --> Deploy[Ansible / Docker / Kubernetes]
  Deploy --> App[Daymark Javalin UI and REST API]
  App --> Health[/health]
```

## Run and verify locally

Requirements: Java 17 or later and Maven. Maven compiles with the Java 17 release target.

```sh
mvn clean verify
java -jar target/todo-app-1.0.0.jar
```

Open <http://localhost:7070/>. The verified Maven suite contains five integration tests covering the UI, completed-counter script, health route, task operations, and blank-title validation.

## DevOps implementation

- **Git:** Work was developed on a feature branch and merged through GitHub PR #1. During this audit, local `main` at `864f041` matched `origin/main` in a read-only GitHub ref check. The working tree also contains uncommitted assignment evidence and Kubernetes/Ansible updates. See [EVIDENCE_CHECKLIST.md](EVIDENCE_CHECKLIST.md).
- **Jenkins:** The Multibranch Pipeline runs tests, packages the JAR, and archives artifacts. Jenkins build #4 was triggered by an SCM change and passed all five tests. The Jenkinsfile polls source control every two minutes; see [JENKINS_VERIFICATION.md](JENKINS_VERIFICATION.md).
- **Docker:** The existing multi-stage Dockerfile builds `daymark:1.0` and runs the app on container port 7070. A genuine Docker build and HTTP 200 health response are recorded in `evidence/component-2/docker/`.
- **Ansible:** The existing `ansible/site.yml` role describes a Java + systemd deployment, but it has not been run successfully on a systemd-capable target. A separate Ubuntu container demonstration successfully copied and ran the JAR, used a restart handler, checked health, and was idempotent. This is a process-based demonstration, not proof of systemd service management. Details and evidence: [ANSIBLE_VERIFICATION.md](ANSIBLE_VERIFICATION.md).
- **Kubernetes:** A local Kind cluster ran the existing ARM64 image `daymark-todo:1.0.0`, with three replicas, a consumed ConfigMap (`PORT=7071`), `/health` liveness/readiness probes, and a NodePort Service. Scaling 3 → 5 → 3 and health checks succeeded. See [KUBERNETES_VERIFICATION.md](KUBERNETES_VERIFICATION.md).

### Kubernetes quick commands

These commands assume the local Kind cluster and kubeconfig used for the demonstration are available. The kubeconfig was kept outside the repository at `/private/tmp/daymark-kubernetes-tools/daymark-kubeconfig`.

```sh
export KUBECONFIG=/private/tmp/daymark-kubernetes-tools/daymark-kubeconfig
kubectl apply -f kubernetes/configmap.yaml
kubectl apply -f kubernetes/deployment.yaml
kubectl apply -f kubernetes/service.yaml
kubectl rollout status deployment/todo-app
kubectl get pods -l app=todo-app
kubectl get svc todo-service
kubectl port-forward service/todo-service 18080:80 --address 127.0.0.1
```

In another terminal, open <http://127.0.0.1:18080/> or query `/health`. To repeat the recorded scale demonstration, scale to five replicas, check readiness, then return to three:

```sh
kubectl scale deployment/todo-app --replicas=5
kubectl rollout status deployment/todo-app
kubectl get pods -l app=todo-app
kubectl scale deployment/todo-app --replicas=3
kubectl rollout status deployment/todo-app
```

The Kind image is loaded locally as `daymark-todo:1.0.0`; the Kubernetes Deployment uses `imagePullPolicy: IfNotPresent`.

## Repository map

```text
src/                         Javalin application, UI assets, integration tests
ansible/                     Existing systemd role and supplementary container demo
kubernetes/                  ConfigMap, Deployment, and Service manifests
evidence/component-1/        Git, Maven, and Jenkins assignment evidence
evidence/component-2/        Docker, Ansible, and Kubernetes evidence
Dockerfile                   Existing multi-stage container build
Jenkinsfile                  Jenkins Multibranch Pipeline
EVIDENCE_CHECKLIST.md        Requirement-by-requirement evidence status
EVIDENCE_INDEX.md            Evidence catalogue and screenshot references
SUBMISSION_CHECKLIST.md      Submission preparation checklist
REPORT_OUTLINE.md            College report structure outline
```

## Limitations and verification status

Tasks are stored in process memory. Restarting an application loses its tasks, and each Kubernetes replica has a separate list, so requests routed to different pods can show different data. A shared persistent store is needed for consistent multi-replica operation and is outside this assignment scope.

The local evidence supports the PASS/PARTIAL/PENDING statuses in [EVIDENCE_CHECKLIST.md](EVIDENCE_CHECKLIST.md). The systemd-based Ansible runtime remains incomplete, and the Kubernetes browser UI was inspected but its screenshot is still pending. A read-only remote check confirmed `main` at `864f041c3e818198a4a0c3c5d9ad0369b42bcd60` and `feature/daymark-improvement` at `9b7c0bc40fe3578799a42927422152c5e350f835`.

See [EVIDENCE_INDEX.md](EVIDENCE_INDEX.md) for evidence file references and [SUBMISSION_CHECKLIST.md](SUBMISSION_CHECKLIST.md) for remaining submission work.
