package gui;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Gui extends JFrame {
	private static final long serialVersionUID = -7886147914342084854L;

	private JTextField textSubmit;
	private JTextArea textDisplay;
	private JButton submitBtn;
	
	private final static String title = "Dining Cryptographers";
	public Gui() {
		super(title);
		
		setLayout(new BorderLayout());
	
		createTextDisplayBox();
		createTextEntryBox();
		
		setVisible(true);
	}

	private void createTextDisplayBox() {
		 textDisplay = new JTextArea(20, 50);
		 textDisplay.setEditable(false);
		 textDisplay.setVisible(true);
		 getContentPane().add(textDisplay, BorderLayout.CENTER);
	}
	
	private void createTextEntryBox() {
		JPanel entryPnl = new JPanel(new BorderLayout());
		getContentPane().add(entryPnl, BorderLayout.PAGE_END);
		
		textSubmit = new JTextField(25);
		entryPnl.add(textSubmit, BorderLayout.CENTER);
		textSubmit.setVisible(true);
		
		SubmitMessageAction submitAct = new SubmitMessageAction();
		submitBtn = new JButton(submitAct);
		entryPnl.add(submitBtn, BorderLayout.LINE_END);
		submitBtn.setVisible(true);
	}
}
