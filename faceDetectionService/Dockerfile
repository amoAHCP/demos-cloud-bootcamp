FROM maven:3.3.3-jdk-8
RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app
ONBUILD ADD . /usr/src/app

ADD src/main/resources/optionalconfig.json /conf/
COPY . /usr/src/app
EXPOSE 9090
CMD ["java", "-jar", "/usr/src/app/target/faceDetectionService-fat.jar", "-conf","/conf/optionalconfig.json"]