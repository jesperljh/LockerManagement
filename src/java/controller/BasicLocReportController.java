package controller;

import dao.DemographicsDAO;
import entity.Demographics;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import utility.DateTimeUtility;
import utility.DateValidation;
import utility.ErrorMessage;
import utility.TokenValidation;

/**
 * BasicLocReportController class contains methods that will be used to retrieve
 * the breakdown of the number of people in the SIS within the indicated time
 * span
 *
 * @author Jiacheng/Jocelyn
 */
public class BasicLocReportController {

    /**
     * Retrieves a LinkedHashMap of the Breakdown of Demographics in SIS within
     * the time span indicated
     *
     * @return LinkedHashMap Returns a LinkedHashMap of Breakdown Results
     * consisting of status, breakdown and totalDemographics
     * @param dateTime Specifies the time span window's end date time to retrieve
     * the Basic Location Report results
     * @param token Specifies the token, to be validated
     * @param order The order of breakdown of the demographics.
     * @param fromJson This will be set to true if it is called by the Json Web
     * Services. If it is true, then token validation will ensure it comes from
     * the admin.
     */
    public LinkedHashMap<String, Object> getBasicLocReport(String dateTime, String[] order, String token, boolean fromJson) {

        // Declare Hashmap to be returned as results
        LinkedHashMap<String, Object> linkedHashmapResult = new LinkedHashMap<String, Object>();

        // Declare and initialize messages to store any form of error messages
        ArrayList<String> messages = new ArrayList<String>();

        // Set secondDateTime (15 minutes earlier)
        String secondDateTime = null;

        // Create an ArrayList<String> that contains all the valid breakdown
        ArrayList<String> validateBreakdown = new ArrayList<String>();
        validateBreakdown.add("year");
        validateBreakdown.add("gender");
        validateBreakdown.add("school");

        // Validate token
        if (token == null) {
            messages.add(ErrorMessage.getMsg("missingToken"));
        } else if (token.isEmpty()) {
            messages.add(ErrorMessage.getMsg("blankToken"));
        } else {
            if (fromJson) {
                if (!TokenValidation.validateTokenWithUsername(token, "admin")) {
                    messages.add(ErrorMessage.getMsg("invalidToken"));
                }
            } else {
                if (!TokenValidation.validateToken(token)) {
                    messages.add(ErrorMessage.getMsg("invalidToken"));
                }
            }
        }

        //Checks for empty or null breakdown order
        if (order == null) {
            messages.add(ErrorMessage.getMsg("missingOrder"));
        } else if (order.length == 0) {
            messages.add(ErrorMessage.getMsg("blankOrder"));
        } else {

            //To check if order Y/G/S is same as the Y/G/S in the validateBreakdown
            if (order.length == 1) {
                if (!validateBreakdown.contains(order[0])) {
                    messages.add(ErrorMessage.getMsg("invalidOrder"));
                }
            } else if (order.length == 2) {
                if (!validateBreakdown.contains(order[0]) || !validateBreakdown.contains(order[1])) {
                    messages.add(ErrorMessage.getMsg("invalidOrder"));

                    //To check same input or not Y/Y
                }
                if (order[0].equals(order[1])) {
                    messages.add(ErrorMessage.getMsg("invalidOrder"));
                }
            } else if (order.length == 3) {
                if (!validateBreakdown.contains(order[0]) || !validateBreakdown.contains(order[1]) || !validateBreakdown.contains(order[2])) {
                    messages.add(ErrorMessage.getMsg("invalidOrder"));

                    //To check same input or not Y/Y/- or -/Y/Y or Y/-/Y
                }
                if (order[0].equals(order[1]) || order[1].equals(order[2]) || order[0].equals(order[2])) {
                    messages.add(ErrorMessage.getMsg("invalidOrder"));
                }
            }
        }

        // Check for empty datetime, or null datetime
        if (dateTime == null) {

            //Check for missing date
            messages.add(ErrorMessage.getMsg("missingDate"));
        } else if (dateTime.length() == 0) {

            //Check for blank date
            messages.add(ErrorMessage.getMsg("blankDate"));

        } else {

            // Validate that dateTime parsed is of valid format
            if (!DateValidation.validateDateFormat(dateTime)) {
                messages.add(ErrorMessage.getMsg("invalidDate"));
            } else {

                // If dateTime is of valid format, replace T with white space
                dateTime = dateTime.replace('T', ' ');

                // Instantiate secondDateTime which is 15 minutes before the dateTime parsed in
                secondDateTime = DateTimeUtility.getTimeBefore(dateTime, 15);
            }
        }

        //If messages size is bigger than 0, means there is an error, and we should display error messages
        if (messages.size() > 0) {

            // sort the errors according to alphabetical order
            Collections.sort(messages);

            // Put status as error
            linkedHashmapResult.put("status", "error");

            // Put the error messages into ArrayList<String> messages
            linkedHashmapResult.put("messages", messages);

            //Return immediately, as there is no longer a need to process further
            return linkedHashmapResult;
        }

        /*
         ****************************************************************************
         * At this point, all inputs are verified and are ready to retrieve results *
         ****************************************************************************
         */
        //Declare ArrayList of LinkedHashMap to store Basic Location Report data
        ArrayList<LinkedHashMap<String, Object>> firstResultList = new ArrayList<LinkedHashMap<String, Object>>();

        //Declare and initialize and locationLookupDAO
        DemographicsDAO demographicsDAO = new DemographicsDAO();

        // Retrieve an ArrayList of Demographics Object that fits the timeframe that the user input
        ArrayList<Demographics> demographicsList = demographicsDAO.getBasicLocReport(dateTime, secondDateTime);

        // Instantiate the total eligible demographics size
        int demographicsListSize = demographicsList.size();

        // Break down the demographicsList according to the break down order specified by the user
        if (order.length == 1) {
            // Breaks down the demographicsList
            firstResultList = breakdown(demographicsList, order[0]);

            // For each break down, replace the ArrayList of Demographics object with its size to be displayed
            for (LinkedHashMap<String, Object> resultMap : firstResultList) {
                ArrayList<Demographics> mapDemoList = (ArrayList<Demographics>) resultMap.get("count");
                resultMap.put("count", mapDemoList.size());
            }
        } else if (order.length == 2) {
            // Initializing the break downs specified by the user
            String firstBreakdown = order[0];
            String secondBreakdown = order[1];

            // First breakdown
            firstResultList = breakdown(demographicsList, firstBreakdown);

            for (LinkedHashMap<String, Object> firstResultMap : firstResultList) {
                // For each break down in first order, replace the ArrayList of Demographics object with its size to be displayed
                ArrayList<Demographics> firstMapDemoList = (ArrayList<Demographics>) firstResultMap.get("count");
                firstResultMap.put("count", firstMapDemoList.size());

                // For each break down in second order, replace the ArrayList of Demographics object with its size to be displayed
                ArrayList<LinkedHashMap<String, Object>> secondResultList = breakdown(firstMapDemoList, secondBreakdown);
                for (LinkedHashMap<String, Object> secondResultMap : secondResultList) {
                    ArrayList<Demographics> secondMapDemoList = (ArrayList<Demographics>) secondResultMap.get("count");
                    secondResultMap.put("count", secondMapDemoList.size());
                }
                firstResultMap.put("breakdown", secondResultList);
            }

        } else if (order.length == 3) {
            String firstBreakdown = order[0];
            String secondBreakdown = order[1];
            String thirdBreakdown = order[2];

            firstResultList = breakdown(demographicsList, firstBreakdown);

            for (LinkedHashMap<String, Object> firstResultMap : firstResultList) {

                // For each break down in first order, replace the ArrayList of Demographics object with its size to be displayed
                ArrayList<Demographics> firstMapDemoList = (ArrayList<Demographics>) firstResultMap.get("count");
                firstResultMap.put("count", firstMapDemoList.size());

                ArrayList<LinkedHashMap<String, Object>> secondResultList = breakdown(firstMapDemoList, secondBreakdown);

                for (LinkedHashMap<String, Object> secondResultMap : secondResultList) {
                    // For each break down in second order, replace the ArrayList of Demographics object with its size to be displayed
                    ArrayList<Demographics> secondMapDemoList = (ArrayList<Demographics>) secondResultMap.get("count");

                    secondResultMap.put("count", secondMapDemoList.size());
                    ArrayList<LinkedHashMap<String, Object>> thirdResultList = breakdown(secondMapDemoList, thirdBreakdown);

                    // For each break down in third order, replace the ArrayList of Demographics object with its size to be displayed
                    for (LinkedHashMap<String, Object> thirdResultMap : thirdResultList) {
                        ArrayList<Demographics> thirdMapDemoList = (ArrayList<Demographics>) thirdResultMap.get("count");
                        thirdResultMap.put("count", thirdMapDemoList.size());
                    }
                    secondResultMap.put("breakdown", thirdResultList);
                }
                firstResultMap.put("breakdown", secondResultList);
            }

        }

        // Storing of status result into hash map to be returned
        linkedHashmapResult.put("status", "success");
        // Storing of heatmap result into hash map to be returned
        linkedHashmapResult.put("breakdown", firstResultList);
        linkedHashmapResult.put("totalDemographics", demographicsListSize);

        return linkedHashmapResult;
    }

