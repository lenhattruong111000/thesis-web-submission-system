package org.hcmiu.submission_system.spring.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.sql.Date;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.mail.MessagingException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hcmiu.submission_system.spring.entity.AppUser;
import org.hcmiu.submission_system.spring.entity.CoAuthor;
import org.hcmiu.submission_system.spring.entity.FileDB;
import org.hcmiu.submission_system.spring.entity.ManuscriptReview;
import org.hcmiu.submission_system.spring.entity.SubmissionInfor;
import org.hcmiu.submission_system.spring.service.AppUserService;
import org.hcmiu.submission_system.spring.service.CoAuthorService;
import org.hcmiu.submission_system.spring.service.FileDBService;
import org.hcmiu.submission_system.spring.service.ManuscriptReviewService;
import org.hcmiu.submission_system.spring.service.SubmissionInforService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import classes.Copyleaks;
import models.exceptions.AuthExpiredException;
import models.exceptions.CommandException;
import models.exceptions.RateLimitException;
import models.exceptions.UnderMaintenanceException;
import models.response.CopyleaksAuthToken;
import models.submissions.CopyleaksFileSubmissionModel;
import models.submissions.CopyleaksURLSubmissionModel;
import models.submissions.properties.SubmissionProperties;
import models.submissions.properties.SubmissionWebhooks;

@Controller
public class SubmissionController {
	
	@Autowired
	private AppUserService appUserService;
	
	@Autowired
	private SubmissionInforService submissionInforService;
	
	@Autowired
	private FileDBService fileDBService;
	
	@Autowired
	private CoAuthorService coAuthorService;
	
	@Autowired
	private ManuscriptReviewService reviewService;
	
	
	@RequestMapping("/verify_first")
	public String VerificationRequestMessage() {
		return "verify_first";
	}
	@RequestMapping("/emailError")
	public String errorEmail() {
		return "emailError";
	}
	@GetMapping("/submissionForm")
	public String getSubmissionForm(Model model, Principal principal) {
		submissionInforService.setMaxAllowedPacket();
		System.out.println(principal.getName());
		AppUser appUser = appUserService.getUserByUserName(principal.getName());
		System.out.println(appUser.isEnabled());
		
		if(appUser.isEnabled()==true) {
			SubmissionInfor submissionInfor =new SubmissionInfor();
			model.addAttribute("manuscript", submissionInfor);
			System.out.println("getok");
			
			return "SubmissionForm";
			
		}else {
			return "verify_first";
		}
		
	}
	
	
	@PostMapping("/saveSubmissionInfor")
	public String saveSubmissionForm(@ModelAttribute("manuscript") SubmissionInfor submissionInfor,
			@RequestParam("file") MultipartFile file,  Model model, Principal principal) throws IOException{
		//create new file
		FileDB fileDB = new FileDB();
		String fileName = file.getOriginalFilename();
		fileDB.setFileName(fileName);
		fileDB.setContent(file.getBytes());
		fileDB.setSize(file.getSize());
		fileDB.setType("manuscript");
		fileDBService.saveFileDB(fileDB);
		
		submissionInfor.setsState("waiting");
		submissionInforService.saveSubmissionInfor(submissionInfor);
		//set Name and id for author
		List<AppUser> listUser = appUserService.getAllAppUser();
		for(int i=0; i<listUser.size();i++) {
			if(listUser.get(i).getUserName().equals(principal.getName())) {
				//submissionInfor.setAppUser(listUser.get(i));
				submissionInforService.setNameAndIdForAuthor(listUser.get(i).getUserId(), listUser.get(i).getFullName(), submissionInfor.getsId());
				break;
			}
		}
		
		fileDBService.setSubmissionInfoId(submissionInfor.getsId(), fileDB.getId());
		
		return "redirect:/userInfo";
	}
	
	//add co-author
	private long sid=0;
	@GetMapping("/newCoAuthorForm/{id}")
	public String addNewCoauthor(@PathVariable(value = "id") long id, Model model){
		//display co-author that already existed 
		sid=id;
		List<CoAuthor> allCoAuthorList =coAuthorService.getAllCoAuthor();
		ArrayList<CoAuthor> coAuthorList =new ArrayList<CoAuthor>();
		for(int i=0; i<allCoAuthorList.size();i++) {
			if(allCoAuthorList.get(i).getSubmissionInfor().getsId().equals(id)) {
				coAuthorList.add(allCoAuthorList.get(i));
			}
		}
		model.addAttribute("coAuthorList",coAuthorList);
		//create new co-author
		CoAuthor coAuthor =new CoAuthor();
		
		model.addAttribute("newCoAuthor",coAuthor);
		return "newCoAuthorForm";
	}
	//save new co-author
	@PostMapping("/saveCoauthor")
	public String saveNewAuthor(@ModelAttribute("newCoAuthor") CoAuthor coAuthor) throws MessagingException, UnsupportedEncodingException{

		//set manuscript for author
		SubmissionInfor submissionInfor = submissionInforService.getSubmissionInforById(sid);
		coAuthor.setSubmissionInfor(submissionInfor);
		System.out.println(coAuthor.getSubmissionInfor().getsId());
		sid=0;
		
		
		try {
			coAuthorService.notificationEmailForCoAuthor(coAuthor);
		} catch (Exception e) {
			return "/emailError";
		}
		
		coAuthorService.saveCoAuthor(coAuthor);
		
		return "redirect:/userInfo";
	}
	
