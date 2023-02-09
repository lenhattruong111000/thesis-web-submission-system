package org.hcmiu.submission_system.spring.repository;

import java.util.List;

import org.hcmiu.submission_system.spring.entity.CoAuthor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface CoAuthorRespository extends JpaRepository<CoAuthor, Long>{
	// get List Co-author by manuscript id
	@Query(value = "SELECT * FROM submissionsystem.coauthor c where c.s_id = :sid", nativeQuery = true)
	public List<CoAuthor> findListCoAuthorByManuscriptId(@Param("sid") long id);

}
