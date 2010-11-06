package gui;

import interfaces.Input;
import interfaces.Output;


public class GuiManager implements Output {
	private Gui gui;
	private SubmitMessageAction submitAct;
	
	public GuiManager() {
		submitAct = new SubmitMessageAction();
		gui = new Gui(submitAct);
	}

	public void setInput(Input input) {
		submitAct.setInput(input);
	}
	
	@Override
	public void outputString(String str) {
		gui.outputString(str);
	}
}
