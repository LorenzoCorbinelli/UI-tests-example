package com.examples.school.view.swing;


import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.core.matcher.JLabelMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JButtonFixture;
import org.assertj.swing.fixture.JTextComponentFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.examples.school.model.Student;

@RunWith(GUITestRunner.class)
public class StudentSwingViewTest extends AssertJSwingJUnitTestCase{

	private StudentSwingView studentSwingView;
	private FrameFixture window;

	@Override
	protected void onSetUp() throws Exception {
		GuiActionRunner.execute(() -> {
			studentSwingView = new StudentSwingView();
			return studentSwingView;
		});
		window = new FrameFixture(robot(), studentSwingView);
		window.show();
	}

	@Test @GUITest
	public void testControlsInitialState() {
		window.label(JLabelMatcher.withText("id"));
		window.textBox("idTextBox").requireEnabled();
		window.label(JLabelMatcher.withText("name"));
		window.textBox("nameTextBox").requireEnabled();
		window.button(JButtonMatcher.withText("Add")).requireDisabled();
		window.list("studentList");
		window.button(JButtonMatcher.withText("Delete Selected")).requireDisabled();
		window.label("errorMessageLabel").requireText(" ");
	}
	
	@Test
	public void testWhenIdAndNameAreNotEmptyTheAddButtonShouldBeEnabled() {
		window.textBox("idTextBox").enterText("1");
		window.textBox("nameTextBox").enterText("test");
		window.button(JButtonMatcher.withText("Add")).requireEnabled();
	}
	
	@Test
	public void testWhenEitherIdOrNameAreEmptyTheAddButtonShouldBeDisabled() {
		JTextComponentFixture id = window.textBox("idTextBox");
		JTextComponentFixture name = window.textBox("nameTextBox");
		
		id.enterText("1");
		name.enterText(" ");
		window.button(JButtonMatcher.withText("Add")).requireDisabled();
		
		id.setText("");
		name.setText("");
		
		id.enterText(" ");
		name.enterText("test");
		window.button(JButtonMatcher.withText("Add")).requireDisabled();
	}
	
	@Test
	public void testDeleteButtonShouldBeEnabledOnlyWhenAStudentIsSelected() {
		GuiActionRunner.execute(() -> 
				studentSwingView.getListStudentsModel().addElement(new Student("1", "test")));
		window.list("studentList").selectItem(0);
		JButtonFixture deleteButton = window.button(JButtonMatcher.withText("Delete Selected"));
		deleteButton.requireEnabled();
		window.list("studentList").clearSelection();
		deleteButton.requireDisabled();
	}
	
	@Test
	public void testShowAllStudentsShouldAddAllStudentsToTheList() {
		Student student1 = new Student("1", "student1");
		Student student2 = new Student("2", "student2");
		GuiActionRunner.execute(() -> 
				studentSwingView.showAllStudents(Arrays.asList(student1, student2))
		);
		String[] listContents = window.list("studentList").contents();
		assertThat(listContents).containsExactly(student1.toString(), student2.toString());
	}
	
	@Test
	public void testShowErrorShouldShowTheMessageInTheErrorLabel() {
		Student student = new Student("1", "test");
		GuiActionRunner.execute(() -> 
				studentSwingView.showError("error message", student)
		);
		window.label("errorMessageLabel").requireText("error message: " + student);
	}
	
}
