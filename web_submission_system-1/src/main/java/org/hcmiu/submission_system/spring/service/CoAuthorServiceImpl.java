package org.hcmiu.submission_system.spring.service;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Optional;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.hcmiu.submission_system.spring.entity.CoAuthor;
import org.hcmiu.submission_system.spring.entity.SubmissionInfor;
import org.hcmiu.submission_system.spring.repository.CoAuthorRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class CoAuthorServiceImpl implements CoAuthorService{
	
	@Autowired
	private CoAuthorRespository coAuthorRespository;
	
	@Autowired
	private JavaMailSender javaMailSender;
	
	@Override
	public List<CoAuthor> getAllCoAuthor() {
		return coAuthorRespository.findAll();
	}

	@Override
	public void saveCoAuthor(CoAuthor coAuthor) {
		this.coAuthorRespository.save(coAuthor);
		
		
	}

	@Override
	public CoAuthor getCoAuthorById(long id) {
		Optional<CoAuthor> optional = coAuthorRespository.findById(id);
    	CoAuthor coAuthor =null;
	
    	if(optional.isPresent()) {
    		coAuthor= optional.get();
    	}else {
    		throw new RuntimeException("The co-author not found by id: "+id);
    	}
    	return coAuthor;
	}

	@Override
	public void deleteCoAuthorById(long id) {
		this.coAuthorRespository.deleteById(id);
	}

	@Override
	public void notificationEmailForCoAuthor(CoAuthor coAuthor) throws MessagingException, UnsupportedEncodingException  {
		String toAddress = coAuthor.getCoEmail();
		 String fromAddress = "jobandjob336@gmail.com";
		    String senderName = "Submission_System";
		    String subject = "Manuscript Reviewing";
		    String content = "Dear "+coAuthor.getCoFullname()+",<br>"
		            + "You have been added to the manuscript as a co-author by author "+coAuthor.getSubmissionInfor().getsAuthorname()+"<br>"
		            + "Manuscript Name is "+ coAuthor.getSubmissionInfor().getsTitle()+"<br>"
		            + "Field: "+ coAuthor.getSubmissionInfor().getsMajor() +"<br>"
		            + "Best regards,<br>"
		            + "Submission_System.";
		     
		    MimeMessage message = javaMailSender.createMimeMessage();
		    MimeMessageHelper helper = new MimeMessageHelper(message);
		     
		    helper.setFrom(fromAddress, senderName);
		    helper.setTo(toAddress);
		    helper.setSubject(subject);
		     
		    helper.setText(content, true);
		     
		    javaMailSender.send(message);
			
		
	}

	@Override
	public List<CoAuthor> getCoAuhtorListByManuscriptId(long id) {
		
		return this.coAuthorRespository.findListCoAuthorByManuscriptId(id);
	}
	
	private String fromEmailAddress() {
		return "jobandjob336@gmail.com";
	}
	
	@Override
	public void emailForNotifyAuthorAndCoAuthorAboutSubmissionState(CoAuthor coauthor, SubmissionInfor submissionInfor)
			throws MessagingException, UnsupportedEncodingException {
		String toAddress = coauthor.getCoEmail();
	    String fromAddress = fromEmailAddress();
	    String senderName = "Submission_System";
	    String subject = "Manuscript State";
	    String content = "Dear "+coauthor.getCoFullname()+",<br>"
	    		+ "Manuscript ID: "+submissionInfor.getsId() +"<br>"
	    		+ "Title: "+submissionInfor.getsTitle()+"<br>"
	    		+ "State: "+submissionInfor.getsState()+"<br>"
	    		+ "Comment: <br>"
	    		+ submissionInfor.getsComment() +"<br>"
	            + "Please, login to the system for more details.<br>"
	            + "Best regards,<br>"
	            + "Submission_System.";
	     
	    MimeMessage message = javaMailSender.createMimeMessage();
	    MimeMessageHelper helper = new MimeMessageHelper(message);
	     
	    helper.setFrom(fromAddress, senderName);
	    helper.setTo(toAddress);
	    helper.setSubject(subject);
	     
	    helper.setText(content, true);
	     
	    javaMailSender.send(message);
		
	}

}
