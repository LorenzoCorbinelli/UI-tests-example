package com.examples.school.view.swing;


import javax.swing.JFrame;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.swing.launcher.ApplicationLauncher.*;

import java.util.regex.Pattern;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.bson.Document;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.testcontainers.containers.MongoDBContainer;

import com.mongodb.MongoClient;

@RunWith(GUITestRunner.class)
public class SchoolSwingAppE2E extends AssertJSwingJUnitTestCase {

	private static final String STUDENT_FIXTURE_2_NAME = "second student";

	private static final String STUDENT_FIXTURE_2_ID = "2";

	private static final String STUDENT_FIXTURE_1_NAME = "first student";

	private static final String STUDENT_FIXTURE_1_ID = "1";

	@ClassRule
	public static final MongoDBContainer mongo = new MongoDBContainer("mongo:4.4.3");
	
	private static final String DB_NAME = "test-db";
	private static final String COLLECTION_NAME = "test-collection";
	
	private MongoClient mongoClient;
	private FrameFixture window;
	
	@Override
	protected void onSetUp() throws Exception {
		String containerIpAddress = mongo.getHost();
		Integer mappedPort = mongo.getFirstMappedPort();
		mongoClient = new MongoClient(containerIpAddress, mappedPort);
		mongoClient.getDatabase(DB_NAME).drop();
		addTestStudentToTheDB(STUDENT_FIXTURE_1_ID, STUDENT_FIXTURE_1_NAME);
		addTestStudentToTheDB(STUDENT_FIXTURE_2_ID, STUDENT_FIXTURE_2_NAME);
		application("com.examples.school.app.swing.SchoolSwingApp")
			.withArgs(
				"--mongo-host=" + containerIpAddress,
				"--mongo-port=" + mappedPort.toString(),
				"--db-name=" + DB_NAME,
				"--db-collection=" + COLLECTION_NAME
			).start();;
		window = WindowFinder.findFrame(new GenericTypeMatcher<JFrame>(JFrame.class) {

			@Override
			protected boolean isMatching(JFrame frame) {
				return "Student View".equals(frame.getTitle()) && frame.isShowing();
			}
		}).using(robot());
	}
	
	@Override
	protected void onTearDown() {
		mongoClient.close();
	}
	
	private void addTestStudentToTheDB(String id, String name) {
		mongoClient.getDatabase(DB_NAME).getCollection(COLLECTION_NAME)
			.insertOne(new Document()
					.append("id", id)
					.append("name", name));
	}
	
	@Test @GUITest
	public void testOnStartAllDatabaseElementAreShown() {
		assertThat(window.list("studentList").contents())
			.anySatisfy(e -> assertThat(e).contains(STUDENT_FIXTURE_1_ID, STUDENT_FIXTURE_1_NAME))
			.anySatisfy(e -> assertThat(e).contains(STUDENT_FIXTURE_2_ID, STUDENT_FIXTURE_2_NAME));
	}
	
	@Test @GUITest
	public void testAddButtonSuccess() {
		window.textBox("idTextBox").enterText("10");
		window.textBox("nameTextBox").enterText("new student");
		window.button(JButtonMatcher.withText("Add")).click();
		assertThat(window.list("studentList").contents())
			.anySatisfy(e -> assertThat(e).contains("10", "new student"));
	}
	
	@Test @GUITest
	public void testAddButtonError() {
		window.textBox("idTextBox").enterText(STUDENT_FIXTURE_1_ID);
		window.textBox("nameTextBox").enterText("new student");
		window.button(JButtonMatcher.withText("Add")).click();
		assertThat(window.label("errorMessageLabel").text()).contains(STUDENT_FIXTURE_1_ID, STUDENT_FIXTURE_1_NAME);
	}
	
	@Test @GUITest
	public void testDeleteButtonSuccess() {
		window.list("studentList").selectItem(Pattern.compile(".*" + STUDENT_FIXTURE_1_NAME + ".*"));
		window.button(JButtonMatcher.withText("Delete Selected")).click();
		assertThat(window.list("studentList").contents())
			.noneMatch(e -> e.contains(STUDENT_FIXTURE_1_NAME));
	}

}
