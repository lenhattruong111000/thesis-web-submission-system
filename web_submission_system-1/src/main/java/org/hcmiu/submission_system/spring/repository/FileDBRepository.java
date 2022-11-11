package org.hcmiu.submission_system.spring.repository;

import org.hcmiu.submission_system.spring.entity.FileDB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface FileDBRepository extends JpaRepository<FileDB, Long>{
	@Modifying
	@Query(value = "UPDATE `submissionsystem`.`files` SET `s_id` =?1 WHERE (`id` =?2);", nativeQuery = true)
	public void setSubmissionInfoId(long sid, long id);
	
	@Query(value = "select * From submissionsystem.files f where f.id= :id", nativeQuery = true)
	public FileDB getFileById(@Param("id") long id);
}
