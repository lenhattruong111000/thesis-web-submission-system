package org.hcmiu.submission_system.spring.service;

import java.util.List;

import org.hcmiu.submission_system.spring.entity.Reviewer;

public interface ReviewerService {
	public List<Reviewer> getRecommentReviewerList(String field);
	public List<Reviewer> findReviewerByField(String field);
	
}
