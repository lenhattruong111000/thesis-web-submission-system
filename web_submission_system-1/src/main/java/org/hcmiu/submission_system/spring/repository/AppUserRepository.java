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
	
	@Modifying
	@Query(value = "UPDATE `submissionsystem`.`app_user` SET `Verification_Code` = :code WHERE (`USER_ID` = :userId)", nativeQuery = true)
	public void setLoginVerifyCode(@Param("code") String code, @Param("userId") long userId );
	
	//get user role by user name
	@Query(value = "select app_role.ROLE_NAME from app_role , app_user, user_role where \r\n"
			+ "app_user.USER_ID = user_role.USER_ID and \r\n"
			+ "app_role.ROLE_ID=user_role.ROLE_ID and app_user.USER_NAME= :username", nativeQuery = true)
	public String getUserRolebyUserName(@Param("username") String username);
	
	//get recommend reviewer list
	@Query(value = "SELECT * FROM reviewer WHERE MATCH(master_field) AGAINST( :field IN NATURAL LANGUAGE MODE)", nativeQuery = true)
	public List<AppUser> getRecommendReviewerList(@Param("field") String field);
	
}
