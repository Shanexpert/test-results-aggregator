package com.jenkins.testresultsaggregator.helper;

import java.util.Properties;

import jakarta.mail.AuthenticationFailedException;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;

public class Validate {
	
	public static boolean confirmSMTP(String host, int port, String username, String password, boolean auth, String enctype) throws AuthenticationFailedException, MessagingException {
		boolean result = false;
		Properties props = new Properties();
		if (auth) {
			props.setProperty("mail.smtp.auth", "true");
		} else {
			props.setProperty("mail.smtp.auth", "false");
		}
		/*if (enctype.endsWith("TLS")) {
			props.setProperty("mail.smtp.starttls.enable", "true");
		} else if (enctype.endsWith("SSL")) {
			props.setProperty("mail.smtp.startssl.enable", "true");
		}*/
		Session session = Session.getInstance(props, null);
		Transport transport = session.getTransport("smtp");
		transport.connect(host, port, username, password);
		transport.close();
		result = true;
		return result;
	}
}
