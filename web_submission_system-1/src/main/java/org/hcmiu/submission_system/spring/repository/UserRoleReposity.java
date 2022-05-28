package org.hcmiu.submission_system.spring.repository;

import org.hcmiu.submission_system.spring.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface UserRoleReposity extends JpaRepository<UserRole, Long>{
	@Modifying
	@Query( value = "insert into user_role (USER_ID, ROLE_ID) values (?1, 2);", nativeQuery = true)
	public void setAuthorRole(long id);
}
