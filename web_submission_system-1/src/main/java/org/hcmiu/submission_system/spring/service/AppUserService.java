package org.hcmiu.submission_system.spring.service;

import java.util.List;

import org.hcmiu.submission_system.spring.entity.AppUser;

public interface AppUserService {
	
	// get all AppUser
    public List<AppUser> getAllAppUser();
    
    //save AppUser
    public void saveAppUser(AppUser appUser);
    
    //get AppUser by Id
    public AppUser getAppUserById(long id);
    
    //delete AppUser by id
    public void deleteAppUserById(long id);
    
    public AppUser getUserByUserName(String userName);
}
