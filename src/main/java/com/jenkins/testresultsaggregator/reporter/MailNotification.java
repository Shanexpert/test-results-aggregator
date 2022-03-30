package com.jenkins.testresultsaggregator.reporter;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.mail.Message.RecipientType;
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
import com.jenkins.testresultsaggregator.helper.Helper;
import com.jenkins.testresultsaggregator.helper.LocalMessages;

import hudson.FilePath;
import jenkins.plugins.mailer.tasks.MimeMessageBuilder;

public class MailNotification {
	
	private PrintStream logger;
	private List<Data> dataJob;
	private FilePath workspace;
	private File rootDirectory;
	private boolean useImages = true;
	
	public MailNotification(PrintStream logger, List<Data> dataJob, FilePath workspace, File rootDirectory) {
		this.logger = logger;
		this.dataJob = dataJob;
		this.workspace = workspace;
		this.rootDirectory = rootDirectory;
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
	
	public void send(String mailTo, String mailToCC, String mailToBCc, String mailFrom, String subject, String body, Map<String, ImageData> images, String preBodyText, String afterBodyText)
			throws Exception {
		logger.println(LocalMessages.GENERATE.toString() + " " + LocalMessages.EMAIL_REPORT.toString());
		MimeMessageBuilder mimeMessageBuilder = new MimeMessageBuilder();
		MimeMessage message = null;
		if (validateResults()) {
			logger.println(LocalMessages.VALIDATION_MAIL_NOT_FOUND_JOBS.toString());
		} else if (Strings.isNullOrEmpty(mailTo) && Strings.isNullOrEmpty(mailToCC) && Strings.isNullOrEmpty(mailToBCc)) {
			logger.println(LocalMessages.VALIDATION_MAIL_RECEIPIENTS_EMPTY.toString());
		} else {
			try {// Add Recipients
				String[] to = mailTo.split(",");
				for (String recipient : to) {
					mimeMessageBuilder.addRecipients(recipient, RecipientType.TO);
				}
				String[] toc = mailToCC.split(",");
				for (String recipient : toc) {
					mimeMessageBuilder.addRecipients(recipient, RecipientType.CC);
				}
				String[] tobc = mailToBCc.split(",");
				for (String recipient : tobc) {
					mimeMessageBuilder.addRecipients(recipient, RecipientType.BCC);
				}
				// Add Body before
				StringBuffer messageBody = new StringBuffer();
				if (!Strings.isNullOrEmpty(preBodyText)) {
					preBodyText = resolveVariables(preBodyText);
					messageBody.append(preBodyText);
					messageBody.append("<br></br>");
				}
				// Add Body
				messageBody.append(body);
				// Add Body before and after text
				if (!Strings.isNullOrEmpty(afterBodyText)) {
					afterBodyText = resolveVariables(afterBodyText);
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
				message = mimeMessageBuilder.buildMimeMessage();
				message.setFrom(new InternetAddress(mailFrom));
				useImages(messageBody, images, message);
				// Save Message
				message.saveChanges();
				// Send Message
				sendMessage(message);
				logger.println(LocalMessages.SEND_MAIL_TO.toString());
				if (!Strings.isNullOrEmpty(mailTo)) {
					logger.println("" + mailTo);
				}
				if (!Strings.isNullOrEmpty(mailToCC)) {
					logger.println("" + mailToCC);
				}
				if (!Strings.isNullOrEmpty(mailToBCc)) {
					logger.println("" + mailToBCc);
				}
			} catch (Exception ex) {
				// Send Mail with no images
				logger.printf(LocalMessages.ERROR_OCCURRED.toString() + ": " + ex.getMessage());
				logger.println(LocalMessages.SEND_MAIL_TO.toString());
				if (message != null) {
					message = mimeMessageBuilder.buildMimeMessage();
					message.setFrom(new InternetAddress(mailFrom));
					// Save Message
					message.saveChanges();
					sendMessage(message);
				}
				ex.printStackTrace();
				logger.println("");
			}
		}
	}
	
	public void sendIgnoredData(String mailTo, String mailFrom, String subject, String body, String preBodyText, String afterBodyText)
			throws Exception {
		logger.println(LocalMessages.GENERATE.toString() + " " + LocalMessages.EMAIL_REPORT.toString());
		MimeMessageBuilder mimeMessageBuilder = new MimeMessageBuilder();
		MimeMessage message = null;
		if (validateResults()) {
			logger.println(LocalMessages.VALIDATION_MAIL_NOT_FOUND_JOBS.toString());
		} else if (Strings.isNullOrEmpty(mailTo)) {
			logger.println(LocalMessages.VALIDATION_MAIL_RECEIPIENTS_EMPTY.toString());
		} else {
			try {// Add Recipients
				String[] to = mailTo.split(",");
				for (String recipient : to) {
					mimeMessageBuilder.addRecipients(recipient, RecipientType.TO);
				}
				// Add Body before
				StringBuffer messageBody = new StringBuffer();
				if (!Strings.isNullOrEmpty(preBodyText)) {
					preBodyText = resolveVariables(preBodyText);
					messageBody.append(preBodyText);
					messageBody.append("<br></br>");
				}
				// Add Body
				messageBody.append(body);
				// Add Body before and after text
				if (!Strings.isNullOrEmpty(afterBodyText)) {
					afterBodyText = resolveVariables(afterBodyText);
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
				message = mimeMessageBuilder.buildMimeMessage();
				message.setFrom(new InternetAddress(mailFrom));
				// Save Message
				message.saveChanges();
				// Send Message
				sendMessage(message);
				logger.println(LocalMessages.SEND_MAIL_TO.toString());
				logger.println("" + mailTo);
			} catch (Exception ex) {
				// Send Mail with no images
				logger.println(LocalMessages.ERROR_OCCURRED.toString() + ": " + ex.getMessage());
				logger.println(LocalMessages.SEND_MAIL_TO.toString());
				if (message != null) {
					message = mimeMessageBuilder.buildMimeMessage();
					message.setFrom(new InternetAddress(mailFrom));
					// Save Message
					message.saveChanges();
					sendMessage(message);
				}
				ex.printStackTrace();
				logger.println("");
			}
		}
	}
	
	private void sendMessage(MimeMessage message) throws Exception {
		Transport.send(message);
	}
	
	private void useImages(StringBuffer messageBody, Map<String, ImageData> images, MimeMessage message) throws MessagingException, IOException, InterruptedException, URISyntaxException {
		MimeBodyPart messageBodyPart = new MimeBodyPart();
		messageBodyPart.setContent(messageBody.toString(), "text/html");
		Multipart multipart = new MimeMultipart();
		multipart.addBodyPart(messageBodyPart);
		// Disable images
		if (useImages) {
			if (images != null && !images.isEmpty()) {
				Set<String> setImageID = images.keySet();
				for (String contentId : setImageID) {
					multipart.addBodyPart(addImagePart(contentId));
				}
				message.setContent(multipart);
			}
		}
	}
	
	private MimeBodyPart addImagePart(String contentId) throws MessagingException, IOException, InterruptedException, URISyntaxException {
		ImageData imageData = ImagesMap.getImages().get(contentId);
		MimeBodyPart imagePart = new MimeBodyPart();
		imagePart.setHeader("Content-ID", "<" + contentId + ">");
		imagePart.setDisposition(MimeBodyPart.INLINE);
		// Get File
		FilePath localFile = Helper.createFile(workspace, imageData.getSourcePath());
		File copied = new File(rootDirectory + "/" + imageData.getFileName());
		try (
				InputStream in = new BufferedInputStream(new FileInputStream(localFile.getRemote()));
				OutputStream out = new BufferedOutputStream(new FileOutputStream(copied))) {
			byte[] buffer = new byte[1024];
			int lengthRead;
			while ((lengthRead = in.read(buffer)) > 0) {
				out.write(buffer, 0, lengthRead);
				out.flush();
			}
		}
		imagePart.attachFile(copied);
		return imagePart;
	}
	
	protected File copyStream(String sourceFile, String destinationFile, File directory) throws IOException, InterruptedException {
		InputStream inputUrl = HTMLReporter.class.getResource(sourceFile).openStream();
		// Create Destination File
		Helper.createFile(new FilePath(directory), destinationFile).copyFrom(inputUrl);
		return new File(directory + destinationFile);
	}
	
	private String resolveVariables(String text) {
		// String newText = Matcher.quoteReplacement(text);
		// newText = TokenMacro.expandAll(context.getRun(), context.getWorkspace(), context.getListener(), newText, false, null);
		return text != null ? text.trim() : "";
	}
}
