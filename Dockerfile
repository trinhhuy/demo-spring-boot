# Sử dụng JDK 17
FROM openjdk:17-jdk-slim

# Đặt biến môi trường
ENV APP_HOME=/app
WORKDIR $APP_HOME

# Sao chép file JAR vào container
COPY target/*.jar app.jar

# Expose cổng 8080
EXPOSE 8080

# Lệnh chạy ứng dụng
ENTRYPOINT ["java", "-jar", "app.jar"]
