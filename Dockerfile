FROM openjdk:17-jdk-slim

LABEL maintainer="David kangnigabiam720@gmail.com"

EXPOSE 8085

COPY target/admin-management.jar admin-management.jar

ENTRYPOINT ["java", "-jar", "admin-management.jar"]
