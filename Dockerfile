ARG JAVA_VERSION=17
FROM amazoncorretto:${JAVA_VERSION} as Build
COPY . /app
WORKDIR /app
# needed to set google_application_credentials while building image
COPY ./src/main/resources/gcp-photosharex-storage-key.json /key/gcp-photosharex-storage-key.json
ENV GOOGLE_APPLICATION_CREDENTIALS=/key/gcp-photosharex-storage-key.json
ENV MAIL_ADDRESS=${MAIL_ADDRESS}


RUN ./gradlew --no-daemon build


FROM amazoncorretto:${JAVA_VERSION} as platform
ARG BUILD_JAR_PATH=build/libs/PhotoShareX-1.0.0.jar
COPY --from=Build /app/${BUILD_JAR_PATH} .
# needed to set google_application_credentials after build succes
COPY ./src/main/resources/gcp-photosharex-storage-key.json /key/gcp-photosharex-storage-key.json
ENV GOOGLE_APPLICATION_CREDENTIALS=/key/gcp-photosharex-storage-key.json
ENV MAIL_ADDRESS=${MAIL_ADDRESS}
ENV PORT 8080
EXPOSE $PORT

ENTRYPOINT ["java", "-Xms1024m", "-Dspring.profiles.active=local", "-jar", "-Dserver.port=${PORT}", "PhotoShareX-1.0.0.jar"]