package com.examples.school.controller;

import static org.mockito.Mockito.ignoreStubs;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import com.examples.school.model.Student;
import com.examples.school.repository.StudentRepository;
import com.examples.school.view.StudentView;

public class SchoolControllerTest {
	
	private StudentView studentView;
	private StudentRepository studentRepository;
	private SchoolController schoolController;
	
	@Before
	public void setup() {
		studentView = mock(StudentView.class);
		studentRepository = mock(StudentRepository.class);
		schoolController = new SchoolController(studentView, studentRepository);
	}

	@Test
	public void testAllStudents() {
		List<Student> student = Arrays.asList(new Student());
		when(studentRepository.findAll())
			.thenReturn(student);
		schoolController.allStudents();
		verify(studentView).showAllStudents(student);
	}
	
	@Test
	public void testNewStudentWhenStudentDoesNotAlreadyExist() {
		Student student = new Student("1", "test");
		when(studentRepository.findById("1"))
			.thenReturn(null);
		schoolController.newStudent(student);
		InOrder inOrder = inOrder(studentRepository, studentView);
		inOrder.verify(studentRepository).save(student);
		inOrder.verify(studentView).studentAdded(student);
	}
	
	@Test
	public void testNewStudentWhenStudentAlreadyExists() {
		Student studentToAdd = new Student("1", "test");
		Student existingStudent = new Student("1", "existingStudent");
		when(studentRepository.findById("1"))
			.thenReturn(existingStudent);
		schoolController.newStudent(studentToAdd);
		verify(studentView).showError("Already existing student with id 1", existingStudent);
		verifyNoMoreInteractions(ignoreStubs(studentRepository));
	}
	
	@Test
	public void testDeleteStudentWhenStudentExists() {
		Student student = new Student("1", "test");
		when(studentRepository.findById("1"))
			.thenReturn(student);
		schoolController.deleteStudent(student);
		InOrder inOrder = inOrder(studentRepository, studentView);
		inOrder.verify(studentRepository).delete("1");
		inOrder.verify(studentView).studentRemoved(student);
	}
	
	@Test
	public void testDeleteStudentWhenStudentDoesNotExist() {
		Student student = new Student("1", "test");
		when(studentRepository.findById("1"))
			.thenReturn(null);
		schoolController.deleteStudent(student);
		verify(studentView).showError("No existing student with id 1", student);
		verifyNoMoreInteractions(ignoreStubs(studentRepository));
	}

}
