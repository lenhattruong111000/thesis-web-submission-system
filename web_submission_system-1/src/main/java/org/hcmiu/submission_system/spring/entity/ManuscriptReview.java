package org.hcmiu.submission_system.spring.entity;

import java.sql.Date;
import java.sql.Time;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "Manuscript_Review")
public class ManuscriptReview {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="reviewerId", nullable = true)
	private AppUser appUser;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="sId", nullable = true)
	private SubmissionInfor submissionInfor;
	
	@Column(name = "sDeadlinedate", nullable = true)
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date sDeadlinedate;
	
	@Column(name = "sDeadlinetime", nullable = true)
	@DateTimeFormat(pattern="hh:mm:ss")
	private Time sDeadlinetime;
	
	@Column(name = "reviewDate", nullable = true)
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date reviewDate;
	
	@Column(name = "reviewTime", nullable = true)
	@DateTimeFormat(pattern="hh:mm:ss")
	private Time reviewTime;
	
	@Column(name = "isLate", nullable = true)
	private boolean isLate = false;
	
	@Column(name = "reviewerState", length = 30, nullable = true)
	private String reviewerState;
	
	@Column(name = "reviewerComment", length = 65535, nullable = true)
	private String reviewerComment;
	
	@Column(name = "editorComment", length = 65535, nullable = true)
	private String editorComment;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public AppUser getAppUser() {
		return appUser;
	}

	public void setAppUser(AppUser appUser) {
		this.appUser = appUser;
	}

	public SubmissionInfor getSubmissionInfor() {
		return submissionInfor;
	}

	public void setSubmissionInfor(SubmissionInfor submissionInfor) {
		this.submissionInfor = submissionInfor;
	}

	public String getReviewerState() {
		return reviewerState;
	}

	public void setReviewerState(String reviewerState) {
		this.reviewerState = reviewerState;
	}

	public String getReviewerComment() {
		return reviewerComment;
	}

	public void setReviewerComment(String reviewerComment) {
		this.reviewerComment = reviewerComment;
	}

	public String getEditorComment() {
		return editorComment;
	}

	public void setEditorComment(String editorComment) {
		this.editorComment = editorComment;
	}

	public Date getsDeadlinedate() {
		return sDeadlinedate;
	}

	public void setsDeadlinedate(Date sDeadlinedate) {
		this.sDeadlinedate = sDeadlinedate;
	}

	public Time getsDeadlinetime() {
		return sDeadlinetime;
	}

	public void setsDeadlinetime(Time sDeadlinetime) {
		this.sDeadlinetime = sDeadlinetime;
	}

	public boolean isLate() {
		return isLate;
	}

	public void setLate(boolean isLate) {
		this.isLate = isLate;
	}

	public Date getReviewDate() {
		return reviewDate;
	}

	public void setReviewDate(Date reviewDate) {
		this.reviewDate = reviewDate;
	}

	public Time getReviewTime() {
		return reviewTime;
	}

	public void setReviewTime(Time reviewTime) {
		this.reviewTime = reviewTime;
	}
	
	
	
	
}