	//delete co-author
	@GetMapping("deleteCoAuthor/{id}")
	public String deleteCoAuthor(@PathVariable(value = "id") long id) {
		this.coAuthorService.deleteCoAuthorById(id);
		return "redirect:/userInfo";
	}
	//update state
	private long sidForUpdate;
	@GetMapping("/updateStateSubmission/{id}")
	public String getUpdateStateSubmission(@PathVariable(value = "id") long id,Model model) {
		sidForUpdate=id;
		//get all reviewer 
		List<AppUser> reviewerList= appUserService.getAllReviewer();
		model.addAttribute("reviewerList",reviewerList);
		 
		//get review list by manuscript id
		List<ManuscriptReview> reviewList= reviewService.getReviewListByManuscriptId(id);
		model.addAttribute("reviewList", reviewList);
		
		//get submission information by s_id
		SubmissionInfor submissionInfor = submissionInforService.getSubmissionInforById(id);
		model.addAttribute("manuscript",submissionInfor);
			
		return "updateStateSubmission";
	}
	//check plagiarism
	 private static final String EMAIL_ADDRESS = "jobandjob336@gmail.com";
	 private static final String KEY = "35e1466e-6eb2-40e9-adfb-328ec2b79f2b";
//	 private static final String PRODUCT = Products.EDUCATION; // BUSINESSES or EDUCATION, depending on your Copyleaks account type.
	
	 
	 
	@RequestMapping(value="/resultChecking", method = RequestMethod.GET)
	public String plagiarismCheck() throws ExecutionException, UnderMaintenanceException, RateLimitException, CommandException, InterruptedException {
		
		CopyleaksAuthToken token=null;
		token = Copyleaks.login(EMAIL_ADDRESS, KEY);

        System.out.println("Logged successfully!\nToken:");
        System.out.println(token.getAccessToken());
        
     // This example is going to scan a FILE for plagiarism.
        // Alternatively, you can scan a URL using the class `UrlDocument`.
        
//        System.out.println("Submitting a new file...");
//        String BASE64_FILE_CONTENT = null;
//        String FILENAME = null;
//        String scanId = null;
//        SubmissionProperties submissionProperties = new SubmissionProperties(new SubmissionWebhooks("http://localhost:8080/copyleaks/{status}/SCAN_ID"));
//        submissionProperties.setSandbox(true); //Turn on sandbox mode. Turn off on production.
//        
//        Optional<FileDB> temp = Optional.ofNullable(fileDBService.getFileDBById(sidForUpdate));
//		if(temp!=null) {
//			FileDB fileDB = temp.get();
//			FILENAME =fileDB.getFileName();
//			scanId= String.valueOf(fileDB.getSubmissionInfor().getsId());
//			System.out.println("fileBD content"+fileDB.getContent());
//			BASE64_FILE_CONTENT = Base64.getEncoder().encodeToString(fileDB.getContent().toString().getBytes(StandardCharsets.UTF_8));
//			CopyleaksFileSubmissionModel model = new CopyleaksFileSubmissionModel(BASE64_FILE_CONTENT, FILENAME, null);
//	        
//			System.out.println("fileName: "+FILENAME);
//			System.out.println("scanId: "+ scanId);
//			System.out.println("BASE64_FILE_CONTENT: "+ BASE64_FILE_CONTENT);
        	
        SubmissionProperties submissionProperties = new SubmissionProperties(new SubmissionWebhooks("https://localhost:8080/resultChecking"));
        String scanId= "1";
        	String url="http://localhost:8080/downloadfile/1";
        	CopyleaksURLSubmissionModel urlFile= new CopyleaksURLSubmissionModel(url, null );
			try {
	            Copyleaks.submitUrl(token, scanId, urlFile);
	        } catch (ParseException e) {
	            System.out.println(e.getMessage() + "\n");
	            e.printStackTrace();
	            
	        } catch (AuthExpiredException e) {
	            System.out.println(e.getMessage() + "\n");
	            e.printStackTrace();
	            
	        } catch (UnderMaintenanceException e) {
	            System.out.println(e.getMessage() + "\n");
	            e.printStackTrace();
	            
	        } catch (CommandException e) {
	            System.out.println(e.getMessage() + "\n");
	            e.printStackTrace();
	            
	        } catch (ExecutionException e) {
	            System.out.println(e.getMessage() + "\n");
	            e.printStackTrace();
	            
	        } catch (InterruptedException e) {
	            System.out.println(e.getMessage() + "\n");
	            e.printStackTrace();
	            
	        }
	        System.out.println("Send to scanning");
	        System.out.printf("You will be notified, using your webhook, once the scan was completed.");


			
			
//		}
        
//		token= Copyleaks.login("jobandjob336@gmail.com", "35e1466e-6eb2-40e9-adfb-328ec2b79f2b");
//		
//		
		
		return "resultChecking";
	}
	
