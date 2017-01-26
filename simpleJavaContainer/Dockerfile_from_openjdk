FROM java:openjdk-8-jdk
MAINTAINER trivadisAG
ENV httpPort 8181
EXPOSE $httpPort
ADD . /usr/local/helloworld
CMD ["java", "-jar", "/usr/local/helloworld/target/simpleJavaContainer-fat.jar"]