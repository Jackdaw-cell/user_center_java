# 基础镜像
FROM  maven:3.5-jdk-8-alpine as builder

# 指定路径
WORKDIR /app
# 复制jar文件到路径
COPY pom.xml .
COPY src ./src

RUN mvn package -DskipTests

CMD ["java","-jar","./target/JavaPro-0.0.2-SNAPSHOT.jar","--spring-profiles.active=prod"]