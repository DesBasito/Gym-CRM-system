FROM maven:3.9.8-amazoncorretto-17 AS build
WORKDIR /build/

COPY pom.xml ./
COPY .mvn .mvn

COPY src ./src

RUN mvn clean package -e -DskipTests

FROM amazoncorretto:17

ARG VERSION=1.0-SNAPSHOT
LABEL org.opencontainers.image.title="Gym CRM System"
LABEL org.opencontainers.image.version="${VERSION}"
LABEL org.opencontainers.image.description="Gym CRM Management System"

WORKDIR /app/

COPY --from=build /build/target/Gym_CRM-system*jar ./Gym_CRM-system.jar
COPY ./config /app/config

EXPOSE 9778

CMD ["java", "-jar", "Gym_CRM-system.jar", "--spring.config.location=file:/app/config/application-prod.yml"]