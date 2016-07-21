

## build
mvn clean package docker:build

## run
docker run -it -p 8080:9090 gcr.io/kubernetestests/facedetectiondervice:01
## push
gcloud docker push gcr.io/kubernetestests/facedetectiondervice:01

## create service
kubectl create -f kube/service.yaml
wait for public ip
## check for public ip
kubectl get services

## create controller
kubectl create -f kube/controller.yaml 

open: http://PUBLIC-IP
