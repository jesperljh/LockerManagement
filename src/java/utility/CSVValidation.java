package utility;

import java.util.ArrayList;

/**
 * CSVValidation is used to contain validation methods needed in processing of CSV files
 * @author Jiacheng, Jing Xiang
 */
public class CSVValidation {



    /**
     * Trim each individual field - removing front and back spaces
     * @param line the line to be trimmed
     * @return String[] a string array of revised line
     */
    public static String[] trimFields(String[] line) {
        String[] revisedLine = new String[line.length];
        for (int n = 0; n < line.length; n++) {
            revisedLine[n] = line[n].trim(); // to make sure that all white spaces are cleared
        }
        return revisedLine;
    }
    
    /**
     * Validate whether each field is a blank field or not
     * @param line the line to be validated
     * @return ArrayList<Integer> list of indexes which corresponds to each line and its field headers
     */
    public static ArrayList<Integer> validateBlankFields(String[] line) {
        ArrayList<Integer> errors = new ArrayList<Integer>();

        for (int n = 0; n < line.length; n++) {
            if (line[n].length() == 0) {
                errors.add(n);
            }
        }
        return errors;
    }
}
