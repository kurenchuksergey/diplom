FROM java:8
ADD target/Diplom.jar Diplom.jar
ENTRYPOINT ["/usr/bin/java"]
CMD ["-jar", "Diplom.jar"]
EXPOSE 8090