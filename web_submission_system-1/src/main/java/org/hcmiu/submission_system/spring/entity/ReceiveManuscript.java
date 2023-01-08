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
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "receivemanuscript", uniqueConstraints = {
		@UniqueConstraint(name = "SUBMISSION_UK" ,columnNames = "s_id")} )
public class ReceiveManuscript {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = true)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "s_id", nullable = true)
	private SubmissionInfor submissionInfor;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "editor_id", nullable = true)
	private Editor editor;

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

	public Editor getEditor() {
		return editor;
	}

	public void setEditor(Editor editor) {
		this.editor = editor;
	}
	
	

}
