ARG JAVA_VERSION=17
FROM amazoncorretto:${JAVA_VERSION} as Build

COPY . /app
WORKDIR /app

ARG MAIL_ADDRESS
ENV MAIL_ADDRESS $MAIL_ADDRESS

# Securely fetch credentials from GitHub Actions secrets
RUN echo ${{ secrets.GOOGLE_APPLICATION_CREDENTIALS }} > /secrets/credentials.json
RUN chmod 600 /secrets/credentials.json  # Set appropriate permissions
ENV GOOGLE_APPLICATION_CREDENTIALS=/secrets/credentials.json

RUN ./gradlew --no-daemon build

FROM amazoncorretto:${JAVA_VERSION} as platform
ARG BUILD_JAR_PATH=build/libs/PhotoShareX-1.0.0.jar
COPY --from=Build /app/${BUILD_JAR_PATH} .

RUN printenv

ENV PORT 8080
EXPOSE $PORT

ENTRYPOINT ["java", "-Xms1024m", "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}", "-jar", "-Dserver.port=${PORT}", "PhotoShareX-1.0.0.jar"]