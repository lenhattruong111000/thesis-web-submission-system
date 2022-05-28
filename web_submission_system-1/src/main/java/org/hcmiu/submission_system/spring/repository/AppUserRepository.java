package org.hcmiu.submission_system.spring.repository;

import org.hcmiu.submission_system.spring.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface AppUserRepository extends JpaRepository<AppUser, Long> {
	
	@Query(value = "SELECT * FROM submissionsystem.app_user where USER_NAME=?1;", nativeQuery = true)
	public AppUser findUserByUserName(String userName);
}
