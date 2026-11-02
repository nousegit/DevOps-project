# Assignment report outline

Use the college template for the title page and certificate when it is provided. This outline follows the project requirements summarized in the supplied assignment notes.

1. Title page
2. Certificate
3. Abstract
4. Introduction and objectives
5. Tools and technologies (Java 17, Javalin, Maven, Git, Jenkins, Ansible, Docker, Kubernetes)
6. Component 1 — CI pipeline
   - Application and REST API design
   - Git branches and merge (screenshot reference: pending)
   - Maven build and automated tests (command output: pending)
   - Jenkins pipeline, automatic trigger, successful console, archived jar (screenshots: pending)
   - CI enhancement: automated tests
7. Component 2 — Deployment
   - Ansible inventory, role, variables, handler and playbook (output: pending)
   - Docker multi-stage build (output/screenshot: pending)
   - Kubernetes ConfigMap, Deployment, probes and Service
   - Three initial replicas, scale to five, then scale down (screenshots: pending)
   - API /health and UI access in deployment (screenshots: pending)
8. Results and evidence index (link each item to `EVIDENCE_CHECKLIST.md` and files in `evidence/`)
9. Limitations: application tasks live in process memory. Kubernetes replicas have separate task lists and restart loses all tasks; use a shared persistent store before relying on multi-replica task consistency.
10. Conclusion
11. References
