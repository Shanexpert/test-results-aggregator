package com.jenkins.testresultsaggregator.reporter;

import java.io.PrintStream;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.google.common.base.Strings;
import com.jenkins.testresultsaggregator.data.DataDTO;
import com.jenkins.testresultsaggregator.data.DataJobDTO;
import com.jenkins.testresultsaggregator.data.JobStatus;
import com.jenkins.testresultsaggregator.helper.LocalMessages;

public class MailNotification {
	
	private PrintStream logger;
	private List<DataDTO> dataJob;
	
	public MailNotification(PrintStream logger, List<DataDTO> dataJob) {
		this.logger = logger;
		this.dataJob = dataJob;
	}
	
	private boolean validateResults() {
		boolean allJobsNotFound = true;
		for (DataDTO tempDataDTO : dataJob) {
			for (DataJobDTO tempDataJobDTO : tempDataDTO.getJobs()) {
				if (tempDataJobDTO.getJenkinsBuild() != null && !JobStatus.NOT_FOUND.name().equals(tempDataJobDTO.getJenkinsBuild().getResult())) {
					allJobsNotFound = false;
					break;
				}
			}
		}
		return allJobsNotFound;
	}
	
	public void send(String mailTo, String mailFrom, String subject, String body, String host, String preBodyText, String afterBodyText) {
		logger.print(LocalMessages.GENERATE.toString() + " " + LocalMessages.EMAIL_REPORT.toString());
		if (validateResults()) {
			logger.println(LocalMessages.VALIDATION_MAIL_NOT_FOUND_JOBS.toString());
		} else if (Strings.isNullOrEmpty(mailTo)) {
			logger.println(LocalMessages.VALIDATION_MAIL_RECEIPIENTS_EMPTY.toString());
		} else if (Strings.isNullOrEmpty(host)) {
			logger.println(LocalMessages.VALIDATION_MAIL_SMTP_ISSUE.toString());
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
				// Add Body with before and after text
				StringBuffer messageBody = new StringBuffer();
				if (!Strings.isNullOrEmpty(preBodyText)) {
					messageBody.append(preBodyText);
					messageBody.append("<br></br>");
				}
				messageBody.append(body);
				messageBody.append("<br></br>");
				if (!Strings.isNullOrEmpty(afterBodyText)) {
					messageBody.append(afterBodyText);
				}
				message.setContent(messageBody, "text/html");
				// Add Subject
				message.setSubject(subject);
				Transport.send(message);
				logger.println(LocalMessages.SEND_MAIL_TO.toString());
				logger.println("" + mailTo);
			} catch (MessagingException ex) {
				logger.println("");
				logger.printf(LocalMessages.ERROR_OCCURRED.toString() + ": " + ex.getMessage());
				logger.println("");
			}
		}
	}
}
