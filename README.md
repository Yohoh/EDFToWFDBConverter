Konverter von EKG Daten vom EDF Format in WFDB Format
===========

Der Konverter liegt als .jar Datei unter 

```out/artifacts/EDFToWFDBConverter_jar```

Dort liegt auch die bereitgestellte Beispieldatei im EDF Format.

Die Jar kann über folgenden Befehl via Terminal gestartet werden:

```java -jar EDFToWFDBConverter.jar InputName.edf OutputName```

Das Programm lässt sich auch direkt über die Klasse Main ohne Parameter starten. Dann werden die Parameter für Input auf 03215_hr.edf und Output auf 03215_hr gesetzt.
(Deshalb liegt die EDF Datei auch nochmal im Projekt Ordner vor)