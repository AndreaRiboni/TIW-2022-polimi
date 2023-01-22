package it.polimi.tiw.project.utility;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class StringUtility {
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

    public static boolean isNullOrEmpty(String... strings){
        for(String string : strings)
            if(string == null || string.isEmpty()) return true;
        return false;
    }
}
