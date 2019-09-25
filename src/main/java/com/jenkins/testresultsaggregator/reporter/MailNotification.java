package com.jenkins.testresultsaggregator.reporter;

import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.jenkins.testresultsaggregator.data.DataDTO;
import com.jenkins.testresultsaggregator.data.DataJobDTO;
import com.jenkins.testresultsaggregator.data.JobStatus;

import hudson.model.BuildListener;

public class MailNotification {
	
	private BuildListener listener;
	private List<DataDTO> dataJob;
	
	public MailNotification(BuildListener listener, List<DataDTO> dataJob) {
		this.listener = listener;
		this.dataJob = dataJob;
	}
	
	private boolean validateResults() {
		boolean allJobsNotFound = true;
		for (DataDTO tempDataDTO : dataJob) {
			for (DataJobDTO tempDataJobDTO : tempDataDTO.getJobs()) {
				if (!JobStatus.NOT_FOUND.name().equals(tempDataJobDTO.getJenkinsBuild().getResult())) {
					allJobsNotFound = false;
					break;
				}
			}
		}
		return allJobsNotFound;
	}
	
	public void send(String mailTo, String mailFrom, String subject, String body, String host) {
		listener.getLogger().println("Generate email Report");
		if (validateResults()) {
			listener.getLogger().println("No mail will be Send since all Jobs are having status NOT_FOUND");
		} else {
			String[] to = mailTo.split(",");
			Properties properties = System.getProperties();
			properties.setProperty("mail.smtp.host", host);
			Session session = Session.getDefaultInstance(properties);
			try {
				MimeMessage message = new MimeMessage(session);
				message.setFrom(new InternetAddress(mailFrom));
				for (String recipient : to) {
					message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
				}
				message.setSubject(subject);
				message.setContent(body, "text/html");
				Transport.send(message);
				listener.getLogger().println("Sent message successfully....to : " + mailTo);
			} catch (MessagingException ex) {
				listener.getLogger().printf("Error Occurred : %s ", ex);
			}
		}
	}
}
