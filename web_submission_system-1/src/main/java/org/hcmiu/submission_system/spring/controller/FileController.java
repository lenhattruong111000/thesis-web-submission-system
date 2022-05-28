package org.hcmiu.submission_system.spring.controller;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hcmiu.submission_system.spring.entity.AppUser;
import org.hcmiu.submission_system.spring.entity.FileDB;
import org.hcmiu.submission_system.spring.entity.SubmissionInfor;
import org.hcmiu.submission_system.spring.service.AppUserService;
import org.hcmiu.submission_system.spring.service.FileDBService;
import org.hcmiu.submission_system.spring.service.SubmissionInforService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class FileController {
	
	@Autowired
	private AppUserService appUserService;
	
	@Autowired
	private SubmissionInforService submissionInforService;
	
	@Autowired
	private FileDBService fileDBService;
	

	@GetMapping("/submissionForm")
	public String getSubmissionForm(Model model) {
		SubmissionInfor submissionInfor =new SubmissionInfor();
		model.addAttribute("manuscript", submissionInfor);
		System.out.println("getok");
		return "SubmissionForm";
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
	
	//update state
	@GetMapping("/updateStateSubmission/{id}")
	public String getUpdateStateSubmission(@PathVariable(value = "id") long id,Model model) {
		SubmissionInfor submissionInfor = submissionInforService.getSubmissionInforById(id);
		model.addAttribute("manuscript",submissionInfor);
		return "updateStateSubmission";
	}
	
	@PostMapping("/saveUpdateState")
	public String saveUpdateState(@ModelAttribute("manuscript") SubmissionInfor submissionInfor) {
		System.out.println("mauscript state"+submissionInfor.getsState());
		System.out.println("mauscript id"+ submissionInfor.getsId());
		submissionInforService.updateStateSubmission(submissionInfor.getsState(), submissionInfor.getsId());
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
	
	@GetMapping("/finalSubmission")
	public String getfinalSubmission(Model model) {
		return "finalSubmission";
	}
	
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
}
