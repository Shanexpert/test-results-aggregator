package com.jenkins.testresultsaggregator.reporter;

import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.google.common.base.Strings;
import com.jenkins.testresultsaggregator.data.Data;
import com.jenkins.testresultsaggregator.data.ImagesMap;
import com.jenkins.testresultsaggregator.data.ImagesMap.ImageData;
import com.jenkins.testresultsaggregator.data.Job;
import com.jenkins.testresultsaggregator.data.JobStatus;
import com.jenkins.testresultsaggregator.helper.LocalMessages;

import hudson.FilePath;
import jenkins.plugins.mailer.tasks.MimeMessageBuilder;

public class MailNotification {
	
	private PrintStream logger;
	private List<Data> dataJob;
	private FilePath workspace;
	
	public MailNotification(PrintStream logger, List<Data> dataJob, FilePath workspace) {
		this.logger = logger;
		this.dataJob = dataJob;
		this.workspace = workspace;
	}
	
	private boolean validateResults() {
		boolean allJobsNotFound = true;
		for (Data tempDataDTO : dataJob) {
			for (Job tempDataJobDTO : tempDataDTO.getJobs()) {
				if (tempDataJobDTO.getBuildInfo() != null && !JobStatus.NOT_FOUND.name().equals(tempDataJobDTO.getBuildInfo().getResult())) {
					allJobsNotFound = false;
					break;
				}
			}
		}
		return allJobsNotFound;
	}
	
	public void send(String mailTo, String mailFrom, String subject, String body, Map<String, ImageData> images, String preBodyText, String afterBodyText) throws MessagingException, IOException {
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
				
				MimeBodyPart messageBodyPart = new MimeBodyPart();
				messageBodyPart.setContent(messageBody.toString(), "text/html");
				Multipart multipart = new MimeMultipart();
				multipart.addBodyPart(messageBodyPart);
				
				if (images != null && !images.isEmpty()) {
					Set<String> setImageID = images.keySet();
					for (String contentId : setImageID) {
						multipart.addBodyPart(addImagePart(contentId));
					}
					message.setContent(multipart);
				}
				// Save Message
				message.saveChanges();
				// Send Message
				Transport.send(message);
				logger.println(LocalMessages.SEND_MAIL_TO.toString());
				logger.println("" + mailTo);
			} catch (MessagingException ex) {
				logger.println("");
				logger.printf(LocalMessages.ERROR_OCCURRED.toString() + ": " + ex.getMessage());
				ex.printStackTrace();
				logger.println("");
			}
		}
	}
	
	private MimeBodyPart addImagePart(String contentId) throws MessagingException, IOException {
		MimeBodyPart imagePart = new MimeBodyPart();
		imagePart.setHeader("Content-ID", "<" + contentId + ">");
		imagePart.setDisposition(MimeBodyPart.INLINE);
		String imageFilePath = workspace + "/" + ImagesMap.getImages().get(contentId).getSourcePath();
		imagePart.attachFile(imageFilePath);
		return imagePart;
	}
}
