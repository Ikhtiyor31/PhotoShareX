ARG JAVA_VERSION=17
FROM amazoncorretto:${JAVA_VERSION} as Build

COPY . /app
WORKDIR /app

ARG MAIL_ADDRESS
ENV MAIL_ADDRESS $MAIL_ADDRESS

# Copy the credentials file into the Docker image
COPY credentials.json /app/credentials.json
COPY credentials.json /app/src/main/resources/gcp-photosharex-storage-key.json
COPY credentials.json /app/src/test/resources/gcp-photosharex-storage-key.json
ENV GOOGLE_APPLICATION_CREDENTIALS /app/credentials.json

RUN ./gradlew --no-daemon build

FROM amazoncorretto:${JAVA_VERSION} as platform
ARG BUILD_JAR_PATH=build/libs/PhotoShareX-1.0.0.jar
COPY --from=Build /app/${BUILD_JAR_PATH} .
COPY --from=Build /app/credentials.json /app/credentials.json

ENV PORT 8080
EXPOSE $PORT

ENTRYPOINT ["java", "-Xms1024m", "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}", "-jar", "-Dserver.port=${PORT}", "PhotoShareX-1.0.0.jar"]