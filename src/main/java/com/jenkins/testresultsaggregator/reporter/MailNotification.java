package com.jenkins.testresultsaggregator.reporter;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.google.common.base.Strings;
import com.jenkins.testresultsaggregator.data.DataDTO;
import com.jenkins.testresultsaggregator.data.DataJobDTO;
import com.jenkins.testresultsaggregator.data.JobStatus;
import com.jenkins.testresultsaggregator.helper.LocalMessages;

import jenkins.plugins.mailer.tasks.MimeMessageBuilder;

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
	
	public void send(String mailTo, String mailFrom, String subject, String body, String preBodyText, String afterBodyText) throws UnsupportedEncodingException, MessagingException {
		logger.print(LocalMessages.GENERATE.toString() + " " + LocalMessages.EMAIL_REPORT.toString());
		MimeMessageBuilder mimeMessageBuilder = new MimeMessageBuilder();
		if (validateResults()) {
			logger.println(LocalMessages.VALIDATION_MAIL_NOT_FOUND_JOBS.toString());
		} else if (Strings.isNullOrEmpty(mailTo)) {
			logger.println(LocalMessages.VALIDATION_MAIL_RECEIPIENTS_EMPTY.toString());
		} else {
			try {// Add Recipients
				String[] to = mailTo.split(",");
				for (String recipient : to) {
					mimeMessageBuilder.addRecipients(recipient);
				}
				// Add Body before
				StringBuffer messageBody = new StringBuffer();
				if (!Strings.isNullOrEmpty(preBodyText)) {
					messageBody.append(preBodyText);
					messageBody.append("<br></br>");
				}
				// Add Body
				messageBody.append(body);
				// Add Body before and after text
				if (!Strings.isNullOrEmpty(afterBodyText)) {
					messageBody.append("<br></br>");
					messageBody.append(afterBodyText);
				}
				// Set Body
				mimeMessageBuilder.setBody(messageBody.toString());
				// Set Subject
				mimeMessageBuilder.setSubject(subject);
				// Set type
				mimeMessageBuilder.setMimeType("text/html");
				// Build
				MimeMessage message = mimeMessageBuilder.buildMimeMessage();
				message.setFrom(new InternetAddress(mailFrom));
				message.saveChanges();
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
