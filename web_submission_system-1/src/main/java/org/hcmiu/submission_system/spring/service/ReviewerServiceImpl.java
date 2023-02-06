package org.hcmiu.submission_system.spring.service;

import java.util.List;

import org.hcmiu.submission_system.spring.entity.Reviewer;
import org.hcmiu.submission_system.spring.repository.ReviewerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReviewerServiceImpl implements ReviewerService{
	@Autowired
	private ReviewerRepository reviewerRepository;
	@Override
	public List<Reviewer> getRecommentReviewerList(String field) {
		return this.reviewerRepository.getRecommendReviewerList(field);
	}

}
