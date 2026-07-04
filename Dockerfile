FROM amazoncorretto:21
ENV CATSGRAM_IMAGE_DIRECTORY=/app/images
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]