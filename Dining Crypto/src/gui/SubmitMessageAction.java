package gui;

import interfaces.Input;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.JTextField;

public class SubmitMessageAction implements Action {
	static String NAME = "NAME";
	private String name = "Submit";
	
	private JTextField textEntryBox;
	
	private Input submitInput;

	public SubmitMessageAction() {
		
	}
	
	public void setInput(Input input) {
		submitInput = input;
	}
	public void setInputBox(JTextField textEntryBox) {
		this.textEntryBox = textEntryBox;
	}
	
	@Override
	public void addPropertyChangeListener(PropertyChangeListener arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object getValue(String key) {
		if (key.equals(NAME))
			return name;
		else
			return null;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void putValue(String arg0, Object arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setEnabled(boolean arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// Pass the string on to the next layer.
		submitInput.inputString(textEntryBox.getText());
		
		// Clear the submission box.
		textEntryBox.setText("");
	}

}
