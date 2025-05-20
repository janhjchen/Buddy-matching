# 构建阶段（使用Maven）
FROM maven:3.8.6-jdk-11 AS builder
WORKDIR /app
# 复制POM文件和源代码
COPY pom.xml .
COPY src ./src
# 构建JAR包（跳过测试）
RUN mvn clean package -DskipTests

# 运行阶段（轻量级JRE）
FROM eclipse-temurin:11-jre-alpine
WORKDIR /app
# 从构建阶段复制JAR文件（动态名称）
COPY --from=builder /app/target/*.jar ./app.jar
# 暴露端口（需与微信云托管配置一致）
EXPOSE 8080
# 启动命令（激活prod环境）
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]