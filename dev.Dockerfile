FROM openjdk:11

ENV DB_HOST=db
ENV DB_PORT=5432
ENV DB_NAME=postgres
ENV DB_USERNAME=postgres
ENV DB_PASS=postgres

CMD mkdirs /app/files

ADD ./build/libs/it3-2007-telegram.jar /app/it3-2007-telegram.jar
ENTRYPOINT ["java", "-Duser.timezone=UTC", "-XX:+UseSerialGC", "-Xmx256M","-jar", "/app/it3-2007-telegram.jar"]
EXPOSE 8766
