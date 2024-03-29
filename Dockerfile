ARG JAVA_VERSION=17
FROM amazoncorretto:${JAVA_VERSION} as Build
COPY . /app
WORKDIR /app

RUN ./gradlew --no-daemon build

FROM amazoncorretto:${JAVA_VERSION} as platform
ARG BUILD_JAR_PATH=build/libs/PhotoShareX-1.0.0.jar
COPY --from=Build /app/${BUILD_JAR_PATH} .

ENV PORT 8080
EXPOSE $PORT

ENTRYPOINT ["java", "-Xms1024m", "-Dspring.profiles.active=local", "-jar", "-Dserver.port=${PORT}", "PhotoShareX-1.0.0.jar"]