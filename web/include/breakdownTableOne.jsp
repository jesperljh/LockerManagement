<%@page import="java.math.BigDecimal"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="utility.StringFormat"%>
<%@page import="java.util.LinkedHashMap"%>
<%@page import="java.util.ArrayList"%>
<%
    //This page is to display tables with simply one breakdown.

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
        <!--Change the category format to be Propercasing-->
        <%= StringFormat.changeFirstLetterUpperCase(order[0])%>: 

        <%
            //Retrieve object so we can check if it's a String or Integer
            Object category = firstMap.get(order[0]);

            //If category is a String
            if (category instanceof String) {
                //Let's change the casing of the String using StringFormat.changeFirstLetterUpperCase();

                //For instance, accounting will be changed to Accounting
                //And ACCOUNTING will be changed to Accounting
                //Cast the category because it is an object and the method only allows String
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
<tr>
    <td>
<center><strong>Count</strong></center>
</td>
<td>
<center>
    <!-- Print out the count of the category -->
    <%
        int mapCount = (Integer) firstMap.get("count");
        if (mapCount != 0) {
            BigDecimal percentage = new BigDecimal(((double)mapCount/totalCount)*100);
    %>
    
    <strong><%=mapCount%> (<%= percentage.setScale(0, BigDecimal.ROUND_HALF_UP)%>%)</strong>
    <%
    } else {
    %>
    <strong><%=mapCount%> (0%)</strong>
    <%
        }
    %>

</center>
</td>

</tr>
</table>
<%
        }
    }
%>