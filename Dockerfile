# specify base image
FROM adoptopenjdk/openjdk11:slim

# install Maven on top of base image
RUN apt-get update && apt-get install -y --no-install-recommends maven

# define working directory
WORKDIR /app

# copy over app files
COPY pom.xml .
COPY src src

# expose default Spring Boot port 8080
EXPOSE 8080

# define default command
CMD ["/bin/sh", "-c", "mvn spring-boot:run"]