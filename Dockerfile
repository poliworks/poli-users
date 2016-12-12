FROM java:8

WORKDIR /app
ADD target/poli-users-0.1.0-SNAPSHOT-standalone.jar /app/poli-users.jar
EXPOSE 4000
CMD ["java", "-jar", "/app/poli-users.jar"]
