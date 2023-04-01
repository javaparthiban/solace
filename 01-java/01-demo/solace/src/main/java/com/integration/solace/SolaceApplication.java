package com.integration.solace;

import com.solace.services.core.model.SolaceServiceCredentials;
import com.solacesystems.jcsmp.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@SpringBootApplication

public class SolaceApplication {

	public static void main(String[] args) {
		var ctx = SpringApplication.run(SolaceApplication.class, args);
		Arrays.asList(ctx.getBeanDefinitionNames()).forEach(System.out::println);
	}
	@Component
	static class Runner implements CommandLineRunner {

		private static final Logger logger = LoggerFactory.getLogger(Runner.class);

		private final Topic topic = JCSMPFactory.onlyInstance().createTopic("tutorial/topic");

		@Autowired
		private SpringJCSMPFactory solaceFactory;

		// Examples of other beans that can be used together to generate a customized SpringJCSMPFactory
		@Autowired(required=false) private SpringJCSMPFactoryCloudFactory springJCSMPFactoryCloudFactory;
		@Autowired(required=false) private SolaceServiceCredentials solaceServiceCredentials;
		@Autowired(required=false) private JCSMPProperties jcsmpProperties;

		private DemoMessageConsumer msgConsumer = new DemoMessageConsumer();
		private DemoPublishEventHandler pubEventHandler = new DemoPublishEventHandler();

		public void run(String... strings) throws Exception {
			final String msg = "Hello World";
			final JCSMPSession session = solaceFactory.createSession();

			XMLMessageConsumer cons = session.getMessageConsumer(msgConsumer);

			session.addSubscription(topic);
			logger.info("Connected. Awaiting message...");
			cons.start();

			// Consumer session is now hooked up and running!

			/** Anonymous inner-class for handling publishing events */
			XMLMessageProducer prod = session.getMessageProducer(pubEventHandler);
			// Publish-only session is now hooked up and running!

			TextMessage jcsmpMsg = JCSMPFactory.onlyInstance().createMessage(TextMessage.class);
			jcsmpMsg.setText(msg);
			jcsmpMsg.setDeliveryMode(DeliveryMode.PERSISTENT);

			logger.info("============= Sending " + msg);
			prod.send(jcsmpMsg, topic);

			try {
				// block here until message received, and latch will flip
				msgConsumer.getLatch().await(10, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				logger.error("I was awoken while waiting");
			}
			// Close consumer
			cons.close();
			logger.info("Exiting.");
			session.closeSession();
		}
	}
}
