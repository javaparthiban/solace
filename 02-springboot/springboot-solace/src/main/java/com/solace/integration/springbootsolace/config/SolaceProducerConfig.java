package com.solace.integration.springbootsolace.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.solacesystems.jcsmp.InvalidPropertiesException;
import com.solacesystems.jcsmp.JCSMPSession;
import com.solacesystems.jcsmp.SpringJCSMPFactory;

@Configuration
public class SolaceProducerConfig {
	@Autowired
	private SpringJCSMPFactory solaceFactory;
	
	@Bean
	public JCSMPSession getJCSMPSession() {
		try {
			return solaceFactory.createSession();
		} catch (InvalidPropertiesException e) {
			e.printStackTrace();
		}
		return null;
	}
}
