package controller;

import dao.DemographicsDAO;
import entity.Demographics;
import is203.JWTUtility;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import utility.ErrorMessage;
import utility.SharedKey;

/**
 * Login Controller class deals with the authentication of the user
 * @author Jesper
 */
public class LoginController {

    private DemographicsDAO demographicsDAO;

    /**
     * Constructor for LoginController <br>
     * Initializes the DemographicsDAO.
     */
    public LoginController() {
        //Initialize DAO for using later
        demographicsDAO = new DemographicsDAO();
    }

    /**
     * Authenticates the user based on the email and password
     * @return the LinkedHashMap authentication results
     */
    public LinkedHashMap<String, Object> authenticateUser(String email, String password) {
        //Instantiate LinkedHashMap to be returned at the end of function
        LinkedHashMap<String, Object> loginStatusMap = new LinkedHashMap<String, Object>();
        // instantiating the messages to be reverted bac
        ArrayList<String> messages = new ArrayList<String>();

        //Check if email and password is null
        if (email == null || password == null) {
            messages.add(ErrorMessage.getMsg("invalidUserPass"));
            loginStatusMap.put("status", "error");
            loginStatusMap.put("messages",messages);
            return loginStatusMap;
        }

        //Declare demographics object
        Demographics demographics = null;

        //Check if admin credentials matches
        if (email != null && password!= null && email.equals("admin") && password.equals("jesper")) {
            //Create demographics object for admin so he can sign in
            demographics = new Demographics("admin-mac-address", "admin", "jesper", "admin", "M", "admin", "");
        } else {
            //If user did not supply credentials matching that of admin, let's call the DAO to attempt to return the Demographics object            
            demographics = demographicsDAO.retrieve(email, password);
        }

        //If demographics is null, it simply means that either the email OR the password is invalid
        if (demographics == null) {
            //Set status of attempt to error
            loginStatusMap.put("status", "error");


            // put the error message into the message list
            messages.add(ErrorMessage.getMsg("invalidUserPass"));

            loginStatusMap.put("messages", messages);
        } else {
            // demographics is not null, hence the supplied username and password matches a record from the database! (user exists)


            //We are assigning tokens to all users irregardless of web sign in or json sign in.
            //This is because in the pipeline, we may require web signed in users to be allowed to use json requests.
            //Sign token
            String token = JWTUtility.sign(SharedKey.getKey(), email);



            //Set status of attempt to success
            loginStatusMap.put("status", "success");

            //Set token into LinkedHashMap
            loginStatusMap.put("token", token);

            //Set Demographics object into LinkedHashMap
            loginStatusMap.put("user", demographics);

        }
        //Return the LinkedHashMap
        return loginStatusMap;

    }
}
