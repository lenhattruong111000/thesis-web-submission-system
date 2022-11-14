package org.hcmiu.submission_system.spring.service;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.mail.MessagingException;

import org.hcmiu.submission_system.spring.entity.AppUser;
import org.hcmiu.submission_system.spring.entity.SubmissionInfor;

public interface AppUserService {
	
	// get all AppUser
    public List<AppUser> getAllAppUser();
    
    //get all reviewer
    public List<AppUser> getAllReviewer();
    
    //save AppUser
    public void saveAppUser(AppUser appUser);
    
    //get AppUser by Id
    public AppUser getAppUserById(long id);
    
    //delete AppUser by id
    public void deleteAppUserById(long id);
    
    public AppUser getUserByUserName(String userName);
    
    public void register(AppUser appUser, String siteURL) throws MessagingException, UnsupportedEncodingException;
    
    public boolean emailVerify(String verificationCode);
    
    public void emailForNotifyReviewer(AppUser appUser) throws MessagingException, UnsupportedEncodingException;
    
    public void emailForNotifyAuthorAboutSubmissionState(AppUser appUser, SubmissionInfor submissionInfor) throws MessagingException, UnsupportedEncodingException;
    
    public void emailLoginVerify(AppUser appUser) throws MessagingException, UnsupportedEncodingException;
    
    public String getUserRolebyUserName(String username);
    
    /////
    public void setLoginVerifyCode(String code, long userId );
    
    ////
}