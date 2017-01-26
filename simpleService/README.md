# part 1
## build project

mvn clean package

## build docker image, The image name must contain your project name (*gcr.io/kubernetestests/*)
docker build -t gcr.io/kubernetestests/simpleservice:01 .

## start docker image
docker run -it -p 8080:9090 gcr.io/kubernetestests/simpleservice:01


# part 2

## push image to gcloud repo

Be aware of the naming convention when using *gcloud docker* . 

gcloud docker push gcr.io/kubernetestests/simpleservice:01

## create service
kubectl create -f kube/service.yaml

## create controller
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
|simpleservice   |10.3.245.122   |104.155.82.47   |80/TCP|      

open in browser: http://104.155.82.47/hallo/cloud