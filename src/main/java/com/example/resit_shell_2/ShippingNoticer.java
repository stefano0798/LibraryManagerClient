package com.example.resit_shell_2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class ShippingNoticer {

	@Value("${queue.response}")
	private String responseQueue;

	@Autowired
	private JmsTemplate jmsTemplate;

	@JmsListener(destination = "response")
	public void pickUpBook(String requestMsg) {

		String isbn = requestMsg.split(":")[1];

		System.out.println("The user picked up: book " + isbn);

	}

}
