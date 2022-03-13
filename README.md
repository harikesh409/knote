# KNote
This is v2, initial version is at [V1](https://github.com/harikesh409/knote/tree/v1.0.0).
Simple Spring Boot app to take notes.
The app should let you:

1. record notes and
2. attach images to your notes

Notes aren't lost when the app is killed or stopped.

So you will use a database to store the content.
## Prerequisites
- Java 11
- Maven
- Docker
- Minikube

## Deploying the Application
1. Build the docker image.
```shell
eval $(minikube docker-env)
mvn compile jib:dockerBuild
```
2. Deploy mongodb
```shell
kubectl apply -f kube/mongo.yaml
```
3. Deploy minio
```shell
kubectl apply -f kube/minio.yaml
```
4. Deploy the application
```shell
kubectl apply -f kube/knote.yaml
```
5. Get the application url and open it in browser.
```shell
minikube service knote --url
```
