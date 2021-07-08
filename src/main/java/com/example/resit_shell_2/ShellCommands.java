package com.example.resit_shell_2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.web.client.RestTemplate;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@ShellComponent
public class ShellCommands {

	private final String baseURL = "http://localhost:8080/";

	@Value("${queue.request}")
	private String requestQueue;
	@Value("${queue.response}")
	private String responseQueue;

	@Autowired
	private JmsTemplate jmsTemplate;

	@Autowired
	private RestTemplateBuilder restTemplateBuilder;

	@ShellMethod(value = "Retrieve all books", key = {"books"})
	public String books() {
		RestTemplate tmp = restTemplateBuilder.build();
		return tmp.getForObject(baseURL.concat("book"), String.class);
	}

	@ShellMethod(value = "Retrieve book by ISBN", key = {"book-by-isbn"})
	public String book_by_id(String isbn) {
		RestTemplate tmp = restTemplateBuilder.build();
		return tmp.getForObject(baseURL.concat("book/".concat(isbn)), String.class);
	}

	@ShellMethod(value = "Retrieve all users", key = {"users"})
	public String users() {
		RestTemplate tmp = restTemplateBuilder.build();
		return tmp.getForObject(baseURL.concat("user/"), String.class);
	}

	@ShellMethod(value = "Retrieve user by email", key = {"user-by-email"})
	public String user_by_email(String email) {
		RestTemplate tmp = restTemplateBuilder.build();
		return tmp.getForObject(baseURL.concat("user/".concat(email)), String.class);
	}

	@ShellMethod(value = "Retrieve loans by user email", key = {"loan-by-email"})
	public String loan_by_email(String email) {
		RestTemplate tmp = restTemplateBuilder.build();
		return tmp.getForObject(baseURL.concat("loan/user/".concat(email)), String.class);
	}

	@ShellMethod(value = "Retrieve loan by ISBN", key = {"loan-by-isbn"})
	public String loan_by_isbn(String isbn) {
		RestTemplate tmp = restTemplateBuilder.build();
		return tmp.getForObject(baseURL.concat("loan/book/".concat(isbn)), String.class);
	}

	@ShellMethod(value = "Set shipped ISBN (MQ)", key = {"ship"})
	public String setShipped(String isbn) {
		String addMessage = "shipping:".concat(isbn);
		jmsTemplate.convertAndSend(requestQueue, addMessage);
		return "User notified: the book " + isbn + " is ready to be shipped";
	}

	@ShellMethod(value = "Add user <emailAddress> <name> <surname>", key = {"add-user"})
	public int addUser(String emailAddress, String name, String surname) {
		int flag = 0;
		URL url = null;
		HttpURLConnection con = null;
		String bodyUserJson = "{" +
				"\"emailAddress\": \"" + emailAddress + "\"," +
				"\"name\":\"" + name + "\"," +
				"\"surname\": \"" + surname + "\"" +
				"}";
		try {
			url = new URL("http://localhost:8080/user");
			con = (HttpURLConnection) url.openConnection();
			con.setDoOutput(true);
			con.setRequestMethod("POST");
			con.addRequestProperty("Content-Type", "application/json");
			OutputStream os = con.getOutputStream();
			byte[] input = bodyUserJson.getBytes(StandardCharsets.UTF_8);
			os.write(input);
			flag = con.getResponseCode();
			con.connect();
			System.out.println(bodyUserJson);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	@ShellMethod(value = "Add book <isbn> <title> <author>", key = {"add-book"})
	public int addBook(String isbn, String title, String author) {
		int flag = 0;
		String shipped = "false";
		URL url = null;
		HttpURLConnection con = null;
		String bodyBookJson = "{" +
				"\"isbn\": \"" + isbn + "\"," +
				"\"title\": \"" + title + "\"," +
				"\"author\": \"" + author + "\"," +
				"\"shipped\": \"" + shipped + "\"" +
				"}";

		try {
			url = new URL("http://localhost:8080/book");
			con = (HttpURLConnection) url.openConnection();
			con.setDoOutput(true);
			con.setRequestMethod("POST");
			con.addRequestProperty("Content-Type", "application/json");
			OutputStream os = con.getOutputStream();
			byte[] input = bodyBookJson.getBytes(StandardCharsets.UTF_8);
			os.write(input);
			con.connect();
			flag = con.getResponseCode();
			System.out.println(bodyBookJson);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	@ShellMethod(value = "Add loan <isbn> <emailAddress>", key = {"add-loan"})
	public int addLoan(String isbn, String emailAddress) {
		int flag = 0;
		URL url = null;
		HttpURLConnection con = null;
		String bodyLoanJson = "{\"isbn\":\"" + isbn + "\"," +
				"\"emailAddress\":\"" + emailAddress + "\"}";
		try {
			url = new URL("http://localhost:8080/loan");
			con = (HttpURLConnection) url.openConnection();
			con.setDoOutput(true);
			con.setRequestMethod("POST");
			con.addRequestProperty("Content-Type", "application/json");
			OutputStream os = con.getOutputStream();
			byte[] input = bodyLoanJson.getBytes(StandardCharsets.UTF_8);
			os.write(input);
			con.connect();
			flag = con.getResponseCode();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

}
