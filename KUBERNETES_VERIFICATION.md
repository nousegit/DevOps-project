# Daymark Kubernetes Verification

## Summary

Kubernetes deployment and scaling were executed locally with Kind and Docker Desktop on Apple Silicon. The existing Daymark image was reused without rebuilding. The final state is 3/3 Running and Ready pods behind `todo-service`; scaling to 5/5 and back to 3/3 succeeded, with HTTP health checks after each change.

The original Ansible systemd deployment remains pending and was not changed as part of this work. Jenkins and the Ansible demo container remained running.

## Environment

- Host: macOS 27.0.0, Apple Silicon `arm64`.
- Docker Desktop: 4.97.0; Docker Engine 29.9.2, `linux/arm64`.
- kubectl: v1.36.1.
- Kind: v0.33.0, Darwin/ARM64 binary downloaded to `/private/tmp/daymark-kubernetes-tools/kind`; its SHA-256 matched the official v0.33.0 checksum.
- Kind cluster: `daymark-assignment`, one control-plane node, Kubernetes v1.37.0 ARM64.
- Kubeconfig: `/private/tmp/daymark-kubernetes-tools/daymark-kubeconfig`, kept outside the repository.
- Existing Ansible demo container continued running on host port 7070; Jenkins uses port 8080. Kubernetes access used loopback port 18080.

## Existing project files and minimal corrections

- Existing image: `daymark-todo:1.0.0`, image ID `cad675255531f0014e58d14fd99f3e206a2caf2ce43349b45bced27f32e0c5ee`, `linux/arm64`, Java 17. It was loaded directly into Kind; it was not rebuilt or removed.
- `kubernetes/deployment.yaml` referenced the absent tag `todo-app:1.0.0`; it now references the actual local image `daymark-todo:1.0.0`.
- To demonstrate that the ConfigMap is consumed, `kubernetes/configmap.yaml` now sets `PORT: "7071"`, different from the image's default `PORT=7070`. The Deployment's container port and both probes, plus the Service target port, were aligned to 7071.
- The Service remains a NodePort Service with Service port 80 and NodePort 30070. For host access, `kubectl port-forward` mapped Service port 80 to `127.0.0.1:18080` to avoid Jenkins and Ansible ports.

## Commands executed

Prerequisites and cluster:

```sh
docker version
kubectl version --client
/private/tmp/daymark-kubernetes-tools/kind version
/private/tmp/daymark-kubernetes-tools/kind create cluster \
  --name daymark-assignment \
  --image kindest/node:v1.37.0@sha256:a1ed56cfb0e7b93589bdf97c8cd566405a265939e3620fc4f5de89adff580ae5 \
  --kubeconfig /private/tmp/daymark-kubernetes-tools/daymark-kubeconfig \
  --wait 5m
KUBECONFIG=/private/tmp/daymark-kubernetes-tools/daymark-kubeconfig kubectl cluster-info --context kind-daymark-assignment
KUBECONFIG=/private/tmp/daymark-kubernetes-tools/daymark-kubeconfig kubectl get nodes -o wide
```

Image and deployment (applied in dependency order):

```sh
/private/tmp/daymark-kubernetes-tools/kind load docker-image daymark-todo:1.0.0 --name daymark-assignment
KUBECONFIG=/private/tmp/daymark-kubernetes-tools/daymark-kubeconfig kubectl apply -f kubernetes/configmap.yaml
KUBECONFIG=/private/tmp/daymark-kubernetes-tools/daymark-kubeconfig kubectl apply -f kubernetes/deployment.yaml
KUBECONFIG=/private/tmp/daymark-kubernetes-tools/daymark-kubeconfig kubectl apply -f kubernetes/service.yaml
KUBECONFIG=/private/tmp/daymark-kubernetes-tools/daymark-kubeconfig kubectl rollout status deployment/todo-app --timeout=180s
```

ConfigMap, probes, and service:

```sh
KUBECONFIG=/private/tmp/daymark-kubernetes-tools/daymark-kubeconfig kubectl get configmap todo-config -o yaml
KUBECONFIG=/private/tmp/daymark-kubernetes-tools/daymark-kubeconfig kubectl get pods -l app=todo-app -o wide
KUBECONFIG=/private/tmp/daymark-kubernetes-tools/daymark-kubeconfig kubectl exec <pod-name> -- printenv PORT
KUBECONFIG=/private/tmp/daymark-kubernetes-tools/daymark-kubeconfig kubectl describe pod -l app=todo-app
KUBECONFIG=/private/tmp/daymark-kubernetes-tools/daymark-kubeconfig kubectl get svc todo-service -o wide
KUBECONFIG=/private/tmp/daymark-kubernetes-tools/daymark-kubeconfig kubectl port-forward service/todo-service 18080:80 --address 127.0.0.1
curl -i --fail http://127.0.0.1:18080/health
curl -sS -o /dev/null -w 'homepage HTTP %{http_code}, content-type %{content_type}\n' http://127.0.0.1:18080/
curl -sS --fail http://127.0.0.1:18080/api/todos
```

