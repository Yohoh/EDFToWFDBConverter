Konverter von EKG Daten vom EDF Format in WFDB Format
===========

Der Konverter liegt als .jar Datei unter 

```out/artifacts/EDFToWFDBConverter_jar```

Dort liegt auch die von bereitgestellte EDF Datei.

Jar über Terminal starten:

```java -jar EDFToWFDBConverter.jar 03215_hr.edf OutputName```

Programm lässt sich auch direkt über die Klasse Main ohne Parameter starten, dann werden die Parameter für Input auf 03215_hr.edf und Output auf 03215_hr gesetzt.
(Deshalb liegt die EDF Datei auch nochmal im Projekt Ordner)