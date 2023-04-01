package com.solace.integration.jcsmp.pubsub;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.solacesystems.jcsmp.JCSMPException;
import com.solacesystems.jcsmp.JCSMPFactory;
import com.solacesystems.jcsmp.JCSMPProperties;
import com.solacesystems.jcsmp.JCSMPSession;
import com.solacesystems.jcsmp.JCSMPStreamingPublishCorrelatingEventHandler;
import com.solacesystems.jcsmp.TextMessage;
import com.solacesystems.jcsmp.Topic;
import com.solacesystems.jcsmp.XMLMessageProducer;

/**
 * Deprecated sample, see patterns/DirectPublisher or GuaranteedPublisher
 * instead
 */
public class TopicPublisher {

	private static final Logger log = LogManager.getLogger(TopicPublisher.class);  
	
	// localhost
	private static final String HOST = "tcp://localhost:55555";
	private static final String VPN_NAME = "Parthiban";
	private static final String USERNAME = "admin";
	private static final String PASSWORD = "admin";

	// cloud
//	private static final String HOST = "tcps://mr-connection-ttyxp4di0f5.messaging.solace.cloud:55443";
//	private static final String VPN_NAME = "vpnparthiban";
//	private static final String USERNAME = "solace-cloud-client";
//	private static final String PASSWORD = "oqmcl2vt0cd10b465glemucilv";
		
	public static void main(String... args) throws JCSMPException {

		log.info("TopicPublisher initializing...");

		// Create a JCSMP Session
		final JCSMPProperties properties = new JCSMPProperties();
		properties.setProperty(JCSMPProperties.HOST, HOST);
		properties.setProperty(JCSMPProperties.VPN_NAME, VPN_NAME);
		properties.setProperty(JCSMPProperties.USERNAME, USERNAME);
		properties.setProperty(JCSMPProperties.PASSWORD, PASSWORD);
		
		final JCSMPSession session = JCSMPFactory.onlyInstance().createSession(properties);
		session.connect();
		
		final Topic topic = JCSMPFactory.onlyInstance().createTopic("tutorial/topic");
		log.info("Created Topic Name: " + topic.getName());

		/** Anonymous inner-class for handling publishing events */
		XMLMessageProducer prod = session.getMessageProducer(new JCSMPStreamingPublishCorrelatingEventHandler() {
			@Override
			public void responseReceivedEx(Object key) {
				log.info("Producer received response for msg: " + key.toString());
			}

			@Override
			public void handleErrorEx(Object key, JCSMPException cause, long timestamp) {
				System.out.printf("Producer received error for msg: %s@%s - %s%n", key.toString(), timestamp, cause);
			}
		});
		// Publish-only session is now hooked up and running!

		TextMessage msg = JCSMPFactory.onlyInstance().createMessage(TextMessage.class);
		final String text = "Solace Test Message!";
		msg.setText(text);
		System.out.printf("Connected. About to send message '%s' to topic '%s'...%n", text, topic.getName());
		prod.send(msg, topic);
		log.info("Message sent. Exiting.");
		session.closeSession();
	}
}