    /**
     * Breakdowns the Demographics by their Gender
     *
     * @return ArrayList Returns an ArrayList of
     * LinkedHashMap which consist of maleMap and femaleMap.
     * @param demographicsList The ArrayList of Demographics to be broken down
     * by gender (M or F)
     */
    public ArrayList<LinkedHashMap<String, Object>> sortGender(ArrayList<Demographics> demographicsList) {

        // Create an arraylist to store the return results 
        ArrayList<LinkedHashMap<String, Object>> resultList = new ArrayList<LinkedHashMap<String, Object>>();

        // Declare LinkedHashMap to store gender and count for male
        LinkedHashMap<String, Object> maleMap = new LinkedHashMap<String, Object>();
        maleMap.put("gender", "M");

        // Create an arraylist to store all demographics for the male
        ArrayList<Demographics> maleList = new ArrayList<Demographics>();

        // Declare LinkedHashMap to store gender and count for female
        LinkedHashMap<String, Object> femaleMap = new LinkedHashMap<String, Object>();
        femaleMap.put("gender", "F");

        // Create an arraylist to store all demographics for the male
        ArrayList<Demographics> femaleList = new ArrayList<Demographics>();

        // Add demographics into list based on the gender
        for (Demographics d : demographicsList) {
            if (d.getGender().equals("M")) {
                maleList.add(d);
            }
            if (d.getGender().equals("F")) {
                femaleList.add(d);
            }
        }

        // Put the demographics lists into their LinkedHashMap respectively
        maleMap.put("count", maleList);
        femaleMap.put("count", femaleList);

        // Return an arraylist of LinkedHashMap for result of this breakdown
        resultList.add(maleMap);
        resultList.add(femaleMap);
        return resultList;
    }

