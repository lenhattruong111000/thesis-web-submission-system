package org.hcmiu.submission_system.spring.service;

import org.hcmiu.submission_system.spring.entity.Author;
import org.hcmiu.submission_system.spring.repository.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthorServiceImpl implements AuthorService {
	
	@Autowired
	private AuthorRepository authorRepository;

	@Override
	public void addToAuthorList(Author author) {
		this.authorRepository.save(author);
		
	}

}
