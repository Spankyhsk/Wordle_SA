FROM sbtscala/scala-sbt:eclipse-temurin-23.0.2_7_1.10.11_3.3.5 AS build

WORKDIR /app

COPY . .

RUN sbt controller/compile

EXPOSE 8081

CMD ["sbt", "controller/run"]
