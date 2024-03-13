FROM openjdk:21-oracle
WORKDIR /app
COPY target/telegram-bot-currency-rate-0.0.1-SNAPSHOT.jar app.jar
CMD ["java", "-jar", "app.jar"]

