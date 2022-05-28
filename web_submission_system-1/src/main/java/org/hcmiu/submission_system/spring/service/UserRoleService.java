package org.hcmiu.submission_system.spring.service;

import org.hcmiu.submission_system.spring.entity.UserRole;

public interface UserRoleService {
	
	public void saveAuthorRole(UserRole userRole);
	
	public void setAuthorRole(long id);

}
