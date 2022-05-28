package org.hcmiu.submission_system.spring.service;

import org.hcmiu.submission_system.spring.entity.UserRole;
import org.hcmiu.submission_system.spring.repository.UserRoleReposity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserRoleServiceImpl implements UserRoleService{
	
	@Autowired
	private UserRoleReposity userRoleReposity;
	@Override
	public void saveAuthorRole(UserRole userRole) {
		this.userRoleReposity.save(userRole);
		
	}
	@Override
	public void setAuthorRole(long id) {
		this.userRoleReposity.setAuthorRole(id);
		
	}

}
