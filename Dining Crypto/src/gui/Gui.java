package gui;

import interfaces.Output;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Gui extends JFrame implements Output {
	private static final long serialVersionUID = -7886147914342084854L;

	private JTextField textSubmit;
	private JTextArea textDisplay;
	private JButton submitBtn;
	private SubmitMessageAction submitAct;
	
	private final static String title = "Dining Cryptographers";
	public Gui(SubmitMessageAction submitAct) {
		super(title);
		
		this.submitAct = submitAct;
		
		setLayout(new BorderLayout());
	
		createTextDisplayBox();
		createTextEntryBox();
		
		submitAct.setInputBox(textSubmit);
		
		pack();
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
		submitBtn = new JButton("Submit");
		
		
		entryPnl.add(textSubmit, BorderLayout.CENTER);
		textSubmit.setAction(submitAct);
		textSubmit.setEnabled(true);
		
		submitBtn.setAction(submitAct);
		submitBtn.setText("Submit");
		submitBtn.setEnabled(true);
		entryPnl.add(submitBtn, BorderLayout.LINE_END);
		
		textSubmit.setVisible(true);
		submitBtn.setVisible(true);
	}
	
	public void displayText(String str) {
		textDisplay.append("\n" + str);
	}

	@Override
	public void outputString(String str) {
		displayText(str);
	}
}
