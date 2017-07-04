package utility;

import is203.JWTException;
import is203.JWTUtility;

/**
 * TokenValidation deals with the validation of token
 * @author Jesper
 */
public class TokenValidation {

    /**
     * Validates token
     * @param token the token to be validated
     * @return returns true if token is valid, false if other
     */
    public static boolean validateToken(String token) {

        //Assume result is true
        boolean result = true;

        try {
            //Verify token based on shared key
            String isVerified = JWTUtility.verify(token, SharedKey.getKey());
            
            if (isVerified == null) {
                //Validation has failed
                result = false;
            }
        } catch (JWTException ex) {
            //Catch exception, hence token is wrong
            result = false;
        }

        return result;

    }
    /**
     * Validates token with username specified. Returns true if successful validation, false if otherwise.
     *
     * @param token The token to be validated against the username
     * @param username Specifies username to be validated against token
     * @return true if token can be validated with specified username, false if otherwise
     */
    public static boolean validateTokenWithUsername(String token,String username) {

        try {
            String isVerified = JWTUtility.verify(token, SharedKey.getKey());
            return isVerified.equals(username);
        } catch (JWTException ex) {
            return false;
        }

    }
}
