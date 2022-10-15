package org.hcmiu.submission_system.spring.service;

import java.util.List;
import java.util.Optional;

import org.hcmiu.submission_system.spring.entity.SubmissionInfor;
import org.hcmiu.submission_system.spring.repository.SubmissionInforReposity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
public class SubmissionInforServiceImpl implements SubmissionInforService {
	@Autowired
	private SubmissionInforReposity submissionInforReposity;
	@Override
	public List<SubmissionInfor> getAllSubmissionInfor() {
		return submissionInforReposity.findAll();
	}

	@Override
	public void saveSubmissionInfor(SubmissionInfor submissionInfor) {
		this.submissionInforReposity.save(submissionInfor);
		
	}

	@Override
	public SubmissionInfor getSubmissionInforById(long id) {
		Optional<SubmissionInfor> optional = submissionInforReposity.findById(id);
    	SubmissionInfor submissionInfor =null;
	
    	if(optional.isPresent()) {
    		submissionInfor= optional.get();
    	}else {
    		throw new RuntimeException("The  not found  submission infor by id: "+id);
    	}
    	return submissionInfor;
	}

	@Override
	public void deleteSubmissionInforById(long id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setNameAndIdForAuthor(long authorid, String authorName, long sid) {
		this.submissionInforReposity.setAuthorIdAndName(authorid, authorName, sid);
		
	}

	@Override
	public void updateStateSubmission(String state, long sid) {
		this.submissionInforReposity.updateStateSubmission(state, sid);
		
	}

	@Override
	public void updateMauscriptComment(String comment, long sid) {
		this.submissionInforReposity.updateComment(comment, sid);
		
	}
	
	@Override
	public List<SubmissionInfor> getManuscriptReviewListByReviewerId(long id) {
		
		return submissionInforReposity.getManuscriptReviewListByReviewerId(id);
	}

	@Override
	public List<SubmissionInfor> getWaitingManuscriptReviewListByReviewerUsername(String username) {
		return submissionInforReposity.getWaitingManuscriptReviewListByReviewerUsername(username);
	}

	@Override
	public void setMaxAllowedPacket() {
		this.submissionInforReposity.setMaxAllowedPacket();
		
	}

}
