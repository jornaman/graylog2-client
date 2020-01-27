package com.tomstlouis.message;
//import org.codehaus.jackson.map.ObjectMapper;
import com.tomstlouis.MessagingClient;
import org.apache.http.*;
import com.tomstlouis.graylog.GelfMessage;
import com.tomstlouis.config.MessageAppProperties;
import com.tomstlouis.graylog.GelfMessage;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MessageSender {
    private static MessageAppProperties maprop = MessageAppProperties.getInstance();
    private static final Logger logger = LogManager.getLogger(MessagingClient.class);

    public static String sendHttpMessage(GelfMessage msg) throws Exception {
        String result = "";
        HttpPost post = new HttpPost(maprop.getProperty("graylog.endpoint"));

        StringBuilder json = new StringBuilder();

        StringEntity requestEntity = new StringEntity(
                msg.toString(),
                ContentType.APPLICATION_JSON);

        HttpPost postMethod = new HttpPost("http://example.com/action");
        postMethod.setEntity(requestEntity);

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(post)) {

            result = EntityUtils.toString(response.getEntity());
        }

        return result;
    }

    public static String sendHttpMessage(String msg) throws Exception {
        GelfMessage gm = GelfMessageFactory.makeMessage(msg);
        String result = "";
        HttpPost post = new HttpPost(maprop.getProperty("graylog.endpoint"));

        StringBuilder json = new StringBuilder();

        StringEntity requestEntity = new StringEntity(
                msg,
                ContentType.APPLICATION_JSON);

        HttpPost postMethod = new HttpPost("http://example.com/action");
        postMethod.setEntity(requestEntity);

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(post)) {

            result = EntityUtils.toString(response.getEntity());
        }

        return result;

    }

}
