package com.solace.integration.jcsmp;

import java.util.concurrent.CountDownLatch;

import com.solacesystems.jcsmp.BytesXMLMessage;
import com.solacesystems.jcsmp.JCSMPException;
import com.solacesystems.jcsmp.JCSMPFactory;
import com.solacesystems.jcsmp.JCSMPProperties;
import com.solacesystems.jcsmp.JCSMPSession;
import com.solacesystems.jcsmp.TextMessage;
import com.solacesystems.jcsmp.Topic;
import com.solacesystems.jcsmp.XMLMessageConsumer;
import com.solacesystems.jcsmp.XMLMessageListener;

public class TopicSubscriber {

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

		// Create a JCSMP Session
		final JCSMPProperties properties = new JCSMPProperties();
		properties.setProperty(JCSMPProperties.HOST, HOST);
		properties.setProperty(JCSMPProperties.VPN_NAME, VPN_NAME);
		properties.setProperty(JCSMPProperties.USERNAME, USERNAME);
		properties.setProperty(JCSMPProperties.PASSWORD, PASSWORD);
		
		final Topic topic = JCSMPFactory.onlyInstance().createTopic("tutorial/topic");
		final JCSMPSession session = JCSMPFactory.onlyInstance().createSession(properties);
		session.connect();

		final CountDownLatch latch = new CountDownLatch(1); // used for
															// synchronizing b/w threads
		/**
		 * Anonymous inner-class for MessageListener This demonstrates the async
		 * threaded message callback
		 */
		final XMLMessageConsumer cons = session.getMessageConsumer(new XMLMessageListener() {
			@Override
			public void onReceive(BytesXMLMessage msg) {
				if (msg instanceof TextMessage) {
					System.out.printf("TextMessage received: '%s'%n", ((TextMessage) msg).getText());
				} else {
					System.out.println("Message received.");
				}
				System.out.printf("Message Dump:%n%s%n", msg.dump());
				latch.countDown(); // unblock main thread
			}

			@Override
			public void onException(JCSMPException e) {
				System.out.printf("Consumer received exception: %s%n", e);
				latch.countDown(); // unblock main thread
			}
		});
		session.addSubscription(topic);
		System.out.println("Connected. Awaiting message...");
		cons.start();
		// Consume-only session is now hooked up and running!

		try {
			latch.await(); // block here until message received, and latch will flip
		} catch (InterruptedException e) {
			System.out.println("I was awoken while waiting");
		}
		// Close consumer
		cons.close();
		System.out.println("Exiting.");
		session.closeSession();
	}
}
