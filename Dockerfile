FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Assume the jar is already built locally via `mvn clean package -DskipTests`
# and exists in the target/ directory
COPY target/electronicsShop-*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
