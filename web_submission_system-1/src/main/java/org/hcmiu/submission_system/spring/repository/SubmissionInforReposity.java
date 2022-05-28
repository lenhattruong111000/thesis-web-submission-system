package org.hcmiu.submission_system.spring.repository;

import org.hcmiu.submission_system.spring.entity.SubmissionInfor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
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
	
}
