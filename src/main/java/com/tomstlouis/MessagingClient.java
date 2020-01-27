package com.tomstlouis;

import com.tomstlouis.config.MessageAppProperties;
import java.io.*;
import java.util.*;

import com.tomstlouis.graylog.GelfMessage;
import com.tomstlouis.message.GelfMessageFactory;
import com.tomstlouis.message.MessageSender;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MessagingClient
{
    private static MessageAppProperties maprop = MessageAppProperties.getInstance();
    private static final Logger logger = LogManager.getLogger(MessagingClient.class);
    private static String help = "-help";
    private static String prop = "-map";
    private static String mfile = "-mfile";
    private static String msg = "-msg";
    private static String clis[] = {prop, mfile, help, msg};
    private static HashMap<String, String> hmap = new HashMap<String, String>();
    private static String helpMsg = "\nUsage: MessagagingClient [-options]\n" +
            "where options include:\n" +
            "-map       path to MessageApp.properties file\n" +
            "-mfile	    path to file of json messages to http send to Graylog\n" +
            "-msg	    a single json message to http send to Graylog\n" +
            "-help	    this help message\n";


    public static void main( String[] args )
    {
        try {
            parseCommands(args);
            processCommands();

        } catch(IOException e){
            logger.error("MessageApp properties Logged !!!", e);
            System.out.println(e.getMessage());
        } catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    public static void help() throws Exception {
        logger.info(helpMsg);
    }

    public static void messageAppProperties() throws Exception {
        logger.info(helpMsg);
    }

    public static void processCommands() throws Exception {

        if(hmap.containsKey(help)) {
            logger.info(helpMsg);
        }

        //segment to process MessageApp.properties
        try {
            if(hmap.containsKey(prop)) {
                getProperties(hmap.get(prop));
            }
        } catch(Exception e){
            logger.error("Exception with MessageApp properties!", e);
            logger.error("A MessageApp.properties file is required to run the application");
            logger.info(helpMsg);
            return;
        }

        //segment to process -msg single json message string
        try {
            if(hmap.containsKey(msg)) {
                if (hmap.get(msg) != null && hmap.get(msg).length() > 0) {
                    processSingleMessage(hmap.get(msg));
                }
            }
        } catch(Exception e){
            logger.error("-msg single message send exception", e);
        }

        try {
            if(hmap.containsKey(mfile)) {
                String fileName = hmap.get(mfile);
                if (fileName != null && fileName.length() > 0) {
                    processSampleMessages(fileName);
                }
            }
        } catch(Exception e){
            logger.error("-mfile process file of sample messages", e);
        }
    }

    public static void parseCommands(String[] parms) {

        if (parms == null || parms.length == 0) {
            logger.error(helpMsg);
            return;
        }

        for (int x = 0; x < clis.length; x++) {
            hmap.put(clis[x], null);
        }
        String key;
        for (int x = 0; x < parms.length; x++) {

            if (hmap.containsKey(parms[x]) && !parms[x].equals("-help")) {
                key = parms[x];
                try {
                    if (parms[++x].charAt(0) != '-' ) {
                        //System.out.println("Attempting key value override: " + parms[x]);
                        hmap.put(key, parms[x]);
                        //System.out.println("Get key value: " + hmap.get(key));
                    } else {
                        IllegalArgumentException iae = new IllegalArgumentException(parms[x]);
                        logger.error("Illegal Parameter value", iae);
                    }
                } catch(ArrayIndexOutOfBoundsException ex) {
                    logger.info(key + " Command option requires a value");
                } catch(Exception ex) {
                    logger.error("Command option requires a value", ex);
                }
            }
        }
    }

    public static void processSampleMessages(String fileName) {
        File fileDir = new File(fileName);
        GelfMessage gm;
        String result = "";
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    new FileInputStream(fileDir), "UTF8"));

            String jsonStr;

            while ((jsonStr = in.readLine()) != null) {
                gm = GelfMessageFactory.makeMessage(jsonStr);
                result = MessageSender.sendHttpMessage(gm);
                logger.info(result);
            }
        } catch(Exception ex) {
            logger.error("MessagingClient send multiple messages exception: ", ex);
        }
    }

    public static void processSingleMessage(String message) {
        try {
            GelfMessage gm = GelfMessageFactory.makeMessage(message);
            String result = MessageSender.sendHttpMessage(gm);
            logger.info(result);
        } catch(Exception ex) {
            logger.error("MessagingClient send single message exception: ", ex);
        }
    }

    public static void getProperties(String fileName) throws Exception {
        maprop.loadProperties(fileName);
        logger.info("Properties" + maprop.toString());
    }

}
