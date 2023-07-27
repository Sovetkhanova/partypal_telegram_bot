FROM openjdk:11

CMD mkdirs /app/files

ADD ./build/libs/it3-2007-telegram.jar /app/it3-2007-telegram.jar
ENTRYPOINT ["java", "-Duser.timezone=UTC", "-XX:+UseSerialGC", "-Xmx256M","-jar", "/app/it3-2007-telegram.jar"]
EXPOSE 8766