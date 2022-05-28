package org.hcmiu.submission_system.spring.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="files")
public class FileDB {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	 
	@Column( name = "type", length = 30 ,nullable = true)
	private String type;
	 
	@Column( name ="fileName", length = 255, nullable = true)
	private String fileName;
	 
	@Column(name ="size", nullable = true)
	private long size;
	 
	@Lob
	private byte [] content;
	 
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name ="sId", nullable = true)
	private SubmissionInfor submissionInfor;
	 
	@Column( name ="sfileName", length = 255, nullable = true)
	private String sfileName;
	 
	@Column(name ="ssize", nullable = true)
	private long ssize;
	
	@Lob
	private byte [] scontent;
	 
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
	public byte[] getContent() {
		return content;
	}
	public void setContent(byte[] content) {
		this.content = content;
	}
	public SubmissionInfor getSubmissionInfor() {
		return submissionInfor;
	}
	public void setSubmissionInfor(SubmissionInfor submissionInfor) {
		this.submissionInfor = submissionInfor;
	}
	public String getSfileName() {
		return sfileName;
	}
	public void setSfileName(String sfileName) {
		this.sfileName = sfileName;
	}
	public long getSsize() {
		return ssize;
	}
	public void setSsize(long ssize) {
		this.ssize = ssize;
	}
	public byte[] getScontent() {
		return scontent;
	}
	public void setScontent(byte[] scontent) {
		this.scontent = scontent;
	}
	
	 
	 
}
