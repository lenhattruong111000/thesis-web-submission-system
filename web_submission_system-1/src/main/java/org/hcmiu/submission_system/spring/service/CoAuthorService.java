package org.hcmiu.submission_system.spring.service;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.mail.MessagingException;

import org.hcmiu.submission_system.spring.entity.CoAuthor;
import org.hcmiu.submission_system.spring.entity.SubmissionInfor;

public interface CoAuthorService {
	// get all CoAuthor
    public List<CoAuthor> getAllCoAuthor();
    
    //save CoAuthor
    public void saveCoAuthor(CoAuthor coAuthor);
    
    //get CoAuthor by Id
    public CoAuthor getCoAuthorById(long id);
    
    //delete CoAuthor by id
    public void deleteCoAuthorById(long id);
    
    //send notification email for co-author
    public void notificationEmailForCoAuthor(CoAuthor coAuthor) throws MessagingException, UnsupportedEncodingException;
}