Scaling:

```sh
KUBECONFIG=/private/tmp/daymark-kubernetes-tools/daymark-kubeconfig kubectl scale deployment/todo-app --replicas=5
KUBECONFIG=/private/tmp/daymark-kubernetes-tools/daymark-kubeconfig kubectl rollout status deployment/todo-app --timeout=180s
KUBECONFIG=/private/tmp/daymark-kubernetes-tools/daymark-kubeconfig kubectl get pods -l app=todo-app -o wide
curl -i --fail http://127.0.0.1:18080/health

KUBECONFIG=/private/tmp/daymark-kubernetes-tools/daymark-kubeconfig kubectl scale deployment/todo-app --replicas=3
KUBECONFIG=/private/tmp/daymark-kubernetes-tools/daymark-kubeconfig kubectl rollout status deployment/todo-app --timeout=180s
KUBECONFIG=/private/tmp/daymark-kubernetes-tools/daymark-kubeconfig kubectl get pods -l app=todo-app -o wide
curl -i --fail http://127.0.0.1:18080/health
```

## Verified results

| Demonstration | Result |
|---|---|
| Cluster initialization | PASS; Kind cluster `daymark-assignment` created and control plane Ready |
| Node status | PASS; `daymark-assignment-control-plane` Ready, Kubernetes v1.37.0 |
| Image | PASS; existing `daymark-todo:1.0.0` loaded and used by pods |
| Initial deployment | PASS; exactly 3 pods, all Running/Ready, zero restarts |
| ConfigMap consumed | PASS; each pod's runtime environment reports `PORT=7071`, and Javalin logs listening on 7071 |
| Probes | PASS; liveness and readiness use `/health` on 7071; all pods Ready, zero restarts, no `Unhealthy` or `BackOff` events |
| Service | PASS; `todo-service` NodePort, Service port 80 targeting 7071, nodePort 30070; 3 ready endpoints |
| `/health` | PASS; HTTP 200 and `{"status":"UP"}` before scaling, at 5 replicas, and after returning to 3 |
| Homepage/UI | PASS; HTTP 200 HTML and the browser rendered “Daymark — your tasks, in focus” through `http://127.0.0.1:18080/` |
| Task API listing | PASS; `/api/todos` returned JSON through the Kubernetes Service |
| Scale 3 → 5 | PASS; 5/5 pods Running/Ready; Service health returned HTTP 200 |
| Scale 5 → 3 | PASS; after terminating excess pods, exactly 3 pods and 3 endpoints remained; health returned `{"status":"UP"}` |
| Terminal screenshots | PASS; genuine screenshots document the initial 3 pods, 5 pods with health response, and final 3 pods |
| Browser UI screenshot | PENDING; the browser rendered the UI through the Service, but no UI screenshot file is present |

## In-memory data limitation

Daymark stores tasks in a static in-memory list in each JVM. Each Kubernetes replica therefore has a separate task list. Requests routed to different pods can observe different tasks, and pod restarts discard that pod's task data. Persistent, consistent multi-replica task storage requires a shared database or other persistence layer, which is intentionally out of scope.

## Evidence files

Raw command output is under `evidence/component-2/kubernetes/`:

- `kind-install.log`, `prerequisites.log`
- `cluster-create.log`, `cluster-node-image-pull.log`, `cluster-verification.log`
- `image-load.log`, `deployment-apply-and-ready.log`
- `configmap-probes-verification.log`, `pod-diagnostics.log`, `probe-and-final-health.log`
- `service-port-forward.log`, `service-http-verification.log`
- `scale-3-initial.log`, `scale-up-5.log`, `scale-down-3.log`, `scale-down-final-three.log`
- `final-verification.log`

Genuine terminal screenshots already present in `evidence/component-2/kubernetes/`:

- `Screenshot 2026-11-03 at 1.40.40 AM.png` — 3 healthy pods and Service details.
- `Screenshot 2026-11-03 at 1.42.32 AM.png` — 5 healthy pods and health response.
- `Screenshot 2026-11-03 at 1.43.01 AM.png` — scaled back to 3 healthy pods and health response.

The browser UI was opened at `http://127.0.0.1:18080/` and the homepage returned HTTP 200, but a genuine browser screenshot was not saved. Capture that page manually and save it as `evidence/component-2/kubernetes/daymark-ui.png` to complete UI screenshot evidence. No existing screenshot has been modified.
