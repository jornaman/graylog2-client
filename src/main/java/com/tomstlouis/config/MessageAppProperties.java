package com.tomstlouis.config;
import java.util.*;
import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class MessageAppProperties extends Properties {
	// static variable single_instance of type Singleton
	private static MessageAppProperties single_instance = null;

	private static final Logger logger = LogManager.getLogger(MessageAppProperties.class);

	private final static String[][] defaults = {{"hostname", "Unknown"},
								         {"gelfversion", "1.1"}};
	private final static String hostname = defaults[0][1];
    private final static String GELF_VERSION = defaults[0][1];

	private MessageAppProperties() {
		setProperty(defaults[0][0], getHost());
		setProperty(defaults[1][0], defaults[1][1]);
	}

	private MessageAppProperties(String fileName) throws Exception {
		this();
		this.loadProperties(fileName);
	}

	// static method to create instance of Singleton class
	public static MessageAppProperties getInstance()
	{
		if (single_instance == null)
			single_instance = new MessageAppProperties();

		return single_instance;
	}

	public static MessageAppProperties getInstance(String fileName) throws Exception
	{
		if (single_instance == null)
			single_instance = new MessageAppProperties(fileName);

		return single_instance;
	}
	private String getHost() {
		String hostname = "Unknown";

		try
		{
		    InetAddress addr;
		    addr = InetAddress.getLocalHost();
		    hostname = addr.getHostName();
		}
		catch (UnknownHostException ex)
		{
		    hostname = defaults[0][1];
			logger.error("{}", ex);
		}
		return hostname;
	}
	
	/*
	 * Reads a properties file key value pairs.  Note any property in the file takes precedence over a property already set
	 */
	public void loadProperties(String fileName) throws Exception {
		File props = new File(fileName);
		InputStream input = new FileInputStream(fileName);

		// load a properties file
		this.load(input);
	}

	public static void main(String[] args) throws Exception {
		MessageAppProperties dp = new MessageAppProperties();
		System.out.println("DP: " + dp.toString());
		String fileNme = "/Users/tomstlouis/code/graylog2-client/src/main/resources/MessageApp.properties";
		MessageAppProperties dpii = new MessageAppProperties(fileNme);
		System.out.println("DPii: " + dpii.toString());

	}
}
