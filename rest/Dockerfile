FROM sbtscala/scala-sbt:eclipse-temurin-23.0.2_7_1.10.11_3.3.5 AS build

WORKDIR /app

COPY . .

RUN sbt rest/compile

EXPOSE 8082

CMD ["sbt", "rest/run"]
