package converter;

import EDflib.EDFException;

import java.io.*;

public class Main {


    public static void main(String[] args) throws IOException, EDFException {
        if (args.length == 2) {
            String in = args[0];
            String out = args[1];
            Converter converter = new Converter(in, out);
            converter.convert();
        } else if (args.length == 1) {
            String in = args[0];
            int endIndex = in.indexOf(".edf");
            Converter converter = new Converter(in, in.substring(0, endIndex));
            converter.convert();
        } else {
            Converter converter = new Converter("03215_hr.edf", "03215_hr");
            converter.convert();
        }


    }
}
