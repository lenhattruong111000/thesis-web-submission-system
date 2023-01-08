package org.hcmiu.submission_system.spring.repository;

import org.hcmiu.submission_system.spring.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {

}
