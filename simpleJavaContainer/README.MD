## build: 
1. mvn clean package
2. docker build -t trivadis/helloworld:1.0 .
## run: 
docker run --name helloworldcontainer trivadis/helloworld:1.0
