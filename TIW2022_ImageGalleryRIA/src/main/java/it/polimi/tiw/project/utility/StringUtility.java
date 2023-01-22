package it.polimi.tiw.project.utility;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class StringUtility {

    /**
     * Get the digest of a string (SHA-256)
     * @param string string used to calculate the digest
     * @return digest
     */
    public static String hash(String string){
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(string.getBytes(StandardCharsets.UTF_8));
            String encodedString = Base64.getEncoder().encodeToString(hash);
            return encodedString;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Determines if at least one of the parameters string is null or empty
     * @param strings strings to check
     * @return true if the array contains an empty string or a null value
     */
    public static boolean isNullOrEmpty(String... strings){
        for(String string : strings)
            if(string == null || string.isEmpty()) return true;
        return false;
    }

    /**
     * Convert to UTF-8
     * @param str string to convert
     * @return UTF-8 formatted string
     */
    public static String getUnicode(String str){
        return new String (str.getBytes (StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
    }
}
