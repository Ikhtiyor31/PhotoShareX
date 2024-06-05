ARG JAVA_VERSION=17
FROM amazoncorretto:${JAVA_VERSION} as Build
COPY . /app
WORKDIR /app

#build time env variables
ARG GOOGLE_APPLICATION_CREDENTIALS
ARG MAIL_ADDRESS
ENV GOOGLE_APPLICATION_CREDENTIALS $GOOGLE_APPLICATION_CREDENTIALS
ENV MAILADDRESS $MAIL_ADDRESS

RUN echo "$GOOGLE_APPLICATION_CREDENTIALS" | base64 --decode > credentials.json

RUN ./gradlew --no-daemon build

FROM amazoncorretto:${JAVA_VERSION} as platform
ARG BUILD_JAR_PATH=build/libs/PhotoShareX-1.0.0.jar
COPY --from=Build /app/${BUILD_JAR_PATH} .

#copy again credentials
COPY --from=Build /app/credentials.json .

ENV PORT 8080
EXPOSE $PORT

ENTRYPOINT ["java", "-Xms1024m", "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}", "-jar", "-Dserver.port=${PORT}", "PhotoShareX-1.0.0.jar"]