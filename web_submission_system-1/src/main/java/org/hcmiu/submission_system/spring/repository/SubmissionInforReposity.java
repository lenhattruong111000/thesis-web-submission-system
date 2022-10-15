package org.hcmiu.submission_system.spring.repository;

import java.util.List;

import org.hcmiu.submission_system.spring.entity.SubmissionInfor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface SubmissionInforReposity extends JpaRepository<SubmissionInfor, Long> {
	@Modifying
	@Query(value = "UPDATE `submission_infor` SET `s_authorid` =?1, `s_authorname` =?2 WHERE (`s_id` =?3);", nativeQuery = true)
	public void setAuthorIdAndName(long authorid, String authorName, long sid);
	
	@Modifying
	@Query(value ="UPDATE `submissionsystem`.`submission_infor` SET `s_state` =?1 WHERE (`s_id` =?2);", nativeQuery = true )
	public void updateStateSubmission(String state, long sid);
	
	@Modifying
	@Query(value ="UPDATE `submissionsystem`.`submission_infor` SET `s_comment` =?1 WHERE (`s_id` =?2);", nativeQuery = true )
	public void updateComment(String comment, long sid);
	
	
	@Query(value = "select * from submission_infor s where s.s_id= (select s_id from manuscript_review m where s.s_id= m.s_id and m.reviewer_id= ?1);", nativeQuery = true)
	public List<SubmissionInfor> getManuscriptReviewListByReviewerId(long id);
	
	//get ManuscriptReview List By Reviewer userName in waiting state
	@Query(value = "select * from submission_infor s where s.s_id= (select s_id from manuscript_review m where s.s_id= m.s_id and m.reviewer_state='waiting' and m.reviewer_id=(select u.USER_ID from app_user u where u.USER_NAME= :username))", nativeQuery = true)
	public List<SubmissionInfor> getWaitingManuscriptReviewListByReviewerUsername(@Param("username") String username);
	
	@Modifying
	@Query(value = "SET GLOBAL max_allowed_packet=134740992;", nativeQuery = true)
	public void setMaxAllowedPacket();
}
