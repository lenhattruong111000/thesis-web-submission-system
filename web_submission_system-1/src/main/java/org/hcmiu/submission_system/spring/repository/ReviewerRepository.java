package org.hcmiu.submission_system.spring.repository;

import java.util.List;

import org.hcmiu.submission_system.spring.entity.Reviewer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewerRepository extends JpaRepository<Reviewer, Long> {
	
	//get recommend reviewer list
	@Query(value = "SELECT * FROM reviewer WHERE MATCH(master_field) AGAINST( :field IN NATURAL LANGUAGE MODE)", nativeQuery = true)
	public List<Reviewer> getRecommendReviewerList(@Param("field") String field);
}
