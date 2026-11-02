# Evidence Index

Evidence files are retained in their original locations. This index points to the genuine screenshots and key raw logs currently present. PASS/PARTIAL/PENDING status is maintained in `EVIDENCE_CHECKLIST.md`.

## Component 1 — Git, Maven, Jenkins

| ID | Suggested figure / caption | Screenshot or evidence | Raw log / details |
|---|---|---|---|
| G1 | Figure 1 — GitHub feature branch and repository | `evidence/component-1/Screenshot 2026-11-02 at 11.03.34 AM.png` | `evidence/component-1/git-branch-history.log` |
| G2 | Figure 2 — GitHub PR #1 and merged state | `evidence/component-1/Screenshot 2026-11-02 at 11.05.02 AM.png` | `EVIDENCE_CHECKLIST.md`; PR merge also reflected in Git history |
| M1 | No screenshot — Maven package / test result | — | `evidence/component-1/maven-build.log`, `evidence/component-1/feature-build-test.log` |
| M2 | No screenshot — final audit Maven `clean verify` | — | `evidence/component-1/final-audit-maven-verify-full.log` (5 tests passed); sandbox-limited attempts are retained separately in `final-audit-maven-verify.log` and `final-audit-maven-verify-project-cache.log` |
| J1 | Figure 3 — Jenkins Multibranch branch indexing | `evidence/component-1/jenkins/Screenshot 2026-11-03 at 12.11.00 AM.png` | `evidence/component-1/jenkins/polling-log-excerpt.txt` |
| J2 | Figure 4 — Jenkins build #4 success and archived JAR | `evidence/component-1/jenkins/jenkins-build-4-console-success.png` | `evidence/component-1/jenkins/git-push-verification.log`; see `JENKINS_VERIFICATION.md` |

## Component 2 — Docker, Ansible, Kubernetes

| ID | Requirement / caption | Screenshot or evidence | Raw log / details |
|---|---|---|---|
| D1 | Figure 5 — Docker build, app and health response | `evidence/component-2/docker/Screenshot 2026-11-02 at 1.08.59 PM.png` | `evidence/component-2/docker/docker-build.log`, `evidence/component-2/docker/health-response.txt`, `evidence/component-2/docker/local-http-verification.log` |
| D2 | Figure 6 — Daymark container in Docker Desktop | `evidence/component-2/docker/Screenshot 2026-11-02 at 1.12.00 PM.png` | `evidence/component-2/docker/local-app.log` |
| A1 | Figure 7 — supplementary Ansible deployment/handler recap | `evidence/component-2/ansible/Screenshot 2026-11-03 at 1.45.32 AM.png` | `container-demo-deploy.log`, `container-demo-idempotence.log`, `container-demo-runtime-health.log`; limitations in `ANSIBLE_VERIFICATION.md` |
| K1 | Figure 8 — Kind Service and initial 3 ready pods | `evidence/component-2/kubernetes/Screenshot 2026-11-03 at 1.40.40 AM.png` | `cluster-verification.log`, `scale-3-initial.log`, `final-verification.log` |
| K2 | Figure 9 — scale up to 5 ready pods and health response | `evidence/component-2/kubernetes/Screenshot 2026-11-03 at 1.42.32 AM.png` | `scale-up-5.log`, `service-http-verification.log` |
| K3 | Figure 10 — scale down to 3 ready pods and health response | `evidence/component-2/kubernetes/Screenshot 2026-11-03 at 1.43.01 AM.png` | `scale-down-final-three.log`, `final-verification.log` |
| K4 | No screenshot — ConfigMap/probe consistency and live server dry-run | — | `configmap-probes-verification.log`, `probe-and-final-health.log`, `final-audit-server-dry-run-authorized.log`; screenshot not separately captured |
| K5 | Figure 11 — Daymark browser UI via Kubernetes Service | **PENDING — screenshot missing** | Homepage HTTP response: `service-http-verification.log`; browser screenshot should be saved as `evidence/component-2/kubernetes/daymark-ui.png` |

## Other raw logs

- Host prerequisites: `evidence/prerequisites.log`.
- Docker/Ansible/Kubernetes raw logs: respective directories under `evidence/component-2/`.
- Final Docker image inspection: `evidence/component-2/docker/final-audit-image-inspect-authorized.log`.
- Final Ansible syntax check: `evidence/component-2/ansible/final-audit-syntax-check-combined.log`.
- The unsuccessful initial restricted-environment audit attempts remain preserved alongside the successful reruns; see the closing note in `EVIDENCE_CHECKLIST.md` for their interpretation.
- Application HTTP smoke test: `evidence/component-1/http-smoke.log`.
- Detailed component results: `JENKINS_VERIFICATION.md`, `ANSIBLE_VERIFICATION.md`, and `KUBERNETES_VERIFICATION.md`.

All screenshot entries above refer to files that currently exist. K5 is intentionally listed as pending because the UI was inspected but no browser screenshot was saved. This index does not imply that screenshots provide raw command output; logs are linked separately.
