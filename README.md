# KNote
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
2. Deploy mongodb to k8s
```shell
kubectl apply -f kube/mongo.yaml
```
3. Deploy the application
```shell
kubectl apply -f kube/knote.yaml
```
4. Get the application url and open it in browser.
```shell
minikube service knote --url
```
