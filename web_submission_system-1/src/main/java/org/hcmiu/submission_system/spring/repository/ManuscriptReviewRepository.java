package org.hcmiu.submission_system.spring.repository;


import java.sql.Date;
import java.sql.Time;
import java.util.List;

import org.hcmiu.submission_system.spring.entity.ManuscriptReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface ManuscriptReviewRepository extends JpaRepository<org.hcmiu.submission_system.spring.entity.ManuscriptReview, Long> {
	
	//get Manuscript review by reviewer id and manuscript id
	@Query(value = "select * from manuscript_review m where m.reviewer_id = :r_id and m.s_id= :s_id", nativeQuery = true)
	public ManuscriptReview getManuscriptReviewByRIDAndSID(@Param("r_id") long r_id, @Param("s_id") long s_id );
	
	//update Manuscript review state and comment
	@Modifying
	@Query(value = "update manuscript_review set reviewer_state= :state , reviewer_comment= :comment where reviewer_id= :r_id and s_id = :s_id", nativeQuery = true)
	public void updateStateAndComment(@Param("state") String state, @Param("comment") String comment, @Param("r_id") long r_id, @Param("s_id") long s_id);
	
	//get manuscript review by manuscript id
	@Query(value = "select * from manuscript_review m where m.s_id= :sid", nativeQuery = true)
	public List<ManuscriptReview> getReviewListByManuscriptId(@Param("sid") long sid);
	
	//get manuscript review by s_id and r_id
	@Query(value = "select * from manuscript_review m where m.reviewer_id= :r_id, m.s_id= :s_id", nativeQuery = true)
	public ManuscriptReview getManuscriptReviewBySidAndRid(@Param("r_id") long r_id, @Param("s_id") long s_id);
	
	//update date and time review
	@Modifying
	@Query(value = "update manuscript_review set review_date= :rdate , review_time= :rtime , is_late= :isLate where reviewer_id= :r_id and s_id = :s_id", nativeQuery = true)
	public void updateDateAndTimeReview(@Param("rdate") Date rdate, @Param("rtime") Time rtime, @Param("isLate") boolean isLate ,@Param("r_id") long r_id, @Param("s_id") long s_id);
	
	//delete by sId
	@Modifying
	@Query(value = "delete from submissionsystem.manuscript_review m where m.s_id= :sid", nativeQuery = true)
	public void deleteBySid(@Param("sid") long sid);
}
