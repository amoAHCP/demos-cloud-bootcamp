## build project

mvn clean package docker:build

## show all images
docker images

## start docker image
docker run -it -p 8080:9090 simpleservicemaven:1.0-SNAPSHOT