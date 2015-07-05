import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/*
 * Author: Ganesh R Zilpe
 * This class is the editor for the command prompt
 */
public class CommandEditor extends JPanel
{
	private int editorNumber = -1;
	private JTextArea editorArea;
	
	public CommandEditor(int number)
	{
		editorNumber = number;
		setLayout(new GridLayout(1,1));
		editorArea = new JTextArea();
		editorArea.setText("");
		editorArea.setEditable(true);
		JScrollPane scrollCode = new JScrollPane(editorArea); 
		add(scrollCode); 
		
	}
	
	public String getEditorText()
	{
		return editorArea.getText();
	}
	
	public void setEditorText(String text)
	{
		editorArea.setText(text);
	}

}
