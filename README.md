# Daymark Todo

A small Java 17 + Maven + Javalin task manager with a browser UI and JSON API. The same artifact is used by the Jenkins, Ansible, Docker, and Kubernetes examples.

## Run locally

```sh
mvn clean package
java -jar target/todo-app-1.0.0.jar
```

Open [http://localhost:7070](http://localhost:7070). The list is held in memory and resets when the process restarts. Each Kubernetes replica has an independent list, so requests routed to different pods may show different tasks. The app demonstrates deployment, but consistent multi-replica storage requires a shared persistent store.

## API

| Method | Path | Purpose |
| --- | --- | --- |
| GET | `/api/todos` | List tasks |
| POST | `/api/todos` | Add a task with `{"title":"Read a chapter"}` |
| PATCH | `/api/todos/{id}` | Update `title` and/or `completed` |
| DELETE | `/api/todos/{id}` | Delete a task |
| GET | `/health` | Health check |

## DevOps demonstration files

- `Jenkinsfile`: Maven build and archived jar artifact.
- `ansible/site.yml`: applies the `todo_app` role to install Java and run the jar as a systemd service. Adjust `ansible/inventory.ini` and the package name for the target OS.
- `Dockerfile`: multi-stage image build.
- `kubernetes/`: ConfigMap, 3-replica Deployment with liveness/readiness probes, and NodePort Service.

Example Kubernetes scale demonstration:

```sh
kubectl apply -f kubernetes/configmap.yaml
kubectl apply -f kubernetes/deployment.yaml
kubectl apply -f kubernetes/service.yaml
kubectl scale deployment/todo-app --replicas=5
kubectl scale deployment/todo-app --replicas=2
```

For a local cluster, build/import the image so it is available as `todo-app:1.0.0` before applying the manifests.
