<%@page import="java.math.BigDecimal"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="utility.StringFormat"%>
<%@page import="java.util.LinkedHashMap"%>
<%@page import="java.util.ArrayList"%>
<%
                    //This page is to display tables with two breakdown.

    //Store an ArrayList of breakdown    
    ArrayList<LinkedHashMap<String, Object>> firstList = (ArrayList) basicLocationResult.get("breakdown");

    //To store the total number of possible users
    int totalCount = (Integer) basicLocationResult.get("totalDemographics");

    //If the first arraylist of breakdown contains something     
    if (firstList != null && firstList.size() > 0) {

        //Let's iterate through each element  containing
        //[categorySelected] : [value]
        //[count] : [value]
        for (LinkedHashMap<String, Object> firstMap : firstList) {
%>
<table>
    <tr>
    <thead>
    <th colspan="2">
    <center>

        <!-- Printing out the category selected -->
        <%= StringFormat.changeFirstLetterUpperCase(order[0])%>: 

        <%
            //Retrieve object so we can check if it's a String or Integer
            Object category = firstMap.get(order[0]);

            //If category is a String
            if (category instanceof String) {
                //Let's change the casing of the String using StringFormat.changeFirstLetterUpperCase();

                //For instance, accounting will be changed to Accounting
                //And ACCOUNTING will be changed to Accounting
                out.print(StringFormat.changeFirstLetterUpperCase((String) category));
            } else {

                //If category is not string, simply print it out (usually this is things like year)
                out.print(category);
            }


        %> 

    </center>
</th>
</thead>
</tr> 
<%                        //Store an ArrayList of breakdown                
    ArrayList<LinkedHashMap<String, Object>> secondList = (ArrayList) firstMap.get("breakdown");
    //If the second arraylist of breakdown contains something             
    if (secondList != null && secondList.size() > 0) {

        //Let's iterate through each element  containing
        //[categorySelected] : [value]
        //[count] : [value]                    
        for (LinkedHashMap<String, Object> secondMap : secondList) {
            ArrayList<LinkedHashMap<String, Object>> thirdList = (ArrayList) secondMap.get("breakdown");
            //To check if there is a thirdList
            if (thirdList != null && thirdList.size() > 0) {
                //Declare count for rowspan
                int count = 0;
                //Loop the thirdList to get the each linkedhashmap
                for (LinkedHashMap<String, Object> thirdMap : thirdList) {
                    //Convert any objects to String
                    String thirdCategory = thirdMap.get(order[2]).toString();
                    //Check originally String/Integer(2010) -> cannot be converted
                    if (thirdMap.get(order[2]) instanceof String) {
                        //If String, convert to Propercasing.
                        thirdCategory = StringFormat.changeFirstLetterUpperCase(thirdCategory);
                    }
                    //Check if it is the first time looping
                    if (count == 0) {

                        String secondCategory = secondMap.get(order[1]).toString();
                        if (secondMap.get(order[1]) instanceof String) {
                            secondCategory = StringFormat.changeFirstLetterUpperCase(secondCategory);
                        }
                        out.println("<tr>");
                        BigDecimal secondPercentage = new BigDecimal(0);
                        // To get the percentage values
                        int secondMapCount = (Integer) secondMap.get("count");
                        if (secondMapCount != 0) {
                            secondPercentage = new BigDecimal(((double) secondMapCount / totalCount) * 100);
                            secondPercentage = secondPercentage.setScale(0, BigDecimal.ROUND_HALF_UP);
                        }
                        BigDecimal thirdPercentage = new BigDecimal(0);
                        int thirdMapCount = (Integer) thirdMap.get("count");
                        if (thirdMapCount != 0) {
                            thirdPercentage = new BigDecimal(((double) thirdMapCount / totalCount) * 100);
                            thirdPercentage = thirdPercentage.setScale(0, BigDecimal.ROUND_HALF_UP);
                        }

                        //If it is the first time looping, we just add a rowspan to categorize the data
                        out.println("<td rowspan='" + thirdList.size() + "'><center>" + secondCategory + " - Count: " + secondMapCount + " (" + secondPercentage + "%)" + "</center></td>");
                        out.println("<td><center>" + thirdCategory + " - Count: " + thirdMapCount + " (" + thirdPercentage + "%)" + "</center></td>");
                        out.println("</tr>");

                    } else {

                        //To get the percentage values
                        BigDecimal thirdPercentage = new BigDecimal(0);
                        int thirdMapCount = (Integer) thirdMap.get("count");
                        if (thirdMapCount != 0) {
                            thirdPercentage = new BigDecimal(((double) thirdMapCount / totalCount) * 100);
                            thirdPercentage = thirdPercentage.setScale(0, BigDecimal.ROUND_HALF_UP);
                        }
                        //For looping the rest of the records, we do not need a rowspan anymore
                        //as it is attached to the previous rows.
                        out.println("<tr>");
                        out.println("<td><center>" + thirdCategory + " - Count: " + thirdMapCount + " (" + thirdPercentage + "%)" + "</center></td>");
                        out.println("</tr>");

                    }

                    count++;

                }
            }
        }
    }
%>
<tr>
    <td style="background: #36363b; color: white">
<center>
    <b>Total Count</b>
</center>
</td>
<td style="background: #36363b; color: white">
<center>
    <b>
        <!-- Print out the count of the category -->                    
        <%
            BigDecimal firstPercentage = new BigDecimal(0);
            int firstMapCount = (Integer) firstMap.get("count");
            if (firstMapCount != 0) {
                firstPercentage = new BigDecimal(((double) firstMapCount / totalCount) * 100);
                firstPercentage = firstPercentage.setScale(0, BigDecimal.ROUND_HALF_UP);
            }
        %>
        <strong><%=firstMapCount%> (<%=firstPercentage%>%)</strong>
    </b>
</center>
</td>
</tr>

</table>
<%
        }
    }

%>