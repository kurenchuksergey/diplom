FROM java:8 as build
WORKDIR /workspace/app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

RUN ./mvnw install -DskipTests
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

FROM java:8
VOLUME /tmp
COPY --from=build /workspace/app/target/Diplom.jar /
ENV port=8090
ENV profile=manager
ENV oauth_client_id=""
ENV oauth_client_secret=""


ENTRYPOINT ["java","-jar","./Diplom.jar -Dserver.port=$port -Dspring.profiles.active=$profile -Dsecurity.oauth2.client.clientId=$oauth_client_id -Dsecurity.oauth2.client.clientSecret=$oauth_client_secret"]