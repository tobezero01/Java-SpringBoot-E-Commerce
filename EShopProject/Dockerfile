# ===== Stage 1: BUILD (Maven + JDK) =====
# ===== Stage 1: BUILD =====
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build

# copy cả EShopCommon và EShopWebParent vào context
COPY . .

# Cài EShopCommon trước vào local repo của container
RUN mvn -B -ntp -DskipTests -f EShopCommon/pom.xml clean install

# Build Web-page-for-customer
RUN mvn -B -ntp -DskipTests -f EShopWebParent/Web-page-for-customer/pom.xml clean package

# ===== Stage 2: RUNTIME =====
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /build/EShopWebParent/Web-page-for-customer/target/Web-page-for-customer-0.0.1-SNAPSHOT.jar app.jar

ARG DB_URL
ARG DB_USERNAME
ARG DB_PASSWORD
ARG JWT_SECRET
ARG GOOGLE_CLIENT_ID
ARG GOOGLE_CLIENT_SECRET
ARG COOKIE_SECURE=false
ARG CORS_ALLOWED_ORIGINS=http://localhost:4200

# ===== Map ARG -> ENV so Spring reads them at runtime =====
ENV DB_URL=${DB_URL} \
    DB_USERNAME=${DB_USERNAME} \
    DB_PASSWORD=${DB_PASSWORD} \
    JWT_SECRET=${JWT_SECRET} \
    GOOGLE_CLIENT_ID=${GOOGLE_CLIENT_ID} \
    GOOGLE_CLIENT_SECRET=${GOOGLE_CLIENT_SECRET} \
    COOKIE_SECURE=${COOKIE_SECURE} \
    CORS_ALLOWED_ORIGINS=${CORS_ALLOWED_ORIGINS}


EXPOSE 8386
ENV JAVA_OPTS="-Xms256m -Xmx512m"
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]




# ================================ Build theo cách dùng file jar có sẵn ================================
# image runtiem JRE java 17
#FROM eclipse-temurin:17-jre-alpine
#
## thu muc la viec trong container
#WORKDIR /app
#
## Đường dẫn này phải khớp với file thực tế của bạn
#COPY EShopWebParent/Web-page-for-customer/target/Web-page-for-customer-0.0.1-SNAPSHOT.jar app.jar
#
## ===== Declare ARGs in final stage =====
#ARG DB_URL
#ARG DB_USERNAME
#ARG DB_PASSWORD
#ARG JWT_SECRET
#ARG GOOGLE_CLIENT_ID
#ARG GOOGLE_CLIENT_SECRET
#ARG COOKIE_SECURE=false
#ARG CORS_ALLOWED_ORIGINS=http://localhost:4200
#
## ===== Map ARG -> ENV so Spring reads them at runtime =====
#ENV DB_URL=${DB_URL} \
#    DB_USERNAME=${DB_USERNAME} \
#    DB_PASSWORD=${DB_PASSWORD} \
#    JWT_SECRET=${JWT_SECRET} \
#    GOOGLE_CLIENT_ID=${GOOGLE_CLIENT_ID} \
#    GOOGLE_CLIENT_SECRET=${GOOGLE_CLIENT_SECRET} \
#    COOKIE_SECURE=${COOKIE_SECURE} \
#    CORS_ALLOWED_ORIGINS=${CORS_ALLOWED_ORIGINS}
#
#EXPOSE 8386
#
## Tuỳ chọn: giới hạn RAM JVM (đổi cho phù hợp)
#ENV JAVA_OPTS="-Xms256m -Xmx512m"
#
## Chạy ứng dụng
#ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]

