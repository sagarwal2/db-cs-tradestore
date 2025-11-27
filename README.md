# Trade Store - Runnable Spring Boot Project

## What is included
- Spring Boot REST service for Trade Store (create/replace, get latest, get version)
- Kafka consumer example (consumes JSON messages on topic `trades`)
- Dockerfile and docker-compose to run Postgres, Zookeeper, Kafka and the app
- Actuator endpoints for health and Prometheus metrics

## Run locally (quick - H2)
mvn spring-boot:run

- API docs: http://localhost:8080/swagger-ui/index.html
- Health: http://localhost:8080/actuator/health
- Prometheus: http://localhost:8080/actuator/prometheus

## Run with Docker (Postgres + Kafka)
1. Build jar: `mvn package -DskipTests`
2. Start services: `docker-compose up --build`
3. App will be available at http://localhost:8080

## Kafka
- Example topic: `trades`
- Messages should be JSON matching `TradeDto` shape.

