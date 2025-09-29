FROM openjdk:17-jdk-slim

WORKDIR /app

COPY gradle/ gradle/
COPY gradlew build.gradle ./
RUN ./gradlew dependencies --no-daemon

COPY src/ src/
RUN ./gradlew build --no-daemon

EXPOSE 8080

CMD ["java", "-jar", "build/libs/campus-time-bank-0.0.1-SNAPSHOT.jar"]
