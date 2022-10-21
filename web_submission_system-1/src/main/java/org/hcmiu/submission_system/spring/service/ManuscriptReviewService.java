package org.hcmiu.submission_system.spring.service;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

import org.hcmiu.submission_system.spring.entity.ManuscriptReview;
import org.hcmiu.submission_system.spring.entity.SubmissionInfor;

public interface ManuscriptReviewService {
	
	// get all manuscript review list
    public List<ManuscriptReview> getAllManuscriptReview();
    
    //save ManuscriptReview
    public void saveManuscriptReview(ManuscriptReview manuscriptReview);
    
    //get ManuscriptReview by Id
    public ManuscriptReview getManuscriptReviewById(long id);
    
    //get Manuscript review by reviewer id and manuscript id
    public ManuscriptReview getManuscriptReviewByRIDAndSID(long r_id, long s_id );
    
    public void updateStateAndComment(String state, String comment, long r_id, long s_id);
    
    public List<ManuscriptReview> getReviewListByManuscriptId(long sid);
    
    public void updateDateAndTimeReview(Date rdate, Time rtime, boolean isLate, long r_id, long s_id );
    
    public ManuscriptReview getManuscriptReviewBySidAndRid(long r_id, long s_id);
    
    public void deleteManuscriptReviewBySid(long sid);
}
