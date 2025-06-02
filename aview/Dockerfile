FROM sbtscala/scala-sbt:eclipse-temurin-23.0.2_7_1.10.11_3.3.5 AS build

WORKDIR /app

# Install GUI dependencies for X11 forwarding macOS
RUN apt-get update && apt-get install -y curl gnupg2 x11-apps
RUN apt-get update && apt-get install -y \
    libxext6 \
    libxrender1 \
    libxtst6 \
    libxi6 \
    libxrandr2 \
    libgtk-3-0

COPY . .

RUN sbt aview/compile

EXPOSE 8080

CMD ["sbt", "aview/run"]
