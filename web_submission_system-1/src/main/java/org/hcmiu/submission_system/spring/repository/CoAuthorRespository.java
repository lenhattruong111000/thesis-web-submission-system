package org.hcmiu.submission_system.spring.repository;

import org.hcmiu.submission_system.spring.entity.CoAuthor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface CoAuthorRespository extends JpaRepository<CoAuthor, Long>{

}
