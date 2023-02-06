package org.hcmiu.submission_system.spring.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
@Entity
@Table(name = "reviewer")
public class Reviewer {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = true)
	private Long id;
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "userId", nullable = true)
	private AppUser appUser;
	
	@Column(name ="masterField", length =255, nullable = true)
	private String masterField;
	
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
	public String getMasterField() {
		return masterField;
	}
	public void setMasterField(String masterField) {
		this.masterField = masterField;
	}
	
}
