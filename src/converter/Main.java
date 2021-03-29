/*
 *****************************************************************************
 * Created by Yannis Heim
 *
 * E-mail Adresse: yohoh2@gmail.com
 *
 ******************************************************************************
 */
package converter;

import EDflib.EDFException;

import java.io.*;

/**
 * Startpunkt der Anwendung zum konvertieren von EKG Daten.
 */
public class Main {

    /**
     * Konvertiert EKG Daten vom EDF Format ins WFDB Format.
     * Die Methode bietet verschiedene Aufrufmöglichkeiten an, je nachdem wie viele Parameter der Startmethode übergeben werden.
     *
     * @param args erster Parameter ist die einzulesende Datei inklusive der .edf Endung, der zweite Parameter ist der Name der Ausgabedatei
     * @throws IOException
     * @throws EDFException
     */
    public static void main(String[] args) throws IOException, EDFException {
        switch (args.length) {
            case 1: {
                String in = args[0];
                int endIndex = in.indexOf(".edf");
                Converter converter = new Converter(in, in.substring(0, endIndex));
                converter.convert();
                break;
            }
            case 2: {
                String in = args[0];
                String out = args[1];
                Converter converter = new Converter(in, out);
                converter.convert();
                break;
            }
            default:
                Converter converter = new Converter("03215_hr.edf", "03215_hr");
                converter.convert();
        }

    }
}
