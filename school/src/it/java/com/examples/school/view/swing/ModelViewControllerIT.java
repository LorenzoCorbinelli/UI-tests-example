package com.examples.school.view.swing;



import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.MongoDBContainer;

import com.examples.school.controller.SchoolController;
import com.examples.school.model.Student;
import com.examples.school.repository.mongo.StudentMongoRepository;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

public class ModelViewControllerIT extends AssertJSwingJUnitTestCase {
	
	@ClassRule
	public static final MongoDBContainer mongo = new MongoDBContainer("mongo:4.4.3");
	private MongoClient client;
	private StudentMongoRepository studentRepository;
	private StudentSwingView studentSwingView;
	private SchoolController schoolController;
	private FrameFixture window;

	@Override
	protected void onSetUp() throws Exception {
		client = new MongoClient(
				new ServerAddress(
						mongo.getHost(),
						mongo.getFirstMappedPort()));
		studentRepository = new StudentMongoRepository(client);
		for (Student student : studentRepository.findAll()) {
			studentRepository.delete(student.getId());
		}
		GuiActionRunner.execute(() -> {
			studentSwingView = new StudentSwingView();
			schoolController = new SchoolController(studentSwingView, studentRepository);
			studentSwingView.setSchoolController(schoolController);
			return studentSwingView;
		});
		window = new FrameFixture(robot(), studentSwingView);
		window.show();
	}
	
	@Override
	protected void onTearDown() {
		client.close();
	}

	@Test
	public void testAddStudent() {
		window.textBox("idTextBox").enterText("1");
		window.textBox("nameTextBox").enterText("test");
		window.button(JButtonMatcher.withText("Add")).click();
		assertThat(studentRepository.findById("1")).isEqualTo(new Student("1", "test"));
	}
	
	@Test
	public void testDeleteStudent() {
		studentRepository.save(new Student("10", "test"));
		GuiActionRunner.execute(() -> 
			schoolController.allStudents()
		);
		window.list("studentList").selectItem(0);
		window.button(JButtonMatcher.withText("Delete Selected")).click();
		assertThat(studentRepository.findById("10")).isNull();
	}
	
}