	// save update state and add the comment
	@PostMapping("/saveUpdateState")
	public String saveUpdateState(@ModelAttribute("manuscript") SubmissionInfor submissionInfor) {
		System.out.println("mauscript state"+submissionInfor.getsState());
		System.out.println("mauscript id"+ submissionInfor.getsId());
		submissionInforService.updateStateSubmission(submissionInfor.getsState(), submissionInfor.getsId());
		submissionInforService.updateMauscriptComment(submissionInfor.getsComment(), submissionInfor.getsId());
		return "redirect:/editor";
	}
	
	@GetMapping("/downloadfile/{id}")
	 public void downloadFile(@PathVariable(value = "id") long id , Model model, HttpServletResponse response) throws IOException {
		System.out.println("file id:"+id);
		Optional<FileDB> temp = Optional.ofNullable(fileDBService.getFileDBById(id));
		if(temp!=null) {
			FileDB fileDB = temp.get();
			response.setContentType("application/octet-stream");
			String headerKey = "Content-Disposition";
			String headerValue = "attachment; filename = "+fileDB.getFileName();
			response.setHeader(headerKey, headerValue);
			ServletOutputStream outputStream = response.getOutputStream();
			outputStream.write(fileDB.getContent());
			outputStream.close();
		}
	 }
	// final submision
	@GetMapping("/finalSubmission")
	public String getfinalSubmission(Model model) {
		return "finalSubmission";
	}
	//save final submission to database 
	@PostMapping("/saveFinalSubmission")
	public String savefinalSubmission(HttpServletRequest request,
			@RequestParam("file") MultipartFile file, Principal principal ) throws IOException {
		String aid= request.getParameter("aid");
		String sid= request.getParameter("sid");
		List<AppUser> listappUser = appUserService.getAllAppUser();
		List<SubmissionInfor> submissionInfors = submissionInforService.getAllSubmissionInfor();
		AppUser appUser=null;
		//check for user account
		for(int i=0; i<listappUser.size();i++) {
			if(listappUser.get(i).getUserName().equals(principal.getName())) {
				appUser=appUserService.getAppUserById(listappUser.get(i).getUserId());
				break;
			}
		}
		//check for manuscript is own by user or not
		for(int i=0; i<submissionInfors.size();i++) {
			if(submissionInfors.get(i).getAppUser().getUserId().equals(appUser.getUserId()) &&
					submissionInfors.get(i).getsId().equals(Long.valueOf(sid))&&
					submissionInfors.get(i).getsState().equals("accept")) {
				FileDB fileDB = fileDBService.getFileDBById(Long.valueOf(sid));
				
				fileDB.setType("manuscript/finalSubmit");
				fileDB.setSfileName(file.getOriginalFilename());
				fileDB.setScontent(file.getBytes());
				fileDB.setSsize(file.getSize());
				
				fileDBService.saveFileDB(fileDB);
				
			}
		}
		return "redirect:/userInfo";
	}
	
	//send manuscript for reviewer
	private long rid=0;
	private long mid=0;
	@GetMapping("/send/{rid}/{mid}")
	public String sentManuscriptForReviewer(@PathVariable(value = "rid") long rid, @PathVariable(value = "mid") long mid, Model model) {
		System.out.println("rid="+ rid);
		System.out.println("mid"+mid);
		this.rid=rid;
		this.mid=mid;
		ManuscriptReview manuscriptReview =new ManuscriptReview();
		model.addAttribute("manuscriptReview",manuscriptReview);
		return "send_manuscript";
	}
	