    /**
     * Breakdowns the Demographics by their Year
     *
     * @return ArrayList Returns an ArrayList of
     * LinkedHashMap which consist of map2010, map2011, map2012, map2013,
     * map2014
     * @param demographicsList The ArrayList of Demographics to be broken down
     * by their year (2010 - 2014)
     */
    public ArrayList<LinkedHashMap<String, Object>> sortYear(ArrayList<Demographics> demographicsList) {
        //Create an arraylist to store the return results 
        ArrayList<LinkedHashMap<String, Object>> resultList = new ArrayList<LinkedHashMap<String, Object>>();

        //Declare linkedhashmaps to store year and count(later)
        LinkedHashMap<String, Object> map2010 = new LinkedHashMap<String, Object>();
        map2010.put("year", 2010);
        ArrayList<Demographics> list2010 = new ArrayList<Demographics>();

        LinkedHashMap<String, Object> map2011 = new LinkedHashMap<String, Object>();
        map2011.put("year", 2011);
        ArrayList<Demographics> list2011 = new ArrayList<Demographics>();

        LinkedHashMap<String, Object> map2012 = new LinkedHashMap<String, Object>();
        map2012.put("year", 2012);
        ArrayList<Demographics> list2012 = new ArrayList<Demographics>();

        LinkedHashMap<String, Object> map2013 = new LinkedHashMap<String, Object>();
        map2013.put("year", 2013);
        ArrayList<Demographics> list2013 = new ArrayList<Demographics>();

        LinkedHashMap<String, Object> map2014 = new LinkedHashMap<String, Object>();
        map2014.put("year", 2014);
        ArrayList<Demographics> list2014 = new ArrayList<Demographics>();

        //Add demographics into list based on the year
        for (Demographics d : demographicsList) {
            if (d.getYear() == 2010) {
                list2010.add(d);
            }
            if (d.getYear() == 2011) {
                list2011.add(d);
            }
            if (d.getYear() == 2012) {
                list2012.add(d);
            }
            if (d.getYear() == 2013) {
                list2013.add(d);
            }
            if (d.getYear() == 2014) {
                list2014.add(d);
            }
        }

        //After adding then can now insert the list for counting later
        map2010.put("count", list2010);
        map2011.put("count", list2011);
        map2012.put("count", list2012);
        map2013.put("count", list2013);
        map2014.put("count", list2014);

        //returning arraylist of linkedhashmap for our results
        resultList.add(map2010);
        resultList.add(map2011);
        resultList.add(map2012);
        resultList.add(map2013);
        resultList.add(map2014);
        return resultList;
    }

