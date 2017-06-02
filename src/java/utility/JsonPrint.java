package utility;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.util.LinkedHashMap;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 * This class helps us to pretty print json output
 * @author Jiacheng
 * @version 1.0
 */
public class JsonPrint {

    /**
     * Reads a LinkedHashMap and changes it into pretty printed Json Format
     * @param resultsMap Contains results
     * @return pretty printed json 
     */
    public String prettyPrint(LinkedHashMap<String, Object> resultsMap) {
        
        // Instantiate a JSONObject
        JSONObject obj = new JSONObject();
        
        // Put all results in JSONValue (as per map order)
        String jsonResponse = JSONValue.toJSONString(resultsMap);
        
        // Initialize the Gson object for pretty printing
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(jsonResponse);
        String prettyJsonString = gson.toJson(je);
        
        // Return pretty String to print
        return prettyJsonString;
    }
}
