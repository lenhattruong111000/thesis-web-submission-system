package org.hcmiu.submission_system.spring.service;

import java.util.List;
import java.util.Optional;


import org.hcmiu.submission_system.spring.entity.FileDB;
import org.hcmiu.submission_system.spring.repository.FileDBRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FileDBServiceImpl implements FileDBService {
	
	@Autowired
	private FileDBRepository fileDBRepository;
	
	@Override
	public List<FileDB> getAllFileDB() {
		
		return fileDBRepository.findAll();
	}

	@Override
	public FileDB getFileDBById(long id) {
		Optional<FileDB> optional = fileDBRepository.findById(id);
    	FileDB fileDB =null;
	
    	if(optional.isPresent()) {
    		fileDB= optional.get();
    	}else {
    		throw new RuntimeException("The file not found by id: "+id);
    	}
    	return fileDB;
	}

	@Override
	public FileDB saveFileDB(FileDB fileDB) {
		
		return fileDBRepository.save(fileDB);
	}

	@Override
	public void setSubmissionInfoId(long sid, long id) {
		this.fileDBRepository.setSubmissionInfoId(sid, id);
		
	}

	@Override
	public FileDB getFileDBBySid(long id) {
		
		return fileDBRepository.getFileBySid(id);
	}

}
