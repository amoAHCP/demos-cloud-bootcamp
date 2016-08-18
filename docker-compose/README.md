# start the multi-container application without docker-compose

docker run -d --name mongo mongo
docker run -d -P --name frontend --link mongo:mongo -p 8181:8181 amoahcp/vertx-frontend:1.0
docker run -d -P --name read --link mongo:mongo --link frontend:frontend amoahcp/vertx-read:1.0
docker run -d -P --name write --link mongo:mongo --link frontend:frontend --link read:read amoahcp/vertx-write:1.0

## stop containers

docker stop $(docker ps -a -q)


# start application using docker-compose

build each project with *mvn clean package*

run *docker-compose up*

