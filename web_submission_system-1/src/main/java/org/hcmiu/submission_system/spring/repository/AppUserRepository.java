package org.hcmiu.submission_system.spring.repository;

import java.util.List;

import org.hcmiu.submission_system.spring.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface AppUserRepository extends JpaRepository<AppUser, Long> {
	
	@Query(value = "SELECT * FROM submissionsystem.app_user where USER_NAME= :userName", nativeQuery = true)
	public AppUser findUserByUserName(@Param("userName") String userName);
	
	@Query(value = "SELECT * FROM app_user u WHERE u.Verification_Code = :code", nativeQuery = true)
    public AppUser findByVerificationCode(@Param("code") String code);
	
	@Modifying
	@Query(value="Update AppUser a Set a.enabled = true where a.id= ?1;", nativeQuery = true)
	public void enabled(int id);
	
	@Query(value = "SELECT * FROM app_user u, user_role r where u.user_id=r.user_id and r.role_id=3;", nativeQuery = true)
	public List<AppUser> getAllReviewer();
}
