package org.hcmiu.submission_system.spring.service;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

import org.hcmiu.submission_system.spring.entity.ManuscriptReview;
import org.hcmiu.submission_system.spring.entity.SubmissionInfor;
import org.hcmiu.submission_system.spring.repository.ManuscriptReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ManuscriptReviewServiceImpl implements ManuscriptReviewService {
	@Autowired
	private ManuscriptReviewRepository manuscriptReviewRepository;
	
	@Override
	public List<ManuscriptReview> getAllManuscriptReview() {
		
		return manuscriptReviewRepository.findAll();
	}

	@Override
	public void saveManuscriptReview(ManuscriptReview manuscriptReview) {
		this.manuscriptReviewRepository.save(manuscriptReview);
		
	}

	@Override
	public ManuscriptReview getManuscriptReviewById(long id) {
		
		return null;
	}

	@Override
	public ManuscriptReview getManuscriptReviewByRIDAndSID(long r_id, long s_id) {
		
		return manuscriptReviewRepository.getManuscriptReviewByRIDAndSID(r_id, s_id);
	}

	@Override
	public void updateStateAndComment(String state, String comment, long r_id, long s_id) {
		this.manuscriptReviewRepository.updateStateAndComment(state, comment, r_id, s_id);
		
	}

	@Override
	public List<ManuscriptReview> getReviewListByManuscriptId(long sid) {
		
		return manuscriptReviewRepository.getReviewListByManuscriptId(sid);
	}

	@Override
	public void updateDateAndTimeReview(Date rdate, Time rtime, boolean isLate, long r_id, long s_id) {
		this.manuscriptReviewRepository.updateDateAndTimeReview(rdate, rtime, isLate, r_id, s_id);
		
	}

	@Override
	public ManuscriptReview getManuscriptReviewBySidAndRid(long r_id, long s_id) {
		
		return manuscriptReviewRepository.getManuscriptReviewByRIDAndSID(r_id, s_id);
	}

	@Override
	public void deleteManuscriptReviewBySid(long sid) {
		this.manuscriptReviewRepository.deleteBySid(sid);
		
	}

}
