package org.hcmiu.submission_system.spring.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.hcmiu.submission_system.spring.entity.AppUser;
import org.hcmiu.submission_system.spring.entity.SubmissionInfor;
import org.hcmiu.submission_system.spring.entity.UserRole;
import org.hcmiu.submission_system.spring.service.AppUserService;
import org.hcmiu.submission_system.spring.service.SubmissionInforService;
import org.hcmiu.submission_system.spring.service.UserRoleService;
import org.hcmiu.submission_system.spring.service.UserRoleServiceImpl;
import org.hcmiu.submission_system.spring.utils.WebUtils;
import org.hibernate.internal.build.AllowSysOut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class MainController {
	
	@Autowired
	private AppUserService appUserService;
	@Autowired
	private UserRoleServiceImpl userRoleServiceImpl;
	
	@Autowired
	private SubmissionInforService submissionInforService;
	
	@RequestMapping(value = { "/", "/welcome" }, method = RequestMethod.GET)
	public String welcomePage(Model model) {
		model.addAttribute("title", "Welcome");
		model.addAttribute("message", "This is welcome page!");
		return "welcomePage";
	}

	@RequestMapping(value = "/editor", method = RequestMethod.GET)
	public String adminPage(Model model, Principal principal) {
		
		User loginedUser = (User) ((Authentication) principal).getPrincipal();

		String userInfo = WebUtils.toString(loginedUser);
		model.addAttribute("userInfo", userInfo);
		

		// all submission information
		List<SubmissionInfor>  allList= submissionInforService.getAllSubmissionInfor();
		List<AppUser> listappUser = appUserService.getAllAppUser();
		
		//view all submission are waiting
		ArrayList<SubmissionInfor> waitingList = new ArrayList<SubmissionInfor>();
		for(int i=0; i<allList.size();i++) {
			if(allList.get(i).getsState().equals("waiting")) {
				waitingList.add(allList.get(i));
			}
		}
		model.addAttribute("waitingListManuscripts", waitingList);
		
		//view accepted manuscript
		ArrayList<SubmissionInfor> acceptedList = new ArrayList<SubmissionInfor>();
		for(int i=0; i<allList.size();i++) {
			if(allList.get(i).getsState().equals("accept")) {
				acceptedList.add(allList.get(i));
			}
		}
		model.addAttribute("acceptedListManuscripts", acceptedList);
		
		//view rejected list
		ArrayList<SubmissionInfor> rejectedList = new ArrayList<SubmissionInfor>();
		for(int i=0; i<allList.size();i++) {
			if(allList.get(i).getsState().equals("reject")) {
				rejectedList.add(allList.get(i));
			}
		}
		model.addAttribute("rejectedListManuscripts", rejectedList);
		
		
		
		return "adminPage";
	}

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String loginPage(Model model) {

		return "loginPage";
	}

	@RequestMapping(value = "/logoutSuccessful", method = RequestMethod.GET)
	public String logoutSuccessfulPage(Model model) {
		model.addAttribute("title", "Logout");
		return "welcomePage";
	}

	@RequestMapping(value = "/userInfo", method = RequestMethod.GET)
	public String userInfo(Model model, Principal principal) {

		//get username
		String userName = principal.getName();

		System.out.println("User Name: " + userName);

		User loginedUser = (User) ((Authentication) principal).getPrincipal();

		String userInfo = WebUtils.toString(loginedUser);
		model.addAttribute("userInfo", userInfo);
		
		// all submission information
		List<SubmissionInfor>  allList= submissionInforService.getAllSubmissionInfor();
		List<AppUser> listappUser = appUserService.getAllAppUser();
		AppUser appUser=null;
		for(int i=0; i<listappUser.size();i++) {
			if(listappUser.get(i).getUserName().equals(userName)) {
				appUser=appUserService.getAppUserById(listappUser.get(i).getUserId());
				break;
			}
		}
		
		System.out.println("id by getuserbyuserid"+ appUser.getUserId());
		
		//view all submission are waiting
		ArrayList<SubmissionInfor> waitingList = new ArrayList<SubmissionInfor>();
		for(int i=0; i<allList.size();i++) {
			if(allList.get(i).getsState().equals("waiting") &&
					allList.get(i).getAppUser().getUserId().equals(appUser.getUserId())) {
				waitingList.add(allList.get(i));
			}
		}
		
		model.addAttribute("waitingListManuscripts", waitingList);
		
		// view accepted list
		ArrayList<SubmissionInfor> accpetedList = new ArrayList<SubmissionInfor>();
		for(int i=0; i<allList.size();i++) {
			if(allList.get(i).getsState().equals("accept") &&
					allList.get(i).getAppUser().getUserId().equals(appUser.getUserId())) {
				accpetedList.add(allList.get(i));
			}
		}
		
		model.addAttribute("acceptedListManuscripts", accpetedList);
		
		//view rejected list
		ArrayList<SubmissionInfor> rejectedList = new ArrayList<SubmissionInfor>();
		for(int i=0; i<allList.size();i++) {
			if(allList.get(i).getsState().equals("reject") &&
					allList.get(i).getAppUser().getUserId().equals(appUser.getUserId())) {
				rejectedList.add(allList.get(i));
			}
		}
		
		model.addAttribute("rejectedListManuscripts", rejectedList);
		
		return "userInfoPage";
	}

	@RequestMapping(value = "/403", method = RequestMethod.GET)
	public String accessDenied(Model model, Principal principal) {

		if (principal != null) {
			User loginedUser = (User) ((Authentication) principal).getPrincipal();

			String userInfo = WebUtils.toString(loginedUser);

			model.addAttribute("userInfo", userInfo);

			String message = "Hi " + principal.getName() //
					+ "<br> You do not have permission to access this page!";
			model.addAttribute("message", message);

		}

		return "403Page";
	}
	
	//Register Author Account
	@GetMapping("/registerForm")
	public String getAuthorRegisterForm(Model model) {
		AppUser appUser = new AppUser();
		model.addAttribute("author",appUser);
		return "registerForm";
	}
	
	@PostMapping("/saveRegisterForm")
	public String saveAuthor(@ModelAttribute("author") AppUser appUser, Principal principal) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		appUser.setEnabled(true);
		String encrytedPassword = encoder.encode(appUser.getEncrytedPassword());
		appUser.setEncrytedPassword(encrytedPassword);
		//save new author to database
		appUserService.saveAppUser(appUser);
		//set role for author
		System.out.println("author id: "+ appUser.getUserId());
		userRoleServiceImpl.setAuthorRole(appUser.getUserId());
		return "redirect:/userInfo";
	}

}
