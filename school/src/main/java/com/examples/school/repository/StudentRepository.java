package com.examples.school.repository;

import java.util.List;

import com.examples.school.model.Student;

public interface StudentRepository {
	
	List<Student> findAll();
	
	Student findById(String id);
	
	void save(Student student);
	
	void delete(String id);
}
