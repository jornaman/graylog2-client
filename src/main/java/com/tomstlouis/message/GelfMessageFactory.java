package com.tomstlouis.message;

import com.tomstlouis.MessagingClient;
import com.tomstlouis.config.MessageAppProperties;
import com.tomstlouis.graylog.*;
import com.google.gson.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.*;

public class GelfMessageFactory {
    private static final Logger logger = LogManager.getLogger(MessagingClient.class);
    private static MessageAppProperties maprop = MessageAppProperties.getInstance();

    public static GelfMessage makeMessage(String jsonStr) throws Exception {
        String shortMsg, level, host = null, fullMsg = jsonStr;
        GelfMessage gm = null;
        JsonObject obj = null;
        JsonElement status, time;
        int stat = 0;
        Long timeLg = null;
        try {
            obj = new JsonParser().parse(jsonStr).getAsJsonObject();
            if (obj.get("ClientStatus") != null) {
                status = obj.get("ClientStatus");
                stat = Integer.parseInt(status.getAsString());
            }
            if (obj.get("EdgeStartTimestamp") != null) {
                time = obj.get("EdgeStartTimestamp");
                timeLg = time.getAsLong();
            } else { timeLg = System.currentTimeMillis(); }
            if (obj.get("ClientRequestReferer") != null) {
                host = ((JsonElement) obj.get("ClientRequestReferer")).getAsString();
            } else host = maprop.getProperty("hostname");
        } catch(Exception ex) {
            logger.error("Exception setting GelfMessage required attributes",ex);
        }

        if (stat > 0 && stat < 200) {
            level = "6";
            shortMsg = "Http Response Status: Informational responses (100-199)";
        } else if (stat > 199 && stat < 300) {
            level = "6";
            shortMsg = "Http Response Status: Successful responses (200-299)";
        } else if (stat > 299 && stat < 400) {
            level = "4";
            shortMsg = "Http Response Status: Redirects (300-399)";
        } else if (stat > 399 && stat < 500) {
            level = "3";
            shortMsg = "Http Response Status: Client errors (400-499)";
        } else if (stat > 499 && stat < 600) {
            level = "2";
            shortMsg = "Http Response Status: Server errors (500-599)";
        } else {
            level = "7";
            shortMsg = "Default: Debug-level messages";
        }

        try {
            gm = new GelfMessage(shortMsg, fullMsg, timeLg, level);
            gm.setHost(host);

            Set<Map.Entry<String, JsonElement>> set = obj.entrySet();
            Iterator<Map.Entry<String, JsonElement>> iter = set.iterator();
            while (iter.hasNext()) {
                Map.Entry<String, JsonElement> el = iter.next();
                gm.addField(el.getKey(), el.getValue().getAsString());
            }
        } catch(Exception ex) {
            logger.error("Error making GelfMessage object", ex);
            throw ex;
        }

        return gm;
    }
}