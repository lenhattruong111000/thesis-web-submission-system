package org.hcmiu.submission_system.spring.service;

import java.util.List;

import org.hcmiu.submission_system.spring.entity.SubmissionInfor;

public interface SubmissionInforService {
	// get all SubmissionInfor
    public List<SubmissionInfor> getAllSubmissionInfor();
    
    //save SubmissionInfor
    public void saveSubmissionInfor(SubmissionInfor submissionInfor);
    
    //get SubmissionInfor by Id
    public SubmissionInfor getSubmissionInforById(long id);
    
    //delete SubmissionInfor by id
    public void deleteSubmissionInforById(long id);
    
    public void setNameAndIdForAuthor(long authorid, String authorName, long sid);
    
    public void updateStateSubmission(String state, long sid);
	
}
