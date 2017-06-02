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
<%            //Store an ArrayList of breakdown                
    ArrayList<LinkedHashMap<String, Object>> secondList = (ArrayList) firstMap.get("breakdown");
    //If the second arraylist of breakdown contains something             
    if (secondList != null && secondList.size() > 0) {

        //Let's iterate through each element  containing
        //[categorySelected] : [value]
        //[count] : [value]                    
        for (LinkedHashMap<String, Object> secondMap : secondList) {
%>
<tr>
    <td>
<center>
    <!-- Printing out the category selected -->
    <%= StringFormat.changeFirstLetterUpperCase(order[1])%>: 

    <%
        //Retrieve object so we can check if it's a String or Integer
        Object secondCategory = secondMap.get(order[1]);

        //If secondCategory is a string
        if (secondCategory instanceof String) {
            //Let's change the casing of the String using StringFormat.changeFirstLetterUpperCase();
            //For instance, accounting will be changed to Accounting
            //And ACCOUNTING will be changed to Accounting                                            
            out.print(StringFormat.changeFirstLetterUpperCase((String) secondCategory));
        } else {
            //If category is not string, simply print it out (usually this is things like year)                                         
            out.print(secondCategory);
        }

    %> 
</center>

</td>
<td>
    <!-- Print out the count of the category -->                    
<center>
    <%        
        int mapCount = (Integer) secondMap.get("count");
        if (mapCount != 0) {
            BigDecimal secondPercentage = new BigDecimal(((double) mapCount / totalCount) * 100);
    %>
    <strong><%= mapCount %></strong>
    <strong>(<%= secondPercentage.setScale(0, BigDecimal.ROUND_HALF_UP)%>%)</strong>
    <%
    } else {
    %>
    <strong><%= mapCount %> (0%)</strong>
    <%
        }
    %>
</center>
</td>
</tr>
<%
        }
    }

%>
<tr>
    <td  style="background: #36363b; color: white">
<center>
    <b>
        Total Count
    </b>
</center>
</td>
<td style="background: #36363b; color: white">
<center>
    <!-- Print out the count of the category -->                    
    <b>
        <%            
            int mapCount = (Integer) firstMap.get("count");
            if (mapCount != 0) {
                BigDecimal firstPercentage = new BigDecimal(((double) mapCount / totalCount) * 100);
        %>
        <strong><%=mapCount%> (<%= firstPercentage.setScale(0, BigDecimal.ROUND_HALF_UP)%>%)</strong>
        <%
        } else {
        %>
        <strong><%=mapCount%> (0%)</strong>
        <%
            }
        %>
    </b>
</center>
</td>

</tr>

</table>
<%
        }
    }
%>     