	@PostMapping("/sendManuscript")
	public String sendManuscript(@ModelAttribute("manuscriptReview") ManuscriptReview manuscriptReview, HttpServletRequest request) throws UnsupportedEncodingException, MessagingException {
		
		//get manuscript by id
		SubmissionInfor submissionInfor= new SubmissionInfor();
		submissionInfor = submissionInforService.getSubmissionInforById(mid);
		
		//get reviewer by id
		AppUser appUser = new AppUser();
		appUser = appUserService.getAppUserById(rid);
		
		System.out.println("deadline Date:"+request.getParameter("deadlineDate"));
		System.out.println("deadline Time:"+request.getParameter("deadlineTime"));
		
		
		manuscriptReview.setSubmissionInfor(submissionInfor);
		manuscriptReview.setAppUser(appUser);
		manuscriptReview.setReviewerState("waiting");
		manuscriptReview.setsDeadlinedate(Date.valueOf(request.getParameter("deadlineDate")));
		manuscriptReview.setsDeadlinetime(Time.valueOf(request.getParameter("deadlineTime")+":00"));
		
		System.out.println("reviewerId: " +manuscriptReview.getAppUser().getUserId());
		System.out.println("reviewerName: " +manuscriptReview.getAppUser().getUserName());
		System.out.println("mid : " +manuscriptReview.getSubmissionInfor().getsId());
		System.out.println("mName : " +manuscriptReview.getSubmissionInfor().getsAuthorname());
		
		reviewService.saveManuscriptReview(manuscriptReview);
		
		//send mail for reviewer
		appUserService.emailForNotifyReviewer(appUser);
		
		return "redirect:/updateStateSubmission/"+mid;
	}
	
	//Evaluate the manuscript of reviewer
	@GetMapping("/reviewManuscript/{id}")
	public String ReviewManuscript(@PathVariable(value = "id") long id,Model model, Principal principal) {
		
		//get Manuscript Information
		SubmissionInfor submissionInfor = submissionInforService.getSubmissionInforById(id);
		model.addAttribute("manuscript",submissionInfor);
		
		//get manuscript review
		long r_id =appUserService.getUserByUserName(principal.getName()).getUserId();
		ManuscriptReview manuscriptReview = reviewService.getManuscriptReviewByRIDAndSID(r_id, id);
		model.addAttribute("review", manuscriptReview);
		
		
		return "reviewManuscript";
	}
	
	@PostMapping("/saveReviewManuscript")
	public String saveReviewManuscript(@ModelAttribute("review") ManuscriptReview manuscriptReview, @ModelAttribute("manuscript") SubmissionInfor submissionInfor, Principal principal ) {
		java.util.Date currentDate= new java.util.Date();
		DateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		Calendar cal = Calendar.getInstance();
	    cal.setTime(new java.util.Date());
	    cal.add(Calendar.HOUR, 24);
	    
	    
		//SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
		//SimpleDateFormat time = new SimpleDateFormat("hh:mm:ss");
	    LocalDate curDate =LocalDate.now();
	    LocalTime curTime = LocalTime.now();
	    
	 
	    System.out.println("curentTime"+curTime);
		String date=curDate.toString();
		String time=String.valueOf(curTime.getHour())+":"+String.valueOf(curTime.getMinute())+":"+String.valueOf(curTime.getSecond());
		System.out.println("date: "+date);
		System.out.println("time: "+time);
		
		Date reviewDate= Date.valueOf(date); 
		Time reviewTime= Time.valueOf(time);
		
		System.out.println("review date: "+ reviewDate);
		System.out.println("review time: "+ reviewTime);
		
		String dateTimeReview= date+" "+time;
		ManuscriptReview review = reviewService.getManuscriptReviewByRIDAndSID(appUserService.getUserByUserName(principal.getName()).getUserId(), submissionInfor.getsId());
		
		String dateTimeDeadline= review.getsDeadlinedate().toString()+" "+ review.getsDeadlinetime().toString(); 
		System.out.println("date time deadline: "+dateTimeDeadline);
		
		try {			 
	    	 
	    	 java.util.Date date1 = simpleDateFormat.parse(dateTimeReview);
	    	 java.util.Date date2 = simpleDateFormat.parse(dateTimeDeadline);

	    	 long getDiff = date2.getTime() - date1.getTime();
	    	 
	    	 
	    	 System.out.println("getDifff: "+ getDiff);
	    	 
	    	 
	    	 if (getDiff<0) {
	    		 reviewService.updateDateAndTimeReview(reviewDate, reviewTime, true,  appUserService.getUserByUserName(principal.getName()).getUserId(), submissionInfor.getsId());
	    	 }else {
	    		 reviewService.updateDateAndTimeReview(reviewDate, reviewTime, false,  appUserService.getUserByUserName(principal.getName()).getUserId(), submissionInfor.getsId());
	    	 }
	    	 // using TimeUnit class from java.util.concurrent package
	    	 //long getDaysDiff = TimeUnit.MILLISECONDS.toDays(getDiff);
	    	// System.out.println("Duration is: " + getDaysDiff + " days.");
	    	 
	    	} catch (Exception e) {
	    		e.printStackTrace();
	    	  }
		
		
		reviewService.updateStateAndComment(manuscriptReview.getReviewerState(), manuscriptReview.getReviewerComment(), appUserService.getUserByUserName(principal.getName()).getUserId(), submissionInfor.getsId());
		
		return "redirect:/reviewer";
	}
}
