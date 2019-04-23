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
ENV port 8090
ENV profile manager
ENV oauth_client_id ""
ENV oauth_client_secret ""
ENV db_host "postgres"
ENV db_name "diplomDb"
ENV db_user "diplom"
ENV db_pass "diplom"
ENV db_port 5432
ENV rabbit_user "guest"
ENV rabbit_pass "guest"
ENV rabbit_service_id "rabbitmq"
ENV consul_host "client-consul"

CMD  ["java","-jar","./Diplom.jar", "--server.port=${port}","--spring.profiles.active=${profile}","--security.oauth2.client.clientId=${oauth_client_id}", "--security.oauth2.client.clientSecret=${oauth_client_secret}","--app.db.host=${db_host}","--app.db.name=${db_name}","--app.db.user=${db_user}","--app.db.pass=${db_pass}","--app.db.port=${db_port}", "--rabbit.user=${rabbit_user}","--rabbit.pass=${rabbit_pass}","--rabbit.service.id=${rabbit_service_id}", "--spring.cloud.consul.host=${consul_host}"]