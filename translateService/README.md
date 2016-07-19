

## build
mvn clean package docker:build

## run
docker run -it -p 8080:9090 gcr.io/kubernetestests/translateservice:1.0-SNAPSHOT
