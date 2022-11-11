package org.hcmiu.submission_system.spring.service;

import java.util.List;

import org.hcmiu.submission_system.spring.entity.FileDB;

public interface FileDBService {
	
	public List<FileDB> getAllFileDB();
	
	public FileDB getFileDBById(long id);
	
	public FileDB saveFileDB(FileDB fileDB); 
	
	public void setSubmissionInfoId(long sid, long id);
	
}
