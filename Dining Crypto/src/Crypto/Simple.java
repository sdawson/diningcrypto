package crypto;

import interfaces.Input;
import interfaces.Output;

import communication.Message;

public class Simple implements Input, Output {
	private Output output;
	
	public Simple() {
		
	}
	
	public void setOutput(Output output) {
		this.output = output;
	}
	
	@Override
	public void inputString(String str) {
		Message msg = new Message(str);
		
		System.out.println("Received message: " + str);
		outputString("Got this message: " + str);
	}

	@Override
	public void outputString(String str) {
		output.outputString(str);
	}

}
