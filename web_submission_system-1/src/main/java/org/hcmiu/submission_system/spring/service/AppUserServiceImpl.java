package org.hcmiu.submission_system.spring.service;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Optional;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.hcmiu.submission_system.spring.entity.AppUser;
import org.hcmiu.submission_system.spring.entity.SubmissionInfor;
import org.hcmiu.submission_system.spring.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import net.bytebuddy.utility.RandomString;

@Service
public class AppUserServiceImpl implements AppUserService{
	
	@Autowired
	private AppUserRepository appUserRepository;
	
	@Autowired
	private JavaMailSender javaMailSender;
	
	@Autowired
	private PasswordEncoder passwordEncoder; 
	
	@Override
	public List<AppUser> getAllAppUser() {
		
		return appUserRepository.findAll();
	}

	@Override
	public void saveAppUser(AppUser appUser) {
		this.appUserRepository.save(appUser);
		
	}

	@Override
	public AppUser getAppUserById(long id) {
		Optional<AppUser> optional = appUserRepository.findById(id);
    	AppUser appUser =null;
	
    	if(optional.isPresent()) {
    		appUser= optional.get();
    	}else {
    		throw new RuntimeException("The user not found by id: "+id);
    	}
    	return appUser;
	}

	@Override
	public void deleteAppUserById(long id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public AppUser getUserByUserName(String userName) {
		
		Optional<AppUser> optional = Optional.ofNullable(appUserRepository.findUserByUserName(userName));
    	AppUser appUser =null;
	
    	if(optional.isPresent()) {
    		appUser= optional.get();
    	}else {
    		throw new RuntimeException("The user not found by id: "+userName);
    	}
    	return appUser;
	}
	
	@Override
	public void register(AppUser appUser, String siteURL) throws MessagingException, UnsupportedEncodingException{
		String encrytedPassword = passwordEncoder.encode(appUser.getEncrytedPassword());
		appUser.setEncrytedPassword(encrytedPassword);
	     
	    String randomCode = RandomString.make(64);
	    appUser.setVerificationCode(randomCode);
	    appUser.setEnabled(false);
	     
	    appUserRepository.save(appUser);
	     
	    sendVerificationEmail(appUser, siteURL);
	}
	private String fromEmailAddress() {
		return "jobandjob336@gmail.com";
	}

	private void sendVerificationEmail(AppUser user, String siteURL) throws MessagingException, UnsupportedEncodingException {
		String toAddress = user.getUserEmail();
	    String fromAddress = fromEmailAddress();
	    String senderName = "Submission_System";
	    String subject = "Please verify your registration";
	    String content = "Dear "+user.getFullName()+",<br>"
	            + "Please click the link below to verify your registration:<br>"
	            + "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>"
	            + "Thank you,<br>"
	            + "Submission_System.";
	     
	    MimeMessage message = javaMailSender.createMimeMessage();
	    MimeMessageHelper helper = new MimeMessageHelper(message);
	     
	    helper.setFrom(fromAddress, senderName);
	    helper.setTo(toAddress);
	    helper.setSubject(subject);
	     
	    content = content.replace("[[name]]", user.getFullName());
	    String verifyURL = siteURL + "/verify?code=" + user.getVerificationCode();
	     
	    content = content.replace("[[URL]]", verifyURL);
	     
	    helper.setText(content, true);
	     
	    javaMailSender.send(message);
	}
	

	@Override
	public boolean emailVerify(String verificationCode) {
		System.out.println("code = "+verificationCode);
		AppUser appUser = appUserRepository.findByVerificationCode(verificationCode);
	    
	    if (appUser == null || appUser.isEnabled()) {
	    	System.out.println("?????");
	        return false;
	    } else {
	    	System.out.println("done now!");
	    	appUser.setVerificationCode(null);
	    	appUser.setEnabled(true);
	        appUserRepository.save(appUser);
	         
	        return true;
	    }
	}
	
	@Override
	public List<AppUser> getAllReviewer() {
		
		return appUserRepository.getAllReviewer();
	}

	@Override
	public void emailForNotifyReviewer(AppUser appUser) throws MessagingException, UnsupportedEncodingException {
		String toAddress = appUser.getUserEmail();
	    String fromAddress = fromEmailAddress();
	    String senderName = "Submission_System";
	    String subject = "Manuscript Reviewing";
	    String content = "Dear "+appUser.getFullName()+",<br>"
	            + "Please, login to the system  to review the Manuscript that I already send you.<br>"
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
	public void emailForNotifyAuthorAboutSubmissionState(AppUser appUser, SubmissionInfor submissionInfor)
			throws MessagingException, UnsupportedEncodingException {
		
		String toAddress = appUser.getUserEmail();
	    String fromAddress = fromEmailAddress();
	    String senderName = "Submission_System";
	    String subject = "Manuscript State";
	    String content = "Dear "+submissionInfor.getsAuthorname()+",<br>"
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

	@Override
	public void emailLoginVerify(AppUser appUser) throws MessagingException, UnsupportedEncodingException {
		// set ramdon verify code for login 
		String randomCode = RandomString.make(6);
		appUser.setVerificationCode(randomCode);
		
		// store verify code for login to the database
		appUserRepository.setLoginVerifyCode(randomCode, appUser.getUserId());
		
		//sent verify code with email
		String toAddress = appUser.getUserEmail();
	    String fromAddress = fromEmailAddress();
	    String senderName = "Submission_System";
	    String subject = "Verify_Login";
	    String content = "Dear "+appUser.getFullName()+",<br>"
	            + "This is your verify code for login to the system:<br>"
	    		+ "Code: "+ appUser.getVerificationCode() + "<br>"
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
	public String getUserRolebyUserName(String username) {
		
		return appUserRepository.getUserRolebyUserName(username);
	}

}
