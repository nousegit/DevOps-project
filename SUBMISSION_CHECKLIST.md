# Submission Checklist

Use this checklist with `EVIDENCE_CHECKLIST.md` and the college-provided formatting template. Do not treat documentation preparation as proof that a pending demonstration has passed.

## Ready from recorded evidence

- [x] Existing Daymark application, UI, API, Maven configuration, and tests retained.
- [x] Git feature branch / PR #1 merge evidence retained.
- [x] Jenkins build #4 SCM-triggered success and artifact archival documented.
- [x] Docker image build, running container, and HTTP health evidence present.
- [x] Supplementary Ansible Ubuntu process deployment, handler, health check, and idempotence demonstrated.
- [x] Kind Kubernetes 3 → 5 → 3 deployment, ConfigMap consumption, probes, Service, and health documented.
- [x] Evidence paths indexed in `EVIDENCE_INDEX.md`.
- [x] Repository URL recorded in `repo_link.txt`.
- [x] Report outline follows the assignment sections in `REPORT_OUTLINE.md`.
- [x] Read-only GitHub ref check confirmed current remote `main` and feature-branch SHAs.

## Partial or pending before final submission

- [ ] **Ansible systemd role runtime:** `ansible/site.yml` and role pass syntax/static inspection, but systemd unit installation, service state, existing role handler, and idempotence were not demonstrated. Keep reported as pending/partial.
- [ ] **Kubernetes browser capture:** browser UI was inspected at `http://127.0.0.1:18080/`; save a genuine screenshot as `evidence/component-2/kubernetes/daymark-ui.png` if the cluster and port-forward are available.
- [ ] **College report:** populate official title page/certificate fields and format the final report using the official college template. No student or faculty identity was invented.
- [ ] Check the college rubric against the status table and include each required screenshot/log reference from `EVIDENCE_INDEX.md`.

## Before uploading

- [ ] Confirm no local credentials/private keys or temporary kubeconfig were copied into the repository; keys and kubeconfig used in demonstrations were stored under `/private/tmp`.
- [ ] Review the exact files to submit; preserve original evidence files and their timestamps/content.
- [ ] Keep sandbox-restriction and malformed-command diagnostic logs locally unless the instructor requests the full execution history; they are not evidence of application/configuration failure. The checklist identifies the successful reruns.
- [ ] Check `git status --short --branch` and inspect diffs. The current local project includes uncommitted documentation, Kubernetes manifest, Ansible demo, and evidence changes.
- [ ] When ready, stage only the intended assignment files, commit with the user's chosen message, and push only after their explicit instruction. No commit or push was performed during this audit.

## Known application limitation

Task state is held in memory and is separate in every Kubernetes replica. Restarts lose tasks, and requests routed between replicas can see different lists. Shared persistence is outside this assignment scope.
