package utility;

/**
 * StringFormat changes first letter of a word to upper case
 * @author Kenneth
 */
public class StringFormat {

    
    /**
     * Changes the first letter of a word to upper case
     * @param wordToChange Specifies the word to change (for instance ninja-warriors)
     * @return Returns the word with the first letter changed to upper case, (for instance Ninja-warriors)
     */
    public static String changeFirstLetterUpperCase(String wordToChange) {
        //Only convert first letter to upper case if it's a alphabet (so we check for a to z, and A to Z)
        if (wordToChange.charAt(0) >= 'a' && wordToChange.charAt(0) <= 'z' || wordToChange.charAt(0) >= 'A' && wordToChange.charAt(0) <= 'Z') {
            wordToChange = Character.toUpperCase(wordToChange.charAt(0)) + wordToChange.substring(1);
            
        }
        //Returns word
        return wordToChange;

    }
}
