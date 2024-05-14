# Gunakan image dasar OpenJDK 17
FROM openjdk:17-jdk-alpine

# Set working directory
WORKDIR /app

# Salin file pom.xml dan source code ke container
COPY pom.xml /app/
COPY src /app/src/

# Install Maven
RUN apk add --no-cache maven

# Package aplikasi menggunakan Maven
RUN mvn -f /app/pom.xml clean package -DskipTests

# Ekspos port 8080
EXPOSE 8080

# Jalankan aplikasi
CMD ["java", "-jar", "/app/target/newsscraper-0.0.1-SNAPSHOT.jar"]