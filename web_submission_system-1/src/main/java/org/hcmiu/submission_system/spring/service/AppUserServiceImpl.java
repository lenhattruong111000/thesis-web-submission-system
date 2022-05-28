package org.hcmiu.submission_system.spring.service;

import java.util.List;
import java.util.Optional;

import org.hcmiu.submission_system.spring.entity.AppUser;
import org.hcmiu.submission_system.spring.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AppUserServiceImpl implements AppUserService{
	
	@Autowired
	private AppUserRepository appUserRepository;
	@Override
	public List<AppUser> getAllAppUser() {
		
		return appUserRepository.findAll();
	}

	@Override
	public void saveAppUser(AppUser appUser) {
		this.appUserRepository.save(appUser);
		
	}

	@Override
	public AppUser getAppUserById(long id) {
		Optional<AppUser> optional = appUserRepository.findById(id);
    	AppUser appUser =null;
	
    	if(optional.isPresent()) {
    		appUser= optional.get();
    	}else {
    		throw new RuntimeException("The user not found by id: "+id);
    	}
    	return appUser;
	}

	@Override
	public void deleteAppUserById(long id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public AppUser getUserByUserName(String userName) {
		
		Optional<AppUser> optional = Optional.ofNullable(appUserRepository.findUserByUserName(userName));
    	AppUser appUser =null;
	
    	if(optional.isPresent()) {
    		appUser= optional.get();
    	}else {
    		throw new RuntimeException("The user not found by id: "+userName);
    	}
    	return appUser;
	}

}
