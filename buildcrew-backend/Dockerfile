# ---------- Build stage ----------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
COPY src ./src

# Generate the RSA keypair used to sign/verify JWTs. These are intentionally
# NOT committed to git (see .gitignore) - they're generated fresh at build
# time instead. This means a redeploy invalidates existing login sessions
# (everyone just logs in again) - a fine trade-off for a small app, and much
# simpler than managing secret files on a hosting platform.
RUN mkdir -p src/main/resources && \
    openssl genrsa -out src/main/resources/privateKey.pem 2048 && \
    openssl rsa -in src/main/resources/privateKey.pem -pubout -out src/main/resources/publicKey.pem

RUN mvn -B -DskipTests package

# ---------- Runtime stage ----------
FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/target/quarkus-app/lib/ ./lib/
COPY --from=build /app/target/quarkus-app/*.jar ./
COPY --from=build /app/target/quarkus-app/app/ ./app/
COPY --from=build /app/target/quarkus-app/quarkus/ ./quarkus/

# Render (and most hosts) set $PORT; application.properties reads it.
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "quarkus-run.jar"]
