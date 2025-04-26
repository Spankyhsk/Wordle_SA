FROM hseeberger/scala-sbt:17.0.9_1.9.7_3.3.1 as build

WORKDIR /app

COPY . .

RUN sbt aview/compile

EXPOSE 8080

CMD ["sbt", "aview/run"]