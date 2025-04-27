

# Wordle
 

Wordle ist ein Rätselspiel, bei dem es darum geht, verborgene Wörter zu identifizieren. 
Deine Mission ist klar: Finde das versteckte Wort innerhalb einer limitierten Anzahl an Versuchen.

Bei jedem deiner Versuche erhältst du hilfreiche Hinweise in Form von gefärbten Buchstaben:

🖤Schwarz deutet darauf hin, dass ein Buchstabe nicht im Wort enthalten ist.

💛Gelb verrät dir, dass ein Buchstabe im Wort vorhanden ist, aber noch nicht am richtigen Platz.

💚Grün bestätigt, dass ein Buchstabe korrekt ist und genau dort hingehört.

Möchtest du deine Rätsel-Skills weiter herausfordern? Versuche dich an höheren Schwierigkeitsstufen, bei denen du mehrere Wörter gleichzeitig erraten musst. 

Viel Spaß! 

(Coverage Status hat aktuell einen Bug, bitte einmal auf Coverage Icon klicken um den echten zu sehen)


## Spielmodus Leicht

Das Wordle Original! Du suchst ein Wort und hast dafür sechs Versuche. Das Lösen sollte für dich kein Problem sein, falls doch, empfehlen wir dir noch einmal die Buchstaben zu üben.

<img src="texturengui/screenshotleicht.png" width="300">

## Spielmodus Mittel

Hier wird es schon interessanter. Behältst du den Überblick und kannst zwei Wörter gleichzeitig lösen?

<img src="texturengui/screenshotmittel.png" width="300">

## Spielmodus Schwer

Nur was für Wordle Maestros! Bezwingst du den Wordle Olymp? Falls ja, bist du ein ganz Großer.

<img src="texturengui/screenshotschwer.png" width="300">

## Tests

sbt clean coverage test
sbt coverageReport
(inklusive Testklasse der GUI)
<p>
  <img src="texturengui/coverageReportAktuell1.png" width="300">
</p>
<p>
  <img src="texturengui/coverageReportAktuell2.png" width="300">
</p>



Docker
Jedes Modul kann einzeln in einem Docker Container gestartet werden.
Dafür muss sich im root Verzeichnis befunden werden.
Hier können folgende Befehle ausführen:
```code
docker build -f aview/Dockerfile -t aview-service .
docker build -f controller/Dockerfile -t controller-service .
docker build -f model/Dockerfile -t model-service .
```
Damit werden die Images erstellt.

Damti die Container später im gleichen Netzwerk sind, muss ein Docker Netzwerk erstellt werden.
```code
docker network create wordle-network
```
Die Images können dann gestartet werden.
```code
docker run --name aview-service --network wordle-network -p 8080:8080 aview-service
docker run --name controller-service --network wordle-network -p 8081:8081 controller-service
docker run --name model-service --network wordle-network -p 8082:8082 model-service
```

Wichtig, dass die Ports 8080, 8081 und 8082 nicht schon belegt sind.
Zusätzlich muss darauf geachtet werden, dass die Container die richtigen Namen haben, da über die Namen die APIs aufgerufen werden.

Oder Docker-Compose nutzen.
Mit
```code
docker compose up -d
```
im Projekt-Root, werden alle Images automatisch gebaut und die Container gestartet.
Mit
```code
docker compose down
```
können die Container gestoppt werden, die Container werden damit auch gleich gelöscht, die Images bleiben allerdings bestehen.









 [![Tests](https://github.com/spankyhsk/wordle/actions/workflows/scala.yml/badge.svg)](https://github.com/spankyhsk/wordle/actions/workflows/scala.yml)
 [![Coverage Status](https://coveralls.io/repos/github/Spankyhsk/Wordle/badge.svg?branch=main)](https://coveralls.io/github/Spankyhsk/Wordle?branch=main)
