# Daymark DevOps Assignment — Report Outline

Use the college-provided report template if available. Fill in student, faculty, department, and institution details from the official template; none have been inferred or invented here. Keep Component 1 and Component 2 as separate report sections.

## Front matter

1. Title page — project title, student/course/institution fields from the college template.
2. Certificate — use the college-issued certificate format and authorized signatures.
3. Abstract — summarize the Daymark application and the CI/CD and deployment demonstrations.
4. Table of contents and list of figures.

## 1. Introduction

- Problem statement and project objectives.
- Daymark task-manager scope: browser UI and REST API.
- Tools: Java 17 target, Javalin 7.2.4, Maven, Git/GitHub, Jenkins, Ansible, Docker, Kubernetes/Kind.

## 2. Implementation

### 2.1 Development Environment

- macOS/Apple Silicon controller; Java/Maven versions and tools used for the recorded demonstrations.
- Local Kind/Docker environment and Ubuntu Ansible container target, with target type clearly stated.

### 2.2 Code

- Describe the existing application, routes, UI features, Maven build, and five integration tests.
- Refer to `src/main/java/com/todo/Main.java`, `src/main/resources/public/`, and `src/test/java/com/todo/MainTest.java`.

### 2.3 Description — Component 1 CI pipeline

- Git feature branch, PR #1, and merge into `main` (Figure references G1–G2).
- Maven build and test results (Figure/log reference M1).
- Jenkins Multibranch Pipeline, SCM-triggered build #4, successful test/package stages, and archived JAR (Figures J1–J2).
- Enhancement justification: automated tests catch application/API regressions before artifact publication.

### 2.4 Description — Component 2 deployment

- **Ansible:** inventory, reusable role, variables, and handler. Explain both the successful supplementary process-based Ubuntu container demonstration and the original systemd deployment limitation. Do not claim systemd service execution succeeded. (Figure A1; raw logs in `evidence/component-2/ansible/`).
- **Docker:** existing multi-stage image build, container port 7070, running container, and `/health` HTTP 200 (Figures D1–D2).
- **Kubernetes:** local Kind cluster, ConfigMap sets `PORT=7071`, probes use `/health`, Service, and 3 → 5 → 3 replica demonstration (Figures K1–K3).
- Cite raw command logs from the corresponding evidence folders alongside screenshots.

### 2.5 Enhancement and justification

- Completed-task counter in the Daymark UI and its integration test.
- Kubernetes ConfigMap consumption and liveness/readiness checks as deployment enhancements.
- Explain the purpose and evidence for each enhancement without claiming work not demonstrated.

## 3. Results with screenshots

- Summarize verified outcomes and remaining PARTIAL/PENDING items using `EVIDENCE_CHECKLIST.md`.
- Figure IDs, captions, and paths are catalogued in `EVIDENCE_INDEX.md`.
- State limitations: tasks are in memory, each Kubernetes replica has separate task data, and process restart/pod restart loses data. Shared persistence is out of scope.
- State that original Ansible systemd runtime and Kubernetes browser screenshot remain incomplete/unverified. The GitHub remote refs were successfully checked read-only during this audit.

## 4. Conclusion and future scope

- Conclude only from recorded demonstrations.
- Possible future scope: complete the systemd target demonstration, add the missing Kubernetes browser capture, then use shared persistence if consistent multi-replica task state is needed.

## References

- Official project repository: `repo_link.txt`.
- Official documentation actually used: Java, Maven, Javalin, Jenkins, Ansible, Docker, Kubernetes, and Kind. Add exact document titles and access dates in the final formatted report.

## Figures

Use the IDs and captions from `EVIDENCE_INDEX.md`. One required capture remains pending: a genuine browser screenshot of Daymark served through Kubernetes. Do not replace it with a terminal capture or generated image.
