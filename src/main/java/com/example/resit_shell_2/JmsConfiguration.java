package com.example.resit_shell_2;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;

@Configuration
@EnableJms
public class JmsConfiguration {


	private static final Logger log = org.slf4j.LoggerFactory.getLogger(JmsConfiguration.class);

	@Value("${activemq.broker-url}")
	private String brokerUrl;

	@Value("${activemq.broker-username}")
	private String brokerUsername;

	@Value("${activemq.broker-password}")
	private String brokerPassword;

 	private final ResourceLoader resourceLoader;

	@Autowired
	public JmsConfiguration(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	@Bean
	public ActiveMQConnectionFactory activeMQConnectionFactory() {
		ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory();

		activeMQConnectionFactory.setBrokerURL(brokerUrl);

		log.info("Connect to ActiveMQ host: {}", brokerUrl);

		if (brokerUsername != null && !brokerUsername.isEmpty() && brokerPassword != null && !brokerPassword.isEmpty()) {
			activeMQConnectionFactory.setUserName(brokerUsername);
			activeMQConnectionFactory.setPassword(brokerPassword);
		}

		return activeMQConnectionFactory;
	}

	@Bean
	public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		factory.setConnectionFactory(activeMQConnectionFactory());
		factory.setConcurrency("3-10"); // limit concurrent listener
		factory.setErrorHandler((e) -> {
			log.error("An error occurred while processing the MQ message", e);
		});

		return factory;
	}

}
