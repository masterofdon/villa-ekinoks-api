FROM maven:3.9.8-eclipse-temurin-22

COPY config/. /opt/app/.
COPY app.jar /opt/app/app.jar
COPY keystore.p12 /opt/app/keystore.p12
COPY mail-templates /opt/app/mail-templates

ENV SPRING_PROFILES_ACTIVE=production
ENV PROFILE=production

ENV DB_HOST="host.docker.internal"
ENV DB_NAME="villaekinoksdb"
ENV DB_USERNAME="root"
ENV DB_PASSWORD="pED18ggA!"

WORKDIR /opt/app
EXPOSE 443
EXPOSE 8080
CMD ["java" , "-jar" , "app.jar"]