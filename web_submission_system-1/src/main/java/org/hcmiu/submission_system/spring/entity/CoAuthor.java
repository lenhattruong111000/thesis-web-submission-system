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
@Table(name ="coauthor")
public class CoAuthor {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = true)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="sId", nullable = true)
	private SubmissionInfor submissionInfor;
	
	@Column(name = "coFullname", length = 30, nullable = true)
	private String coFullname;
	
	@Column(name = "coEmail", length = 50, nullable = true)
	private String coEmail;
	
	@Column(name = "coCountry", length = 50, nullable = true)
	private String coCountry;
	
	@Column(name = "coOrganization", length = 100, nullable = true)
	private String coOrganization;
	
	@Column(name = "coWebpage", length = 255, nullable = true)
	private String coWebpage;
	
	@Column(name = "coCorrespondingauthor", nullable = true)
	private Boolean coCorrespondingauthor;
	
	public CoAuthor() {
		
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public SubmissionInfor getSubmissionInfor() {
		return submissionInfor;
	}

	public void setSubmissionInfor(SubmissionInfor submissionInfor) {
		this.submissionInfor = submissionInfor;
	}

	public String getCoFullname() {
		return coFullname;
	}

	public void setCoFullname(String coFullname) {
		this.coFullname = coFullname;
	}

	public String getCoEmail() {
		return coEmail;
	}

	public void setCoEmail(String coEmail) {
		this.coEmail = coEmail;
	}

	public String getCoCountry() {
		return coCountry;
	}

	public void setCoCountry(String coCountry) {
		this.coCountry = coCountry;
	}

	public String getCoOrganization() {
		return coOrganization;
	}

	public void setCoOrganization(String coOrganization) {
		this.coOrganization = coOrganization;
	}

	public String getCoWebpage() {
		return coWebpage;
	}

	public void setCoWebpage(String coWebpage) {
		this.coWebpage = coWebpage;
	}

	public Boolean getCoCorrespondingauthor() {
		return coCorrespondingauthor;
	}

	public void setCoCorrespondingauthor(Boolean coCorrespondingauthor) {
		this.coCorrespondingauthor = coCorrespondingauthor;
	}

	
	
	
}
