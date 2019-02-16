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
ENTRYPOINT ["java","-jar","/Diplom.jar"]