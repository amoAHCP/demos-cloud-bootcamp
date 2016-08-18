# part 1
## build project

mvn clean package

## build docker image
docker build -t gcr.io/kubernetestests/simpleservicewithdb:01 .

## push image to gcloud repo

Be aware of the naming convention when using *gcloud docker* . The image name must contain your project name (*gcr.io/kubernetestests/*)

gcloud docker push gcr.io/kubernetestests/simpleservicewithdb:01

## create services
kubectl create -f kube/mongo-service.yaml
kubectl create -f kube/service.yaml

## create controllers
kubectl create -f kube/mongo-controller.yaml
kubectl create -f kube/controller.yaml

## show pods
kubectl get pods

## show logs of pod

kubectl logs POD-NAME

## show replication controller
kubectl get rc

## show service
kubectl get services

|NAME            |CLUSTER-IP     |EXTERNAL-IP     |PORT(S)     |
|---------------------|:--------------:|:---------------:|:-----------:|----:|  
|sdbservice   |10.3.245.122   |104.155.82.47   |80/TCP|      

open in browser: http://104.155.82.47/