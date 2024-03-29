package org.hcmiu.submission_system.spring.controller;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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

import org.apache.pdfbox.pdmodel.PDDocument;
import org.hcmiu.submission_system.spring.entity.AppUser;
import org.hcmiu.submission_system.spring.entity.CoAuthor;
import org.hcmiu.submission_system.spring.entity.FileDB;
import org.hcmiu.submission_system.spring.entity.ManuscriptReview;
import org.hcmiu.submission_system.spring.entity.Reviewer;
import org.hcmiu.submission_system.spring.entity.SubmissionInfor;
import org.hcmiu.submission_system.spring.service.AppUserService;
import org.hcmiu.submission_system.spring.service.CoAuthorService;
import org.hcmiu.submission_system.spring.service.FileDBService;
import org.hcmiu.submission_system.spring.service.FileDBServiceImpl;
import org.hcmiu.submission_system.spring.service.ManuscriptReviewService;
import org.hcmiu.submission_system.spring.service.ReviewerService;
import org.hcmiu.submission_system.spring.service.SubmissionInforService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
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
	
	@Autowired
	private ReviewerService reviewerService;
	
	
	@RequestMapping("/verify_first")
	public String VerificationRequestMessage() {
		return "verify_first";
	}
	@RequestMapping("/emailError")
	public String errorEmail() {
		return "emailError";
	}
	@RequestMapping("/oversizeMessage")
	public String oversizeFile() {
		return "oversizeMessage";
	}
	
	@RequestMapping("/overLenghtMessage")
	public String overLenghtMessage(Model model) {
		model.addAttribute("wordsCounted", wordsCounted );
		model.addAttribute("keywordNum", keywordNum );
		model.addAttribute("pageNumPDf", pageNumPDf );
		return "overLengthMessage";
	}
	
	
	//======================================Submission Manuscript==================================//
	@GetMapping("/submissionForm")
	public String getSubmissionForm(Model model, Principal principal) {
		
		submissionInforService.setMaxAllowedPacket();
		System.out.println(principal.getName());
		AppUser appUser = appUserService.getUserByUserName(principal.getName());
		System.out.println(appUser.isEnabled());
		
			///////
			if(appUser.isEnabled()==true && appUser.getVerificationCode()!=null) {
				return "verifyLogin";
			}
			//////
		
		if(appUser.isEnabled()==true) {
			SubmissionInfor submissionInfor =new SubmissionInfor();
			model.addAttribute("manuscript", submissionInfor);
			System.out.println("getok");
			
			return "SubmissionForm";
			
		}else {
			return "verify_first";
		}
		
	}
	private int keywordNum=0;
	private int wordsCounted=0; 
	private int pageNumPDf=0;
	@PostMapping("/saveSubmissionInfor")
	public String saveSubmissionForm(@ModelAttribute("manuscript") SubmissionInfor submissionInfor,
			@RequestParam("file") MultipartFile file,  Model model, Principal principal) throws IOException{
		String abstractParagraph =submissionInfor.getsAbstract();
		final char SPACE =' ';
		final char BREAK_LINE = '\n';
		final char TAB ='\n';
		
		//keyword counted
		String allKeyword= submissionInfor.getsKeyword();
		keywordNum=0;
		boolean isCounted = true;
		
		for(int i=0; i<allKeyword.length();i++) {
			if (allKeyword.charAt(i) != BREAK_LINE) {
                         
                if(isCounted==true) {
                    System.out.println(allKeyword.charAt(i)+"_");
                    keywordNum++;
                    isCounted = false;
                }
            } else {
                isCounted = true;
            }
		}
		System.out.println("number of keyword: "+ keywordNum);
		
		
		// words counted (abstract paragraph) 
        int count = 0;
        isCounted = true;
        
        for (int i = 0; i < abstractParagraph.length(); i++) {
            if ( abstractParagraph.charAt(i) != SPACE && abstractParagraph.charAt(i) != TAB 
                    && abstractParagraph.charAt(i) != BREAK_LINE) {
                         
                if(isCounted==true) {
                    System.out.println(abstractParagraph.charAt(i)+"_");
                    count++;
                    isCounted = false;
                }
            } else {
                isCounted = true;
            }
        }
        
        System.out.println("wordsCount: "+ count);
        //wordsCounted =String.valueOf(count);
        wordsCounted =count;
        
        // the abstract paragraph length from 100 to 200 words.
        if(count<=200 && count>=100 && keywordNum>= 1 && keywordNum<=5) {
        	//get file extension
    		String nameFile= file.getOriginalFilename();
			String fileExtension = null;
        	try {
        	
			System.out.println("length: "+nameFile.length());
			for(int i=nameFile.length()-1;i>=0;i--) {
				
				System.out.println(nameFile.charAt(i));
				if(nameFile.charAt(i)=='.') {
					System.out.println(i);
					fileExtension =nameFile.substring(i+1,nameFile.length());
					break;
				}		
			}
			System.out.println(nameFile);
			System.out.println("Flie Extension: "+fileExtension);
			
			if(fileExtension.equals("pdf")) {
				 PDDocument documentPdf= PDDocument.load(file.getBytes());
				 pageNumPDf= documentPdf.getNumberOfPages();
				 System.out.println("Number of pdf page"+ pageNumPDf);
				 documentPdf.close();
			}
			
			if(fileExtension.equals("pdf") && (pageNumPDf>20 || pageNumPDf< 5)) {
        		return "redirect:/overLenghtMessage";
        	}
        		
			} catch (Exception e) {
				System.out.println("can not find number of page");
			}
        	
        	
        	//50MB= 52428800 B
        	if(file.getSize()<= 52428800 ) {
        		
        		FileDB fileDB = new FileDB();
        		String fileName = file.getOriginalFilename();
        		fileDB.setFileName(fileName);
        		fileDB.setContent(file.getBytes());
        		fileDB.setSize(file.getSize());
        		fileDB.setType("manuscript"); 
        		fileDBService.saveFileDB(fileDB);
			
        		//create new co-author
        		CoAuthor coauthor= new CoAuthor();
        		//set state for new submission
        		submissionInfor.setsState("waiting");
        		submissionInfor.setFileDB(fileDB);
        		submissionInforService.saveSubmissionInfor(submissionInfor);
        		//set Name and id for author
        		List<AppUser> listUser = appUserService.getAllAppUser();
        		for(int i=0; i<listUser.size();i++) {
        			if(listUser.get(i).getUserName().equals(principal.getName())) {
        				//submissionInfor.setAppUser(listUser.get(i));
        				submissionInforService.setNameAndIdForAuthor(listUser.get(i).getUserId(), listUser.get(i).getFullName(), submissionInfor.getsId());
					 
        				//set information for new co-author
        				coauthor.setSubmissionInfor(submissionInfor);
        				coauthor.setCoFullname(listUser.get(i).getFullName());
        				coauthor.setCoEmail(listUser.get(i).getUserEmail());
        				coauthor.setCoOrganization(submissionInfor.getsWorkplace());
        				coauthor.setCoCountry(submissionInfor.getsCountry());
        				coauthor.setCoWebpage("");
        				coauthor.setCoCorrespondingauthor(true);
        			}
        		}
			
        		//fileDBService.setSubmissionInfoId(submissionInfor.getsId(), fileDB.getId());
        		//save new co-author
        		coAuthorService.saveCoAuthor(coauthor);
			
        		return "redirect:/userInfo";
			
        	} else return "/oversizeMessage";
        }
       
		return "redirect:/overLenghtMessage";
	}
	//=======================================Co-Author====================================================//
	//add co-author
	private long sid=0;
	@GetMapping("/newCoAuthorForm/{id}")
	public String addNewCoauthor(@PathVariable(value = "id") long id, Model model, Principal principal){
		
			///////
			AppUser getAppUser = appUserService.getUserByUserName(principal.getName());
			if(getAppUser.isEnabled()==true && getAppUser.getVerificationCode()!=null) {
				return "verifyLogin";
			}
			//////
		
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
	
	//===================================Update State of Manuscript==========================================//
	
	//update state
	private long sidForUpdate;
	private String keyword;
	@GetMapping("/updateStateSubmission/{id}")
	public String getUpdateStateSubmission(@PathVariable(value = "id") long id,Model model, Principal principal) {
		
		///////
		AppUser getAppUser = appUserService.getUserByUserName(principal.getName());
		if(getAppUser.isEnabled()==true && getAppUser.getVerificationCode()!=null) {
			return "verifyLogin";
		}
		//////
		
		//get submission information by s_id
		sidForUpdate =id;
		SubmissionInfor submissionInfor = submissionInforService.getSubmissionInforById(id);
		model.addAttribute("manuscript",submissionInfor);
		
		//=======================================================================//
		
		//get all reviewer 
		List<AppUser> reviewerList= appUserService.getAllReviewer();
		
		//find Reviewer by field.
		//String searchField = request.getParameter("search_fields");
		System.out.println(keyword);
		if(keyword!=null) {
			
			List<Reviewer> recommendReviewerList = reviewerService.findReviewerByField(keyword);
			model.addAttribute("reviewerList",recommendReviewerList);
			keyword=null;

		} else {
			//List<AppUser> recommendReviewerList = appUserService.getRecommendReviewerList(submissionInfor.getsMajor());
			List<Reviewer> recommendReviewerList = reviewerService.getRecommentReviewerList(submissionInfor.getsMajor());
			model.addAttribute("reviewerList",recommendReviewerList);
		}
		
		
		//=======================================================================//
				
		//get review list by manuscript id
		List<ManuscriptReview> reviewList= reviewService.getReviewListByManuscriptId(id);
		model.addAttribute("reviewList", reviewList);
		
		//get all reviewer comment with id of manuscript
		String allComment = ""; 
		try {
			if(reviewList!= null) {
				for(int i=0; i<reviewList.size();i++) {
					int num=i+1;
					
					allComment= allComment+ "Reviewer "+ num +": \n"+
					reviewList.get(i).getReviewerComment()+"\n";
					
					num=0;
				}
				allComment=allComment+"Editor messages: \n";
				submissionInforService.updateMauscriptComment(allComment, id);
				allComment="";
			}
			
			
		}catch(Exception e) {
			System.out.println("not review yet");
		}
			
		return "updateStateSubmission";
	}
	//search reviewer by master_fields
	@GetMapping("/search")
	public String search(HttpServletRequest request, Model model) {
		keyword = request.getParameter("keyword");
		System.out.println("keyword:"+ keyword );
//		List<Reviewer> recommendReviewerList = reviewerService.findReviewerByField();
//		model.addAttribute("reviewerList",recommendReviewerList);
		return "redirect:/updateStateSubmission/"+ sidForUpdate;
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
	public String saveUpdateState(@ModelAttribute("manuscript") SubmissionInfor submissionInfor) throws UnsupportedEncodingException, MessagingException {
		System.out.println("mauscript state"+submissionInfor.getsState());
		System.out.println("mauscript id"+ submissionInfor.getsId());
		submissionInforService.updateStateSubmission(submissionInfor.getsState(), submissionInfor.getsId());
		submissionInforService.updateMauscriptComment(submissionInfor.getsComment(), submissionInfor.getsId());
		
		//get mauscript and sent email about its state, comment for author
		SubmissionInfor submission = submissionInforService.getSubmissionInforById(submissionInfor.getsId());
		List<CoAuthor> coAuthors = coAuthorService.getCoAuhtorListByManuscriptId(submissionInfor.getsId());
		for(int i=0; i<coAuthors.size();i++) {
			coAuthorService.emailForNotifyAuthorAndCoAuthorAboutSubmissionState(coAuthors.get(i), submission);
		}
		reviewService.deleteManuscriptReviewBySid(submission.getsId());
		//appUserService.emailForNotifyAuthorAboutSubmissionState(submission.getAppUser(), submission);
		return "redirect:/editor";
	}
	
	//====================================Download Manuscript File====================================================//
	@GetMapping("/downloadfile/{id}")
	public void downloadFile(@PathVariable(value = "id") long id , Model model, HttpServletResponse response) throws IOException {
		
		System.out.println("file id:"+id);
		SubmissionInfor infor = submissionInforService.getSubmissionInforById(id);
		Optional<FileDB> temp = Optional.ofNullable(fileDBService.getFileDBById(infor.getFileDB().getId()));
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
	
	//==========================================Re-Submission================================================//
	@GetMapping("/resubmitManuscript/{id}")
	public String resubmitManuscript(@PathVariable(value = "id") long id, Model model, Principal principal) {
		
		///////
		AppUser getAppUser = appUserService.getUserByUserName(principal.getName());
		if(getAppUser.isEnabled()==true && getAppUser.getVerificationCode()!=null) {
			return "verifyLogin";
		}
		//////
		SubmissionInfor manuscript = submissionInforService.getSubmissionInforById(id);
		model.addAttribute("manuscript", manuscript);
		return "resubmitForm";
	}
	@PostMapping("/saveResubmit")
	public String saveResubmitManuscript(@RequestParam("file") MultipartFile file, 
			@ModelAttribute("manuscript") SubmissionInfor manuscript) throws IOException {
		
		SubmissionInfor submissionInfor =submissionInforService.getSubmissionInforById(manuscript.getsId());
		System.out.println("id: "+submissionInfor.getsId());
		
		System.out.println("name:"+submissionInfor.getsTitle());
		System.out.println("file Id: "+ submissionInfor.getFileDB().getId());
		
		submissionInfor.setsTitle(manuscript.getsTitle());
		submissionInfor.setsAbstract(manuscript.getsAbstract());
		submissionInfor.setsMajor(manuscript.getsMajor());
		submissionInfor.setsWorkplace(manuscript.getsWorkplace());
		submissionInfor.setsKeyword(manuscript.getsKeyword());
		submissionInfor.setsCountry(manuscript.getsCountry());
		
		////////////////////////
		String abstractParagraph =submissionInfor.getsAbstract();
		final char SPACE =' ';
		final char BREAK_LINE = '\n';
		final char TAB ='\n';
		
		//keyword counted
		String allKeyword= submissionInfor.getsKeyword();
		keywordNum=0;
		boolean isCounted = true;
		
		for(int i=0; i<allKeyword.length();i++) {
			if (allKeyword.charAt(i) != BREAK_LINE) {
                         
                if(isCounted==true) {
                    System.out.println(allKeyword.charAt(i)+"_");
                    keywordNum++;
                    isCounted = false;
                }
            } else {
                isCounted = true;
            }
		}
		System.out.println("number of keyword: "+ keywordNum);
		
		
		// words counted (abstract paragraph) 
        int count = 0;
        isCounted = true;
        
        for (int i = 0; i < abstractParagraph.length(); i++) {
            if ( abstractParagraph.charAt(i) != SPACE && abstractParagraph.charAt(i) != TAB 
                    && abstractParagraph.charAt(i) != BREAK_LINE) {
                         
                if(isCounted==true) {
                    System.out.println(abstractParagraph.charAt(i)+"_");
                    count++;
                    isCounted = false;
                }
            } else {
                isCounted = true;
            }
        }
        
        System.out.println("wordsCount: "+ count);
        //wordsCounted =String.valueOf(count);
        wordsCounted =count;
        
        // the abstract paragraph length from 100 to 200 words.
        if(count<=200 && count>=100 && keywordNum>= 1 && keywordNum<=5) {
        	//get file extension
    		String nameFile= file.getOriginalFilename();
			String fileExtension = null;
        	try {
        	
			System.out.println("length: "+nameFile.length());
			for(int i=nameFile.length()-1;i>=0;i--) {
				
				System.out.println(nameFile.charAt(i));
				if(nameFile.charAt(i)=='.') {
					System.out.println(i);
					fileExtension =nameFile.substring(i+1,nameFile.length());
					break;
				}		
			}
			System.out.println(nameFile);
			System.out.println("Flie Extension: "+fileExtension);
			
			if(fileExtension.equals("pdf")) {
				 PDDocument documentPdf= PDDocument.load(file.getBytes());
				 pageNumPDf= documentPdf.getNumberOfPages();
				 System.out.println("Number of pdf page"+ pageNumPDf);
				 documentPdf.close();
			}
			
			if(fileExtension.equals("pdf") && (pageNumPDf>20 || pageNumPDf< 5)) {
        		return "redirect:/overLenghtMessage";
        	}
        		
			} catch (Exception e) {
				System.out.println("can not find number of page");
			}
        	
        	
        	//50MB= 52428800 B
        	if(file.getSize()<= 52428800 ) {
        		FileDB fileDB = fileDBService.getFileDBById(submissionInfor.getFileDB().getId());
        		String fileName = file.getOriginalFilename();
        		fileDB.setFileName(fileName);
        		fileDB.setContent(file.getBytes());
        		fileDB.setSize(file.getSize());
        		fileDB.setType("manuscript");
        		fileDBService.saveFileDB(fileDB);
        		

        		if(submissionInfor.getsState().equals("waiting")==false) {
        			submissionInfor.setsState("re-submit");
        		}else {
        			submissionInfor.setsState("waiting");
        		}
        		
        		submissionInforService.saveSubmissionInfor(submissionInfor);
        		submissionInforService.updateFileId(fileDB.getId(), submissionInfor.getsId());
        		reviewService.deleteManuscriptReviewBySid(submissionInfor.getsId());
        		
        		return "redirect:/userInfo";
			
        	} else return "/oversizeMessage";
        }
		
		////////////////////////
		
		// get file submission by file id
		
		
//		return "redirect:/userInfo";
        return "redirect:/overLenghtMessage";
	}
	
	//==========================================Final Submission============================================//
	@GetMapping("/finalSubmission")
	public String getfinalSubmission(Model model, Principal principal) {
		///////
		AppUser getAppUser = appUserService.getUserByUserName(principal.getName());
		if(getAppUser.isEnabled()==true && getAppUser.getVerificationCode()!=null) {
			return "verifyLogin";
		}
		//////
		return "finalSubmission";
	}
	//save final submission to database 
	@PostMapping("/saveFinalSubmission")
	public String savefinalSubmission(HttpServletRequest request,
			@RequestParam("file") MultipartFile file, Principal principal) throws IOException {
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
					submissionInfors.get(i).getsState().equals("formal_accepted")) {
				FileDB fileDB = fileDBService.getFileDBById(Long.valueOf(submissionInfors.get(i).getFileDB().getId()));
				
				fileDB.setType("manuscript/finalSubmit");
				fileDB.setSfileName(file.getOriginalFilename());
				fileDB.setScontent(file.getBytes());
				fileDB.setSsize(file.getSize());
				
				fileDBService.saveFileDB(fileDB);
				
			}
		}
		return "redirect:/userInfo";
	}
	
	//=======================================Sending Manuscript For Reviewers==============================//
	//send manuscript for reviewer(send one by one)
	private long rid=0;
	private long mid=0;
	@GetMapping("/send/{rid}/{mid}")
	public String sentManuscriptForReviewerPage(@PathVariable(value = "rid") long rid, @PathVariable(value = "mid") long mid, Model model, Principal principal) {
		
		///////
		AppUser getAppUser = appUserService.getUserByUserName(principal.getName());
		if(getAppUser.isEnabled()==true && getAppUser.getVerificationCode()!=null) {
			return "verifyLogin";
		}
		//////
		
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
	public String ReviewManuscriptPage(@PathVariable(value = "id") long id,Model model, Principal principal) {
		
		///////
		AppUser getAppUser = appUserService.getUserByUserName(principal.getName());
		if(getAppUser.isEnabled()==true && getAppUser.getVerificationCode()!=null) {
			return "verifyLogin";
		}
		//////
		
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
	//send more than 1 reviewer at the same time
	
	@GetMapping("/multi_sending")
	public String sendingPage() {
		return "multi_sending";
	}
	private List<AppUser> reviewerList = new ArrayList<AppUser>();
	private long midMutiSending=0;
	@PostMapping("/getSendingList")
	public String SendingAtTheSameTime(HttpServletRequest request) {
		AppUser reviewer =null;
		String[] review_id_list= request.getParameterValues("reviewerList");
		midMutiSending= Long.valueOf(request.getParameter("manuscript_id_multi"));
		for(int i=0; i<review_id_list.length;i++) {
			reviewer= appUserService.getAppUserById(Long.valueOf(review_id_list[i]));
			reviewerList.add(reviewer);
		}
		
		System.out.println(review_id_list[0]);
		System.out.println(review_id_list[1]);
		
		for(int i=0; i<reviewerList.size();i++) {
			System.out.println(reviewerList.get(i).getUserEmail());
		}
		
		return "/multi_sending";
	}
	
	@PostMapping("/multiSending")
	public String multiSending(HttpServletRequest request) throws UnsupportedEncodingException, MessagingException {
		//get manuscript by id
		SubmissionInfor submissionInfor= new SubmissionInfor();
		submissionInfor = submissionInforService.getSubmissionInforById(midMutiSending);
				
		//get reviewer by id
		AppUser reviewer = null;
		ManuscriptReview manuscriptReview =null;
		for(int i=0; i<reviewerList.size();i++) {
			reviewer = appUserService.getAppUserById(reviewerList.get(i).getUserId());
			manuscriptReview =new ManuscriptReview();
			manuscriptReview.setSubmissionInfor(submissionInfor);
			manuscriptReview.setAppUser(reviewer);
			manuscriptReview.setReviewerState("waiting");
			manuscriptReview.setsDeadlinedate(Date.valueOf(request.getParameter("deadlineDate")));
			manuscriptReview.setsDeadlinetime(Time.valueOf(request.getParameter("deadlineTime")+":00"));
			manuscriptReview.setEditorComment(request.getParameter("editorComment"));
			reviewService.saveManuscriptReview(manuscriptReview);
			//send mail for reviewer
			appUserService.emailForNotifyReviewer(reviewer);
		}
		reviewerList.clear();
		
		
				
		System.out.println("deadline Date:"+request.getParameter("deadlineDate"));
		System.out.println("deadline Time:"+request.getParameter("deadlineTime"));
				
		return "redirect:/updateStateSubmission/"+midMutiSending;
	}
}
