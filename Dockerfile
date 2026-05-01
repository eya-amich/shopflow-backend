FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/shopflow-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 10000

ENV SPRING_DATASOURCE_URL=jdbc:mysql://mysql-1ec8da41-shopflow.l.aivencloud.com:28369/defaultdb?sslMode=REQUIRED
ENV SPRING_DATASOURCE_USERNAME=avnadmin
ENV SPRING_DATASOURCE_PASSWORD=AVNS_9hWbHi18bAr3jDbLOLK
ENV APP_JWT_SECRET=MaCleSecreteSuperLongueEtComplexePourJWT2025ShopFlowProject
ENV APP_JWT_ACCESS_TOKEN_EXPIRATION=86400000
ENV APP_JWT_REFRESH_TOKEN_EXPIRATION=604800000

ENTRYPOINT ["java", "-jar", "app.jar"]