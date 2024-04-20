package com.examples.school.view.swing;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.examples.school.model.Student;
import com.examples.school.view.StudentView;

import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import javax.swing.JTextField;
import java.awt.Insets;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JScrollPane;
import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.ListSelectionModel;

public class StudentSwingView extends JFrame implements StudentView {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField textId;
	private JLabel lblName;
	private JTextField textName;
	private JButton btnAdd;
	private JScrollPane scrollPane;
	private JList<Student> listStudents;
	private JButton btnDeleteSelected;
	private JLabel errorLabel;
	
	private DefaultListModel<Student> listStudentsModel;

	DefaultListModel<Student> getListStudentsModel() {
		return listStudentsModel;
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					StudentSwingView frame = new StudentSwingView();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public StudentSwingView() {
		setTitle("Student View");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{0, 137, 0};
		gbl_contentPane.rowHeights = new int[]{0, 0, 0, 0, 0, 13, 0};
		gbl_contentPane.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{0.0, 0.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		JLabel lblNewLabel = new JLabel("id");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		contentPane.add(lblNewLabel, gbc_lblNewLabel);
		
		textId = new JTextField();
		KeyAdapter btnAddEnabler = new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				btnAdd.setEnabled(
						!textId.getText().trim().isEmpty() &&
						!textName.getText().trim().isEmpty());
			}
		};
		textId.addKeyListener(btnAddEnabler);
		textId.setName("idTextBox");
		GridBagConstraints gbc_textId = new GridBagConstraints();
		gbc_textId.insets = new Insets(0, 0, 5, 0);
		gbc_textId.fill = GridBagConstraints.HORIZONTAL;
		gbc_textId.gridx = 1;
		gbc_textId.gridy = 0;
		contentPane.add(textId, gbc_textId);
		textId.setColumns(10);
		
		lblName = new JLabel("name");
		GridBagConstraints gbc_lblName = new GridBagConstraints();
		gbc_lblName.anchor = GridBagConstraints.EAST;
		gbc_lblName.insets = new Insets(0, 0, 5, 5);
		gbc_lblName.gridx = 0;
		gbc_lblName.gridy = 1;
		contentPane.add(lblName, gbc_lblName);
		
		textName = new JTextField();
		textName.addKeyListener(btnAddEnabler);
		textName.setName("nameTextBox");
		GridBagConstraints gbc_textName = new GridBagConstraints();
		gbc_textName.insets = new Insets(0, 0, 5, 0);
		gbc_textName.fill = GridBagConstraints.HORIZONTAL;
		gbc_textName.gridx = 1;
		gbc_textName.gridy = 1;
		contentPane.add(textName, gbc_textName);
		textName.setColumns(10);
		
		btnAdd = new JButton("Add");
		btnAdd.setEnabled(false);
		GridBagConstraints gbc_btnAdd = new GridBagConstraints();
		gbc_btnAdd.gridwidth = 2;
		gbc_btnAdd.insets = new Insets(0, 0, 5, 0);
		gbc_btnAdd.gridx = 0;
		gbc_btnAdd.gridy = 2;
		contentPane.add(btnAdd, gbc_btnAdd);
		
		scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.gridwidth = 2;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 3;
		contentPane.add(scrollPane, gbc_scrollPane);
		
		listStudentsModel = new DefaultListModel<>();
		listStudents = new JList<>(listStudentsModel);
		listStudents.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listStudents.addListSelectionListener(
				arg0 -> btnDeleteSelected.setEnabled(listStudents.getSelectedIndex() != -1));
		listStudents.setName("studentList");
		scrollPane.setViewportView(listStudents);
		
		btnDeleteSelected = new JButton("Delete Selected");
		btnDeleteSelected.setEnabled(false);
		GridBagConstraints gbc_btnDeleteSelected = new GridBagConstraints();
		gbc_btnDeleteSelected.insets = new Insets(0, 0, 5, 0);
		gbc_btnDeleteSelected.gridwidth = 2;
		gbc_btnDeleteSelected.gridx = 0;
		gbc_btnDeleteSelected.gridy = 4;
		contentPane.add(btnDeleteSelected, gbc_btnDeleteSelected);
		
		errorLabel = new JLabel(" ");
		errorLabel.setName("errorMessageLabel");
		errorLabel.setForeground(Color.RED);
		GridBagConstraints gbc_errorLabel = new GridBagConstraints();
		gbc_errorLabel.gridx = 1;
		gbc_errorLabel.gridy = 5;
		contentPane.add(errorLabel, gbc_errorLabel);
		
	}

	@Override
	public void showAllStudents(List<Student> students) {
		students.stream().forEach(listStudentsModel::addElement);
	}

	@Override
	public void showError(String message, Student student) {
		errorLabel.setText(message + ": " + student);
	}

	@Override
	public void studentAdded(Student student) {
		listStudentsModel.addElement(student);
		errorLabel.setText(" ");
	}

	@Override
	public void studentRemoved(Student student) {
		// TODO Auto-generated method stub
		
	}

}
