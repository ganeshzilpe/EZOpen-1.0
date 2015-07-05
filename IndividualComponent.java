import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

/*
 * Author: Ganesh R Zilpe
 * This class holds information about each shortcut
 * Also, this class is GUI component of the shortcut
 */
public class IndividualComponent extends JPanel implements ActionListener 
{
	private JLabel name;
	private int componentNo = -1;
	private boolean isCommandPrompt = false;
	private JTextField url;
	private JButton remove;
	private JButton showEditor;
	private TitledBorder title;
	//the Editor panel i.e. right hand side panel
	private JPanel showEditorTriggeredPanel;
	static private Hashtable  repository;
	public IndividualComponent (int number, boolean isCommand, JPanel editorPanel, Hashtable rep)
	{

		setLayout(new BorderLayout());
		repository = rep;
		isCommandPrompt = isCommand;
		componentNo = number;
		//title = BorderFactory.createTitledBorder("ShortCut"+componentNo);
		//this.setBorder(title);
		this.setBorder(BorderFactory.createEmptyBorder(0,10,10,10)); 
		showEditorTriggeredPanel = editorPanel;
		//title.setVisible(false);

		name = new JLabel(""+componentNo);
		name.setVisible(true);
		//add(name, BorderLayout.WEST);

		url = new JTextField();
		url.setVisible(true);
		//add(url, BorderLayout.CENTER);

		remove = new JButton("Remove");
		showEditor = new JButton("Editor");
		showEditor.setName("Button"+componentNo);
		if(!isCommandPrompt)
			showEditor.setVisible(false);
		else
		{
			add(showEditor, BorderLayout.WEST);
			url.setText("Command Prompt");
			url.setEnabled(false);
		}
		add(url, BorderLayout.CENTER);
		add(remove, BorderLayout.EAST);
		showEditor.addActionListener(this);	
		remove.addActionListener(this);	
		Object createdPanel = new CommandEditor(componentNo);
		HashtableObject ho = new HashtableObject(this, (CommandEditor)createdPanel);
		repository.put(componentNo, ho);
	}



	public void actionPerformed(ActionEvent event) 
	{
		Object createdPanel;
		//while(true)
		//{

		//if(((JButton)event.getSource()).getName().equals(showEditor.getName()))
		if((JButton)event.getSource() == showEditor)
		{
			if(repository.containsKey(componentNo))
			{
				
				createdPanel = ((HashtableObject)repository.get(componentNo)).getCEditor();
			}
			else
			{
				createdPanel = new CommandEditor(componentNo);
				HashtableObject ho = new HashtableObject(this, (CommandEditor)createdPanel);
				repository.put(componentNo, ho);
			}
			showEditorTriggeredPanel.removeAll();
			showEditorTriggeredPanel.setEnabled(true);
			showEditorTriggeredPanel.add((JPanel)createdPanel);
			showEditorTriggeredPanel.revalidate();
			showEditorTriggeredPanel.repaint();
			//break;
		}
		else if((JButton)event.getSource()== remove)
		{
			JPanel parent = (JPanel)IndividualComponent.this.getParent();
			parent.remove(this);
			GUI.decrementCountOfShortcuts();
			repository.remove(componentNo);
			if(GUI.getCountOfShortcuts()<6)
			{
				if(GUI.getCountOfShortcuts() ==-1)
					GUI.resetIC();
				
				GUI.setStatus(false);
				parent.setLayout(new FlowLayout());
				Object mainParent = parent.getParent();
				while(!mainParent.getClass().toString().contains("javax.swing.JPanel"))
				{
					mainParent = ((Component) mainParent).getParent();
					System.out.println(mainParent.getClass().toString());
				}
				/*Component [] component = ((Container) mainParent).getComponents();
				int i =0;
				while(true)
				{
					if(component[i].getName().equals("scrollbar"))
					{
						((JPanel) mainParent).remove(component[i]);
						break;
					}
				}
				/*component = ((Container) mainParent).getComponents();
				(Container) mainParent
				for(i=0; i<component.length; i++)
				{
					((JPanel) mainParent).add(component[i]);

				}*/
				((JPanel) mainParent).removeAll();
				((JPanel)mainParent).add(parent);
				((Component) mainParent).revalidate();
				((Component) mainParent).repaint();
			}
			else
			{
				parent.revalidate();
				parent.repaint();
			}

		}
		//}
	}



	public static Hashtable getRepository() {
		return repository;
	}



	public static void setRepository(Hashtable repository) {
		IndividualComponent.repository = repository;
	}
	
	public static void clearRepository() {
		repository.clear();
	}



	public int getComponentNo() {
		return componentNo;
	}



	public boolean isCommandPrompt() {
		return isCommandPrompt;
	}



	public String getUrlText() {
		return url.getText();
	}
	
	public void setUrlText(String text) {
		url.setText(text);
	}
	
	

}

class HashtableObject
{
	IndividualComponent iComponent = null;
	CommandEditor cEditor = null;
	
	public HashtableObject(IndividualComponent component, CommandEditor editor) 
	{
		iComponent = component;
		cEditor = editor;
	}

	public IndividualComponent getIComponent() {
		return iComponent;
	}

	public void setIComponent(IndividualComponent component) {
		iComponent = component;
	}

	public CommandEditor getCEditor() {
		return cEditor;
	}

	public void setCEditor(CommandEditor editor) {
		cEditor = editor;
	}
	
	
}

