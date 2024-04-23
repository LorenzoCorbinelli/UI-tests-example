package com.examples.school.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.MongoDBContainer;

import com.examples.school.model.Student;
import com.examples.school.repository.StudentRepository;
import com.examples.school.repository.mongo.StudentMongoRepository;
import com.examples.school.view.StudentView;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

public class SchoolControllerIT {
	
	@ClassRule
	public static final MongoDBContainer mongo = new MongoDBContainer("mongo:4.4.3");
	
	private StudentView studentView;
	private StudentRepository studentRepository;
	private SchoolController schoolController;
	private MongoClient client;
	private static final String SCHOOL_DB_NAME = "school";
	private static final String STUDENT_COLLECTION_NAME = "student";
	
	@Before
	public void setup() {
		client = new MongoClient(
				new ServerAddress(
						mongo.getHost(),
						mongo.getFirstMappedPort()));
		studentRepository = new StudentMongoRepository(client, SCHOOL_DB_NAME, STUDENT_COLLECTION_NAME);
		for (Student student : studentRepository.findAll()) {
			studentRepository.delete(student.getId());
		}
		studentView = mock(StudentView.class);
		schoolController = new SchoolController(studentView, studentRepository);
	}
	
	@After
	public void tearDown() {
		client.close();
	}
	
	@Test
	public void testAllStudents() {
		Student student = new Student("1", "test1");
		studentRepository.save(student);
		schoolController.allStudents();
		verify(studentView).showAllStudents(Arrays.asList(student));
	}
	
	@Test
	public void testNewStudent() {
		Student student = new Student("1", "test1");
		schoolController.newStudent(student);
		verify(studentView).studentAdded(student);
	}
	
	@Test
	public void testDeleteStudent() {
		Student student = new Student("1", "test1");
		studentRepository.save(student);
		schoolController.deleteStudent(student);
		verify(studentView).studentRemoved(student);
	}

}
