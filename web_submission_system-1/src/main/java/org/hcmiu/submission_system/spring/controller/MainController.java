package org.hcmiu.submission_system.spring.controller;

import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import org.hcmiu.submission_system.spring.entity.AppUser;
import org.hcmiu.submission_system.spring.entity.Author;
import org.hcmiu.submission_system.spring.entity.SubmissionInfor;
import org.hcmiu.submission_system.spring.entity.UserRole;
import org.hcmiu.submission_system.spring.service.AppUserService;
import org.hcmiu.submission_system.spring.service.AuthorServiceImpl;
import org.hcmiu.submission_system.spring.service.ManuscriptReviewService;
import org.hcmiu.submission_system.spring.service.SubmissionInforService;
import org.hcmiu.submission_system.spring.service.UserRoleService;
import org.hcmiu.submission_system.spring.service.UserRoleServiceImpl;
import org.hcmiu.submission_system.spring.utils.WebUtils;
import org.hibernate.internal.build.AllowSysOut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
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
	
	@Autowired
	private AuthorServiceImpl authorServiceImpl;
	
	//@Autowired
	//private ManuscriptReviewService manuscriptReviewService;
	
	@RequestMapping(value = { "/", "/welcome" }, method = RequestMethod.GET)
	public String welcomePage(Model model) {
		model.addAttribute("title", "Welcome");
		model.addAttribute("message", "This is welcome page!");
		return "welcomePage";
	}
	
	@RequestMapping(value = "/logoutSuccessful", method = RequestMethod.GET)
	public String logoutSuccessfulPage(Model model) {
		model.addAttribute("title", "Logout");
		return "welcomePage";
	}
	
	@RequestMapping("/register_success")
	public String registerSuccess() {
		return "register_success";
	}
	
	
	//=========================================Editor Page================================================//
	@RequestMapping(value = "/editor", method = RequestMethod.GET)
	public String adminPage(Model model, Principal principal) {
		
		User loginedUser = (User) ((Authentication) principal).getPrincipal();

		String userInfo = WebUtils.toString(loginedUser);
		
		///////
		AppUser getAppUser = appUserService.getUserByUserName(principal.getName());
		if(getAppUser.isEnabled()==true && getAppUser.getVerificationCode()!=null) {
			return "verifyLogin";
		}
		//////
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
			if(allList.get(i).getsState().equals("accept_with_small_changeable")||
					allList.get(i).getsState().equals("accept_with_changeable")	) {
				acceptedList.add(allList.get(i));
			}
		}
		model.addAttribute("acceptedListManuscripts", acceptedList);
		
		//view rejected list
		ArrayList<SubmissionInfor> resubmitList = new ArrayList<SubmissionInfor>();
		for(int i=0; i<allList.size();i++) {
			if(allList.get(i).getsState().equals("re-submit")) {
				resubmitList.add(allList.get(i));
			}
		}
		model.addAttribute("resubmitListManuscripts", resubmitList);
				
				
		
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
	
	//============================================Reviewer Page=============================================//
	@RequestMapping(value = "/reviewer", method = RequestMethod.GET)
	public String ReviewerPage(Model model, Principal principal) {
		
		User loginedUser = (User) ((Authentication) principal).getPrincipal();

		String userInfo = WebUtils.toString(loginedUser);
		
		///////
		AppUser getAppUser = appUserService.getUserByUserName(principal.getName());
		if(getAppUser.isEnabled()==true && getAppUser.getVerificationCode()!=null) {
			return "verifyLogin";
		}
		//////
		model.addAttribute("userInfo", userInfo);
		
		model.addAttribute("waitingListManuscripts",submissionInforService.getWaitingManuscriptReviewListByReviewerUsername(principal.getName()));
		return "reviewerPage";
	}

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String loginPage(Model model) {

		return "loginPage";
	}
	
	@RequestMapping(value = "/verifyLogin", method = RequestMethod.GET)
	public String verifyLoginPage(Model model, Principal principal) throws UnsupportedEncodingException, MessagingException {
		
		//get user by user name
		AppUser getAppUser = appUserService.getUserByUserName(principal.getName());
		if(getAppUser.isEnabled()==true) {
			//send email with verify code for login
			appUserService.emailLoginVerify(getAppUser);
			return "verifyLogin";
		}else {
			return "verify_first";
		}
		
	}

	//========================================Author Page====================================================//
	@RequestMapping(value = "/userInfo", method = RequestMethod.GET)
	public String userInfo(Model model, Principal principal) throws UnsupportedEncodingException, MessagingException {

		//get username
		String userName = principal.getName();

		System.out.println("User Name: " + userName);

		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		
		//get user by user name
		AppUser getAppUser = appUserService.getUserByUserName(principal.getName());
		/////////
		if(getAppUser.isEnabled()==true && getAppUser.getVerificationCode()!=null) {
			return "verifyLogin";
		}
		////////
		if(getAppUser.isEnabled()==true) {
			
			
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
				if(allList.get(i).getsState().equals("accept_with_small_changeable") || 
						allList.get(i).getsState().equals("accept_with_changeable") &&
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
			
			
		}else {
			return "verify_first";
		}


	}
	
	@PostMapping("/verifyLoginCode")
	public String enterUserPage(HttpServletRequest request, Principal principal, Model model) {
		String verifyCode = request.getParameter("verifyLoginCode");
		AppUser user = appUserService.getUserByUserName(principal.getName());
		if(verifyCode.equals(user.getVerificationCode())) {
			
			////
			appUserService.setLoginVerifyCode(null, user.getUserId());
			////
			
			if(appUserService.getUserRolebyUserName(principal.getName()).equals("ROLE_AUTHOR")) {
				
				return "redirect:/userInfo";
			}else if(appUserService.getUserRolebyUserName(principal.getName()).equals("ROLE_EDITOR")) {
				
				return "redirect:/editor";
			}else if(appUserService.getUserRolebyUserName(principal.getName()).equals("ROLE_REVIEWER")) {
				
				return "redirect:/reviewer";
			}else return "verify_fail";
			
		}else return "verify_fail";
	
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
	
	//======================================Register Author Account Page======================================//
	@GetMapping("/registerForm")
	public String getAuthorRegisterForm(Model model) {
	
		AppUser appUser = new AppUser();
		model.addAttribute("author",appUser);
		return "registerForm";
	}
	
	@PostMapping("/saveRegisterForm")
	public String saveAuthor(@ModelAttribute("author") AppUser appUser, Principal principal, HttpServletRequest request) throws UnsupportedEncodingException, MessagingException {
		
		//register and save new author to database
		appUserService.register(appUser,getSiteURL(request));
		//set role for author
		System.out.println("author id: "+ appUser.getUserId());
		userRoleServiceImpl.setAuthorRole(appUser.getUserId());
		//add to author list
		Author author = new Author();
		author.setAppUser(appUser);
		authorServiceImpl.addToAuthorList(author);
		return "redirect:/register_success";
	}
	
	private String getSiteURL(HttpServletRequest request) {
	        String siteURL = request.getRequestURL().toString();
	        return siteURL.replace(request.getServletPath(), "");
	}
	
	@RequestMapping("/verify_success")
	public String verifySuccess() {
		return "verify_success";
	}
	
	@RequestMapping("/verify_fail")
	public String verifyFail() {
		return "verify_fail";
	}
	
	@GetMapping("/verify")
	public String verifyUser(@Param("code") String code) {
		System.out.println("code = "+code+".");
	    if (appUserService.emailVerify(code)==true) {
	        return "verify_success";
	    } else {
	        return "verify_fail";
	    }
	}
}