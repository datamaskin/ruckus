package utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by mwalsh on 7/4/14.
 */
public class ParserUtil {

    public static Date getDate(JSONObject event, String name) throws JSONException {
        JSONArray startDateArray = event.getJSONArray(name);
        for (int startDateIndex = 0; startDateIndex < startDateArray.length(); startDateIndex++) {
            JSONObject startDate = startDateArray.getJSONObject(startDateIndex);
            if (startDate.getString("dateType").equalsIgnoreCase("UTC")) {
                try {
                    SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    f.setTimeZone(TimeZone.getTimeZone("UTC"));
                    return f.parse(startDate.getString("full"));
                } catch (ParseException e) {
                    return null;
                }
            }
        }
        return null;
    }

    public static Integer parseSignedInt(String signedInt) {
        try {
            return new DecimalFormat("+#;-#").parse(signedInt).intValue();
        } catch (ParseException e) {
            throw new RuntimeException("Could not format "+signedInt+" as Integer.");
        }
    }
}
