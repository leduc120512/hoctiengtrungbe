# =====================================================================
# Dockerfile — build multi-stage cho backend "Web hoc tieng Trung"
#   Stage 1 (build) : JDK 21 + Maven Wrapper, bien dich --release 17, dong goi jar
#   Stage 2 (run)   : JRE 21 Alpine, khong chay bang root, image nho
# JDK 21 bien dich duoc --release 17; cau hinh <proc>full</proc> cho Lombok van hop le.
# =====================================================================

# ---------- Stage 1: build ----------
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Copy phan khai bao truoc de Docker cache duoc layer tai dependency
COPY mvnw pom.xml ./
COPY .mvn .mvn
RUN chmod +x mvnw && ./mvnw -B -q dependency:go-offline

# Copy ma nguon va dong goi (bo qua test — test can MySQL that)
COPY src src
RUN ./mvnw -B -q clean package -DskipTests

# ---------- Stage 2: runtime ----------
FROM eclipse-temurin:21-jre-alpine

# User khong dac quyen de chay app
RUN addgroup -S app && adduser -S app -G app
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

USER app
EXPOSE 8080

# Render free (512 MB): gioi han heap theo % RAM container, SerialGC de tiet kiem bo nho
# May chu 512 MB: heap toi da 40% RAM (~200 MB), C1 JIT, SerialGC; vuot bo nho thi thoat de Render khoi dong lai sach.
ENV JAVA_OPTS="-XX:MaxRAMPercentage=40 -XX:ReservedCodeCacheSize=40m -XX:MaxDirectMemorySize=16m -Xss512k -XX:+UseSerialGC -XX:TieredStopAtLevel=1 -XX:+ExitOnOutOfMemoryError"
# Locale UTF-8 de log co tieng Viet / Han tu khong bi in thanh "?" (busybox mac dinh POSIX)
ENV LANG=C.UTF-8 LC_ALL=C.UTF-8

# Dung sh -c de JAVA_OPTS duoc no ra; PORT / DB_* / JWT_SECRET doc tu bien moi truong.
# "exec" de java thay the sh lam PID 1: nhan duoc SIGTERM cua Render / docker stop
# => Spring tat em (dong Hikari pool) thay vi bi kill sau grace period.
ENTRYPOINT ["sh","-c","exec java $JAVA_OPTS -jar app.jar"]