    /**
     * Breakdowns the Demographics by their School
     *
     * @return ArrayList Returns an ArrayList of
     * LinkedHashMap which consist of accountancyMap, businessMap, sisMap,
     * lawMap, economicsMap, socscMap
     * @param demographicsList The ArrayList of Demographics to be broken down
     * by their schools (accountancy, business, economics, law, sis, socsc)
     */
    public ArrayList<LinkedHashMap<String, Object>> sortSchool(ArrayList<Demographics> demographicsList) {
        //Create an arraylist to store the return results 
        ArrayList<LinkedHashMap<String, Object>> resultList = new ArrayList<LinkedHashMap<String, Object>>();

        //Declare linkedhashmaps to store school and count(later)
        LinkedHashMap<String, Object> businessMap = new LinkedHashMap<String, Object>();
        businessMap.put("school", "business");
        //Create an arraylist to store all demographics for the particular school
        ArrayList<Demographics> businessList = new ArrayList<Demographics>();

        LinkedHashMap<String, Object> accountancyMap = new LinkedHashMap<String, Object>();
        accountancyMap.put("school", "accountancy");
        ArrayList<Demographics> accountancyList = new ArrayList<Demographics>();

        LinkedHashMap<String, Object> sisMap = new LinkedHashMap<String, Object>();
        sisMap.put("school", "sis");
        ArrayList<Demographics> sisList = new ArrayList<Demographics>();

        LinkedHashMap<String, Object> economicsMap = new LinkedHashMap<String, Object>();
        economicsMap.put("school", "economics");
        ArrayList<Demographics> economicsList = new ArrayList<Demographics>();

        LinkedHashMap<String, Object> lawMap = new LinkedHashMap<String, Object>();
        lawMap.put("school", "law");
        ArrayList<Demographics> lawList = new ArrayList<Demographics>();

        LinkedHashMap<String, Object> socscMap = new LinkedHashMap<String, Object>();
        socscMap.put("school", "socsc");
        ArrayList<Demographics> socscList = new ArrayList<Demographics>();

        //Add demographics into list based on the school
        for (Demographics d : demographicsList) {
            if (d.getSchool() != null) {
                if (d.getSchool().equals("business")) {
                    businessList.add(d);
                }
                if (d.getSchool().equals("accountancy")) {
                    accountancyList.add(d);
                }
                if (d.getSchool().equals("sis")) {
                    sisList.add(d);
                }
                if (d.getSchool().equals("economics")) {
                    economicsList.add(d);
                }
                if (d.getSchool().equals("law")) {
                    lawList.add(d);
                }
                if (d.getSchool().equals("socsc")) {
                    socscList.add(d);
                }
            }
        }

        //After adding then can now insert the list for counting later
        businessMap.put("count", businessList);
        accountancyMap.put("count", accountancyList);
        sisMap.put("count", sisList);
        economicsMap.put("count", economicsList);
        lawMap.put("count", lawList);
        socscMap.put("count", socscList);

        //returning arraylist of linkedhashmap for our results
        resultList.add(accountancyMap);
        resultList.add(businessMap);
        resultList.add(economicsMap);
        resultList.add(lawMap);
        resultList.add(sisMap);
        resultList.add(socscMap);

        return resultList;
    }

    /**
     * Breakdowns the Demographics by the order specified by calling the respective sort(Order) method to breakdown
     *
     * @return ArrayList Returns an ArrayList of
     * LinkedHashMap which consist of results from the indicated breakdown
     * @param demographicsList The ArrayList of Demographics to be broken down
     * by the order specified
     * @param breakdown The specified order to breakdown the ArrayList of Demographics
     */
    public ArrayList<LinkedHashMap<String, Object>> breakdown(ArrayList<Demographics> demographicsList, String breakdown) {
        ArrayList<LinkedHashMap<String, Object>> resultList = new ArrayList<LinkedHashMap<String, Object>>();
        //breakdown for year
        if (breakdown.equals("year")) {
            resultList = sortYear(demographicsList);

            //breakdown for gender
        } else if (breakdown.equals("gender")) {
            resultList = sortGender(demographicsList);

            //breakdown for school
        } else if (breakdown.equals("school")) {
            resultList = sortSchool(demographicsList);
        }
        return resultList;
    }
}
