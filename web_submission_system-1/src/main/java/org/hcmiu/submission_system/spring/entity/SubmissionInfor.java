package org.hcmiu.submission_system.spring.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Submission_Infor")
public class SubmissionInfor {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long sId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="sAuthorid", nullable = true)
	private AppUser appUser;
	
	@Column(name ="sTitle", nullable =true, length = 100)
	private String sTitle;
	
	@Column(name ="sAuthorname", nullable =true, length =30 )
	private String sAuthorname;
	
	@Column(name ="sMajor", nullable =true, length = 50)
	private String sMajor;
	
	@Column(name ="sWorkplace", nullable =true, length = 100)
	private String sWorkplace;
	
	@Column(name = "sCountry", length = 50, nullable = true)
	private String sCountry;
	
	@Column(name = "sKeyword", length = 255, nullable = true)
	private String sKeyword;
	
	@Column(name ="sAbstract", nullable =true, length = 255)
	private String sAbstract;
	
	@Column(name = "sComment", length = 255, nullable = true)
	private String sComment;
	
	@Column(name ="sState", length = 30 ,nullable =true)
	private String sState;

	public Long getsId() {
		return sId;
	}

	public void setsId(Long sId) {
		this.sId = sId;
	}

	public AppUser getAppUser() {
		return appUser;
	}

	public void setAppUser(AppUser appUser) {
		this.appUser = appUser;
	}

	public String getsTitle() {
		return sTitle;
	}

	public void setsTitle(String sTitle) {
		this.sTitle = sTitle;
	}

	public String getsAuthorname() {
		return sAuthorname;
	}

	public void setsAuthorname(String sAuthorname) {
		this.sAuthorname = sAuthorname;
	}

	public String getsMajor() {
		return sMajor;
	}

	public void setsMajor(String sMajor) {
		this.sMajor = sMajor;
	}

	public String getsWorkplace() {
		return sWorkplace;
	}

	public void setsWorkplace(String sWorkplace) {
		this.sWorkplace = sWorkplace;
	}

	public String getsAbstract() {
		return sAbstract;
	}

	public void setsAbstract(String sAbstract) {
		this.sAbstract = sAbstract;
	}

	public String getsState() {
		return sState;
	}

	public void setsState(String sState) {
		this.sState = sState;
	}

	public String getsCountry() {
		return sCountry;
	}

	public void setsCountry(String sCountry) {
		this.sCountry = sCountry;
	}

	public String getsKeyword() {
		return sKeyword;
	}

	public void setsKeyword(String sKeyword) {
		this.sKeyword = sKeyword;
	}

	public String getsComment() {
		return sComment;
	}

	public void setsComment(String sComment) {
		this.sComment = sComment;
	}
	
	
	
}
