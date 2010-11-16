import crypto.Simple;
import gui.GuiManager;


public class DiningCrypto {
	public static void main(String[] args) {
		// Start the crypto layer
		Simple simple = new Simple();
		
		// Start the GUI
		GuiManager guiManager = new GuiManager();
		guiManager.setInput(simple);
		simple.setOutput(guiManager);
		
		//
	}
}
