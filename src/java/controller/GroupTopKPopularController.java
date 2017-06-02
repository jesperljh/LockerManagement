package controller;

import entity.GroupPopularLocation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import utility.DateValidation;
import utility.ErrorMessage;
import utility.TokenValidation;

/**
 * The GroupTopKPopularController makes use of AutomaticGroupDetectionController to retrieve groups <br>
 * and following, finds out what is the most popular place based on the groups
 * @author Kenneth
 */
public class GroupTopKPopularController {

    AutomaticGroupDetectionController agdc;

    /**
     * Constructor for GroupTopKPopularController. Initializes the AutomaticGroupDetectionController here.
     */
    public GroupTopKPopularController() {
        this.agdc = new AutomaticGroupDetectionController();
    }

    /**
     * Returns the top k popular places for groups in a LinkedHashMap
     * @return LinkedHashMap Returns the top k popular places for groups in a LinkedHashMap
     * @param dateTime Specifies the date time and (datetime - 15) period to look for groups
     * @param k Specifies the top k number of groups (1 to 10)
     * @param token Specifies the token to be validated
     * @param fromJson Specifies whether this is being called from a JSON Web Service (if yes, then the user must be an admin)
     */
    public LinkedHashMap getTopKPopularPlaces(String dateTime, String k, String token, boolean fromJson) {

        //instantiate LinkedHashMap to store final result to return
        LinkedHashMap<String, Object> topKPopularPlaces = new LinkedHashMap<String, Object>();

        //instantiate an ArrayList to store error messages (although there is only one validation for date, we would still wan to use an arraylist to capture future additional messages
        ArrayList<String> errorMessageList = new ArrayList<String>();

        // Check for empty datetime, or null datetime
        if (dateTime == null) {

            //Check for missing date
            errorMessageList.add(ErrorMessage.getMsg("missingDate"));
        } else if (dateTime.length() == 0) {

            //Check for blank date
            errorMessageList.add(ErrorMessage.getMsg("blankDate"));

        } else {
            //check for valid date
            if (!DateValidation.validateDateFormat(dateTime)) {
                errorMessageList.add(ErrorMessage.getMsg("invalidDate"));
            }
        }

        //initialize kInt to the default value of 3 for "k"
        int kInt = 3;
        //check if k is empty or null
        if (k != null && k.length() != 0) {
            try {
                //check if k is a valid number
                kInt = Integer.parseInt(k);
                if (kInt < 1 || kInt > 10) {
                    //If number is lesser than 1 or bigger than 10, add error
                    errorMessageList.add(ErrorMessage.getMsg("invalidK"));
                }
            } catch (NumberFormatException nfe) {
                //If number is lesser than 1 or bigger than 10, add error
                errorMessageList.add(ErrorMessage.getMsg("invalidK"));
            }
        }
    
        
         // Validate token
         if (token == null) {
            //Missing token
            errorMessageList.add(ErrorMessage.getMsg("missingToken"));
        } else if (token.isEmpty()) {
            //Empty token
            errorMessageList.add(ErrorMessage.getMsg("blankToken"));
        } else {
            //If this is an admin json web service
            if (fromJson) {
                //Ensure it is an admin user
                if (!TokenValidation.validateTokenWithUsername(token, "admin")) {
                    
                    errorMessageList.add(ErrorMessage.getMsg("invalidToken"));
                }
            } else {
                //came from jsp page, no need to validate against admin
                if (!TokenValidation.validateToken(token)) {
                    errorMessageList.add(ErrorMessage.getMsg("invalidToken"));
                }
            }
        }
         
        //check if error message list is empty. If not empty, we end here
        if (!errorMessageList.isEmpty()) {
            Collections.sort(errorMessageList);
            topKPopularPlaces.put("status", "error");
            topKPopularPlaces.put("messages", errorMessageList);
            return topKPopularPlaces;
        }

        
        //This object is initialized to calculate the semantic place and the number of groups at the place        
        //Data will be stored in this manner
        //[Semantic Place(String] : [Count of Groups (Integer)]
        // For instance,
        //[SMUSISL1LOBBY] : [1]
        //[SMUSISL2LOBBY] : [2]
        //[SMUSISL3LOBBY] : [3]
        LinkedHashMap<String, Object> groupLocationsCount = new LinkedHashMap<String, Object>();
        
        //Get groups from Automatic Group Controller 
        LinkedHashMap<String, Object> agdcResults = agdc.getAutomaticGroupDetection(dateTime, token, true, false);


        //If there are groups detected
        if (agdcResults.get("groups") != null) {
            
            //Retrieve the ArrayList of Groups
            ArrayList<LinkedHashMap<String, Object>> groups = (ArrayList) agdcResults.get("groups");

            //Reiterate through the list of groups
            for (LinkedHashMap<String, Object> group : groups) {
                
                //We are only interested in the last semantic place
                String lastSemanticPlace = (String) group.get("lastSemanticPlace");

                //If groupLocationsCount already have records of the particular semantic place 
                if (groupLocationsCount.get(lastSemanticPlace) != null) {
                    //Get the current count
                    int groupCount = (Integer) groupLocationsCount.get(lastSemanticPlace);
                    //Add 1 to the current count
                    groupCount++;
                    //Add the new integer back into the LinkedHashMap
                    groupLocationsCount.put(lastSemanticPlace, groupCount);
                } else {
                    //If there is no records, then we aill add a new record for the semantic place, with a starting integer of 1
                    groupLocationsCount.put(lastSemanticPlace, 1);
                }

            }
        }
        //Declare an ArrayList of GroupPopularLocation (this entity implements Comparable)       
        ArrayList<GroupPopularLocation> groupPopularLocationList = new ArrayList<GroupPopularLocation>();

      
        //For each record in groupLocationsCount
        for (Map.Entry<String, Object> groupLocationEntry : groupLocationsCount.entrySet()) {
            //Initialize a GroupPopularLocation object
            GroupPopularLocation groupPopularLocation = new GroupPopularLocation(groupLocationEntry.getKey(), Integer.parseInt(groupLocationEntry.getValue().toString()));
            //Add to ArrayList of GroupPopularLocation            
            groupPopularLocationList.add(groupPopularLocation);    

        }
        //Sort the array list by number of groups
        Collections.sort(groupPopularLocationList);

        //Let's create an ArrayList of LinkedHashMap to store popular results
        ArrayList<LinkedHashMap<String, Object>> groupTopPopularResults = new ArrayList<LinkedHashMap<String, Object>>();

        //Set size counter to the biggest value, because later we will be comparing the very first rank against this number
        int sizeCounter = Integer.MAX_VALUE;
        //Start from rank 0
        int rank = 0;
        
        //Reiterate through GroupPopularLocation
        for (GroupPopularLocation groupPopularLocation : groupPopularLocationList) {
            //If sizeCounter is bigger than the current number of groups, let's start the group adding process
            if (sizeCounter > groupPopularLocation.getNumberOfGroups()) {
                //Add rank
                rank++;
                //Set the entity's rank
                groupPopularLocation.setRank(rank);
                //Set the new current size counter
                sizeCounter = groupPopularLocation.getNumberOfGroups();

            } else {
                //This condition will trigger if sizeCounter == groupPopularLocation.getNumberOfGroups()
                //If (sizeCounter == groupPopularLocation.getNumberOfGroups()), it means the previous ranked group has the same number of groups as this current group
                //Hence, we can use the same rank :)
                groupPopularLocation.setRank(rank);
            }
            //We don't need any ranks more than k
            if (rank > Integer.parseInt(k)) {
                break;
            }
            //Create a LinkedHashMap object 
            LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();

            //Store the rank, semantic place, and num groups into the LinkedHashMap for displaying
            result.put("rank", groupPopularLocation.getRank());
            result.put("semantic-place", groupPopularLocation.getSemanticPlace());
            result.put("count", groupPopularLocation.getNumberOfGroups());

            //Add the above results to an ArrayList of LinkedHashMap
            groupTopPopularResults.add(result);
        }
        
        //Yes, it's a success!
        topKPopularPlaces.put("status", "success");
        
        //Store the results
        topKPopularPlaces.put("results", groupTopPopularResults);
        
        //Return the results!
        return topKPopularPlaces;
    }
}
