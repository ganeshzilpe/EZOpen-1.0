/*
 * Author: Ganesh R Zilpe
 * This class handles the saving of the existing shortcuts present in the Shortcut Dashboard.
 */
public class XMLObject {

	private int objectNo =-1;
	private boolean commandPrompt = false;
	private String urlText = "";
	private String editorText = "";
	
	public XMLObject() {}
	
	public XMLObject(int no, boolean commandPresent, String url, String text)
	{
		objectNo = no;
		commandPrompt = commandPresent;
		urlText = url;
		editorText = text;
	}

	public void setObjectNo(int objectNo) {
		this.objectNo = objectNo;
	}

	public void setCommandPrompt(boolean commandPrompt) {
		this.commandPrompt = commandPrompt;
	}

	public void setUrlText(String urlText) {
		this.urlText = urlText;
	}

	public void setEditorText(String editorText) {
		this.editorText = editorText;
	}

	public int getObjectNo() {
		return objectNo;
	}

	public boolean isCommandPrompt() {
		return commandPrompt;
	}

	public String getUrlText() {
		return urlText;
	}

	public String getEditorText() {
		return editorText;
	}
	
	
}
