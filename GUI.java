

import javax.swing.border.TitledBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static java.awt.event.InputEvent.CTRL_DOWN_MASK;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.EventListenerList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/*
 * GUI of EZOpen
 * Author: Ganesh R Zilpe
 * email: zilpeganesh@gmail.com
 * 
 */
public class GUI extends JFrame implements ActionListener{

	private JTextArea console;
	private JMenuItem menuSave = new JMenuItem("Save Settings");    
	private JMenuItem menuClear = new JMenuItem("Clear All");  
	private JPanel    editorPanel = new JPanel(new GridLayout(1,1));
	private JButton addComponent, execute;
	private JPanel leftPanel, innerCenterPanel;
	private JCheckBox isCommandPrompt;
	public static int countOfShortcuts = 0;
	private static boolean status = false;
	static private Hashtable  repository =new Hashtable(); 
	protected static EventListenerList listenerList = new EventListenerList();
	private static IndividualComponent ic = null;
	private boolean isSaveRequired = false;

	public GUI(String title) throws IOException {
		super(title);   
		Dimension dim = getToolkit().getScreenSize();
		setSize(3 * dim.width  / 4, 3 * dim.height / 4);
		setLocation((dim.width - getSize().width)/2, (dim.height - getSize().height)/2);    
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
		createMenu();

		createGUI();
		createIndividualComponentsFromXMLObjects(createXMLObjectsFromXMLFile("EZOpen/Settings.xml"));
	}

	private void createGUI() 
	{
		TitledBorder panelTitle;    
		setLayout(new BorderLayout());
		JPanel mainPanel = new JPanel(new BorderLayout());
		leftPanel = new JPanel(new FlowLayout());

		final JPanel centerPanel = new JPanel(new GridLayout(0,2));
		innerCenterPanel = new JPanel(new BorderLayout());

		JPanel downPanel = new JPanel(new GridLayout(1,1));
		JPanel tokenPanel = new JPanel(new GridLayout(1,1));
		JPanel semanticPanel = new JPanel(new GridLayout(1,1)); 

		JPanel consolePanel = new JPanel(new GridLayout(1,1));
		panelTitle = BorderFactory.createTitledBorder("Shortcuts Dashboard");
		leftPanel.setBorder(panelTitle); 
		
		innerCenterPanel.add(leftPanel, BorderLayout.CENTER);

	
		//Initial component
		JPanel controlPanel  = new JPanel();
		isCommandPrompt = new JCheckBox("Command Prompt");
		//isCommandPrompt.setLocation(10, 40);
		addComponent = new JButton("Add shortcut");
		addComponent.addActionListener(this); 
		execute = new JButton("Execute");
		execute.addActionListener(this);
		controlPanel.add(isCommandPrompt);
		controlPanel.add(addComponent);
		controlPanel.add(execute);
		mainPanel.add(controlPanel, BorderLayout.NORTH);

		// console
		panelTitle = BorderFactory.createTitledBorder("Console");
		consolePanel.setBorder(panelTitle);  
		console = new JTextArea();
		console.setEditable(false);
		console.setBackground(Color.black);
		console.setForeground(Color.white);    
		JScrollPane scrollConsole = new JScrollPane(console); 
		consolePanel.add(scrollConsole);


		JTextArea sampleTextArea = new JTextArea(); 
		Color c = new Color(0,0,0,100);
		sampleTextArea.setBackground(c);
		sampleTextArea.setEditable(false);

		JScrollPane scrollCode = new JScrollPane(sampleTextArea); 
		editorPanel.add(scrollCode);
		editorPanel.setEnabled(false);


		centerPanel.add(innerCenterPanel);
		centerPanel.add(editorPanel);

		// main frame
		mainPanel.add(centerPanel, BorderLayout.CENTER);


		downPanel.add(consolePanel);
		downPanel.setPreferredSize(new Dimension(getWidth(), getHeight()/4 ));
		add(mainPanel, BorderLayout.CENTER);
		add(downPanel, BorderLayout.SOUTH);
		// editor hotkey
		menuClear.setAccelerator(KeyStroke.getKeyStroke('C', CTRL_DOWN_MASK));
		setVisible(true);
	}

	private void createMenu() {    
		JMenuBar menuBar = new JMenuBar();
		JMenu menuFile = new JMenu("File");
		JMenu menuFile1 = new JMenu("Help");
		//JMenu menuRun = new JMenu("Run");  
		menuSave.addActionListener(this);
		menuClear.addActionListener(this);
		menuFile.add(menuSave);
		menuFile.add(menuClear);
		menuBar.add(menuFile);   
		menuBar.add(menuFile1);   
		//menuBar.add(menuRun);  
		setJMenuBar(menuBar);
	}

	@Override
	public void actionPerformed(ActionEvent event) 
	{
		if(event.getSource()== addComponent)
		{
			countOfShortcuts++;
			JPanel comp = new IndividualComponent(countOfShortcuts, isCommandPrompt.isSelected(),editorPanel, repository);

			System.out.println("size"+GUI.this.getBounds().width);
			comp.setPreferredSize(new Dimension((((GUI.this.getBounds()).width)/2 -40), 40 ));
			leftPanel.add(comp);
			if(ic == null)
				ic =(IndividualComponent) comp;

			if(countOfShortcuts>5 && !status)
			{
				status = true;
				Component [] components = leftPanel.getComponents();
				leftPanel.removeAll();

				BoxLayout boxLayout = new BoxLayout(leftPanel, BoxLayout.Y_AXIS); // top to bottom
				leftPanel.setLayout(boxLayout);
				JScrollPane scrollPane = new JScrollPane(leftPanel);
				scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
				scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
				scrollPane.setName("scrollbar");

				innerCenterPanel.add(scrollPane);
				for(int i=0; i<components.length; i++)
				{
					leftPanel.add(components[i]);
				}
			}
			innerCenterPanel.revalidate();
			innerCenterPanel.repaint();

		}
		else if (menuSave.equals(event.getSource())) 
		{
			if(ic == null && !isSaveRequired)
			{
				//if there is no shortcut component present in the panel, then there is no need to save settings
				JOptionPane.showMessageDialog(this, "No settings present to save.", "Information",
						JOptionPane.INFORMATION_MESSAGE);
				return;

			}
			isSaveRequired = false;
			File tmp = null;
			//repository.clear();
			repository = IndividualComponent.getRepository();
			try 
			{
				File dir = new File("EZOpen");
				dir.mkdirs();
				tmp = new File(dir, "Settings.xml");
				tmp.createNewFile();
				System.out.println("file created"+tmp.getPath());
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
			XMLObject [] xmlObjects = createXMLObjectsFromRepository(); 
			saveSettingsToXML(tmp, xmlObjects);
			JOptionPane.showMessageDialog(this, "Settings are saved to Settings.xml \nPath: "+tmp.getAbsolutePath(), "Information",
					JOptionPane.INFORMATION_MESSAGE);

		} 
		//clear all individual components added to innerCenterPanel
		else if (menuClear.equals(event.getSource())) 
		{
			if(ic == null)
			{
				//if there is no shortcut component present in the panel, then there is no need to clear settings
				JOptionPane.showMessageDialog(this, "No settings present to clear.", "Information",
						JOptionPane.INFORMATION_MESSAGE);
				return;

			}
			
			JTextArea sampleTextArea = new JTextArea(); 
			Color c = new Color(0,0,0,100);
			sampleTextArea.setBackground(c);
			sampleTextArea.setEditable(false);

			JScrollPane scrollCode = new JScrollPane(sampleTextArea); 
			editorPanel.removeAll();
			editorPanel.add(scrollCode);
			
			
			isSaveRequired = true;
			innerCenterPanel.removeAll();
			leftPanel.removeAll();
			leftPanel.setLayout(new FlowLayout());
			innerCenterPanel.add(leftPanel);
			IndividualComponent.clearRepository();
			repository.clear();
			countOfShortcuts= -1;
			status = false;
			ic = null;
			console.setText("");
			innerCenterPanel.revalidate();
			innerCenterPanel.repaint();
			

		}    
		else if(event.getSource()== execute)
		{
			execute();
		}

	}


	public static void main(String[] args) throws FileNotFoundException, IOException 
	{		 
		try 
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (ClassNotFoundException e) {
		}
		catch (InstantiationException e) {
		}
		catch (IllegalAccessException e) {
		}
		catch (UnsupportedLookAndFeelException e) {
		}

		GUI gui = new GUI("EZOpen 1.0");      
	}
	public static void decrementCountOfShortcuts()
	{
		countOfShortcuts--;
	}
	public static int getCountOfShortcuts()
	{
		return countOfShortcuts;
	}
	public static void setStatus(boolean status1)
	{
		status = status1;
	}

	public static void resetIC()
	{
		ic = null;
	}

	public void saveSettingsToXML(File xmlDoc, XMLObject [] xmlObjects)
	{
		try {

			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("HashtableObjects");
			doc.appendChild(rootElement);

			if(xmlObjects != null && xmlObjects.length !=0 && xmlObjects[0] != null)
				for(int i=0; i< xmlObjects.length; i++)
				{
					// HashtableObject elements
					Element HashtableObject = doc.createElement("HashtableObject");
					rootElement.appendChild(HashtableObject);

					// objectNo elements
					Element objectNo = doc.createElement("objectNo");
					objectNo.appendChild(doc.createTextNode(""+xmlObjects[i].getObjectNo()));
					HashtableObject.appendChild(objectNo);

					// commandPrompt elements
					Element commandPrompt = doc.createElement("commandPrompt");
					commandPrompt.appendChild(doc.createTextNode(""+xmlObjects[i].isCommandPrompt()));
					HashtableObject.appendChild(commandPrompt);

					// urlText elements
					Element urlText = doc.createElement("urlText");
					urlText.appendChild(doc.createTextNode(xmlObjects[i].getUrlText()));
					HashtableObject.appendChild(urlText);

					// editorText elements
					Element editorText = doc.createElement("editorText");
					editorText.appendChild(doc.createTextNode(xmlObjects[i].getEditorText()));
					HashtableObject.appendChild(editorText);
				}

			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(xmlDoc);

			// Output to console for testing
			// StreamResult result = new StreamResult(System.out);

			transformer.transform(source, result);

			System.out.println("File saved!");

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}
	}

	public void loadSettings()
	{
		JPanel comp = new IndividualComponent(countOfShortcuts, isCommandPrompt.isSelected(),editorPanel, repository);
		countOfShortcuts++;
		System.out.println("size"+GUI.this.getBounds().width);
		comp.setPreferredSize(new Dimension((((GUI.this.getBounds()).width)/2 -40), 40 ));
		leftPanel.add(comp);
		if(ic == null)
			ic =(IndividualComponent) comp;

		if(countOfShortcuts>5 && !status)
		{
			status = true;
			Component [] components = leftPanel.getComponents();
			leftPanel.removeAll();

			BoxLayout boxLayout = new BoxLayout(leftPanel, BoxLayout.Y_AXIS); // top to bottom
			leftPanel.setLayout(boxLayout);
			JScrollPane scrollPane = new JScrollPane(leftPanel);
			scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			scrollPane.setName("scrollbar");

			innerCenterPanel.add(scrollPane);
			for(int i=0; i<components.length; i++)
			{
				leftPanel.add(components[i]);
			}
		}
		innerCenterPanel.revalidate();
		innerCenterPanel.repaint();
	}

	public XMLObject [] createXMLObjectsFromRepository()
	{
		XMLObject [] result = new XMLObject[repository.size()];
		for(int i=0; i<=countOfShortcuts; i++)
		{
			if(repository.containsKey(new Integer(i)))
			{
				HashtableObject ho = (HashtableObject) repository.get(new Integer(i));
				XMLObject temp = new XMLObject();
				temp.setObjectNo(i);
				temp.setCommandPrompt(ho.getIComponent().isCommandPrompt());
				temp.setUrlText(ho.getIComponent().getUrlText());
				temp.setEditorText(ho.getCEditor().getEditorText());	
				result[i] = temp;
			}
			else
				continue;
		}
		if(result.length == 0)
			return null;
		return result;
	}

	public XMLObject [] createXMLObjectsFromXMLFile(String file)
	{
		File f = new File(file);
		if(!f.exists())
		{
			JOptionPane.showMessageDialog(this, "No previous settings found", "Information",
					JOptionPane.INFORMATION_MESSAGE);
			return null;
		}
		XMLObject [] result = null;
		Document dom = null;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		try {

			//Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();

			//parse using builder to get DOM representation of the XML file
			dom = db.parse(file);
			if(dom == null)
			{
				return null;
			}


		}catch(ParserConfigurationException pce) {
			pce.printStackTrace();
		}catch(SAXException se) {
			se.printStackTrace();
		}catch(IOException ioe) {
			ioe.printStackTrace();
		}

		Element docEle = dom.getDocumentElement();

		//get a nodelist of elements HashtableObject
		NodeList nl = docEle.getElementsByTagName("HashtableObject");
		result = new XMLObject [nl.getLength()];
		if(nl != null && nl.getLength() > 0) 
		{
			for(int i = 0 ; i < nl.getLength();i++) 
			{
				//get the HashtableObject element
				Element el = (Element)nl.item(i);

				//get the XMLObject object
				int objectNo = getIntValue(el,"objectNo");
				String tempCommandPrompt = getTextValue(el,"commandPrompt");
				boolean commandPrompt = false;
				if(tempCommandPrompt.equals("true"))
					commandPrompt = true;
				String urlText = getTextValue(el,"urlText");
				String editorText = getTextValue(el,"editorText");

				//Create a new XMLObject with the value read from the xml nodes
				XMLObject temp = new XMLObject(objectNo, commandPrompt, urlText, editorText);

				//add it to array result
				result[i] = temp;
			}
		}

		return result;
	}
	private String getTextValue(Element ele, String tagName) {
		String textVal = null;
		NodeList nl = ele.getElementsByTagName(tagName);
		if(nl != null && nl.getLength() > 0) {
			Element el = (Element)nl.item(0);
			if(el.getFirstChild() == null)
				return "";
			textVal = el.getFirstChild().getNodeValue();
		}

		return textVal;
	}
	private int getIntValue(Element ele, String tagName) {
		//in production application you would catch the exception
		return Integer.parseInt(getTextValue(ele,tagName));
	}

	public void createIndividualComponentsFromXMLObjects(XMLObject [] xmlObjects)
	{
		countOfShortcuts = -1;
		repository.clear();
		if(xmlObjects == null || xmlObjects.length == 0)
			return;
		for(int j=0; j<xmlObjects.length; j++)
		{
			IndividualComponent comp = new IndividualComponent(xmlObjects[j].getObjectNo(), xmlObjects[j].isCommandPrompt(),editorPanel, repository);
			comp.setUrlText(xmlObjects[j].getUrlText());
			countOfShortcuts++;
			System.out.println("size"+GUI.this.getBounds().width);
			comp.setPreferredSize(new Dimension((((GUI.this.getBounds()).width)/2 -40), 40 ));
			leftPanel.add(comp);
			if(ic == null)
				ic =(IndividualComponent) comp;

			if(countOfShortcuts>5 && !status)
			{
				status = true;
				Component [] components = leftPanel.getComponents();
				leftPanel.removeAll();

				BoxLayout boxLayout = new BoxLayout(leftPanel, BoxLayout.Y_AXIS); // top to bottom
				leftPanel.setLayout(boxLayout);
				JScrollPane scrollPane = new JScrollPane(leftPanel);
				scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
				scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
				scrollPane.setName("scrollbar");

				innerCenterPanel.add(scrollPane);
				for(int i=0; i<components.length; i++)
				{
					leftPanel.add(components[i]);
				}
			}

			CommandEditor createdPanel = new CommandEditor(xmlObjects[j].getObjectNo());
			createdPanel.setEditorText(xmlObjects[j].getEditorText());
			HashtableObject ho = new HashtableObject(comp, (CommandEditor)createdPanel);
			repository.put(xmlObjects[j].getObjectNo(), ho);
		}
		innerCenterPanel.revalidate();
		innerCenterPanel.repaint();
	}
/*
 * Execute the shortcuts present in shortcuts dashboard 
 */
	public void execute()
	{
		console.setText("");
		String message = "";
		try
		{
			for(int i=0; i<=countOfShortcuts; i++)
			{
				if(repository.containsKey(new Integer(i)))
				{
					HashtableObject ho = (HashtableObject) repository.get(new Integer(i));
					IndividualComponent ic = ho.getIComponent();
					if(ic.isCommandPrompt())
					{
						message = "Command Prompt is opening .....";
						File dir = new File("EZOpen");
						dir.mkdirs();
						File batch_file = new File(dir, "run.bat");
						batch_file.createNewFile();
//						File batch_file = new File("EZOpen/run.bat");
//						batch_file.createNewFile();
						if(batch_file.exists())
						{
							FileWriter fw = new FileWriter(batch_file.getAbsoluteFile());
							BufferedWriter bw = new BufferedWriter(fw);
							bw.write(ho.getCEditor().getEditorText()+"\n cmd /k");
							bw.close();
							final URL urltoFile = batch_file.toURI().toURL();
							Thread processThread = new Thread() {

								public void run() {

									try {
										Process p = Runtime.getRuntime().exec("cmd /k start "+urltoFile.toString());
										Thread.sleep(1000);
									} catch (InterruptedException e) {
										e.printStackTrace();
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
							};
							processThread.start();
							processThread.join();
							console.setText(console.getText() + "\n"+message);
						}
						else
						{
							message = message +" No commands are found to execute in command prompt";
							console.setText(console.getText() + "\n"+message);
							continue;
						}


					}
					else
					{
						//if it is the shortcut of the process

						message = ho.getIComponent().getUrlText()+" is opening....";
						if(!(ho.getIComponent().getUrlText().equals("") || ho.getIComponent().getUrlText().isEmpty()))
						{
							try {
								Process p = null;
								//if the url is of exe then run following
								if(ho.getIComponent().getUrlText().contains(".exe"))
									p = new ProcessBuilder(ho.getIComponent().getUrlText()).start();
								else
								{
									//if the url is not exe and it may be doc or something else, then run following
									if (Desktop.isDesktopSupported()) {
										Desktop.getDesktop().open(new File(ho.getIComponent().getUrlText()));
									}
								}
								Thread.sleep(1000);
								console.setText(console.getText() + "\n"+message);
							} catch (IOException e) {
								message = message +"\n "+e.getMessage() +"\n";
								console.setText(console.getText() + "\n"+message);
							}
						}
						else
						{
							message = "shortcut uri is empty";
							console.setText(console.getText() + "\n"+message);
							continue;
						}

					}
				}
				else
					continue;
				
			}
			

		}
		catch(Exception ex)
		{
			System.out.println(ex.getMessage());
		}
	}

}
class SyncPipe implements Runnable
{
	public SyncPipe(InputStream istrm, OutputStream ostrm) {
		istrm_ = istrm;
		ostrm_ = ostrm;
	}
	public void run() {
		try
		{
			final byte[] buffer = new byte[1024];
			for (int length = 0; (length = istrm_.read(buffer)) != -1; )
			{
				ostrm_.write(buffer, 0, length);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		{
			//Runtime r = Runtime.getRuntime();
			///Process p2 = r.exec("a.exe"); //absolute or relative path
			/*ProcessBuilder builder = new ProcessBuilder(
		            "cmd.exe", "/c", "cd \"C:\\Program Files\" && dir");
		        builder.redirectErrorStream(true);
		        Process p = builder.start();
			 */
			//Process p = Runtime.getRuntime().exec("cmd.exe /c  ping 192.168.1.8");
			//File file = new File("EZOpen/run.bat");
			//file.createNewFile();
			//PrintWriter writer = new PrintWriter(file, "UTF-8");
			//writer.println("cd c:/");
			//file.delete();
			//Process p1 = Runtime.getRuntime().exec("H:\\Software\\Eclipse\\eclipse-ganymede\\eclipse\\eclipse.exe");
			/*
			String command = "cmd /c start cmd.exe";
		    Process child = Runtime.getRuntime().exec(command);

		    // Get output stream to write from it
		    OutputStream out = child.getOutputStream();

		    out.write("cd C:/ /r/n".getBytes());
		    out.flush();
		    out.write("dir /r/n".getBytes());
		    out.close();
			 */
			/*String[] command =
		    {
		        "cmd",
		    };
		    Process p = Runtime.getRuntime().exec(command);
		    new Thread(new SyncPipe(p.getErrorStream(), System.err)).start();
		    new Thread(new SyncPipe(p.getInputStream(), System.out)).start();
		    PrintWriter stdin = new PrintWriter(p.getOutputStream());
		    stdin.println("dir c:\\ /A /Q");
		    // write any other commands you want here
		    stdin.close();
		    int returnCode = p.waitFor();
		    System.out.println("Return code = " + returnCode);
			 */
			/*
			File f = new File("EZOpen");
			System.out.println(f.getAbsolutePath());
			Process p =  Runtime.getRuntime().exec("cmd /c run.bat", null, new File(f.getAbsolutePath()+"\\run"));
			 */
			// String[] command = {"cmd.exe", "/C", "Start", "H:\\Development\\Java Workspace_1\\openAll\\EZOpen\\run.bat"};
			//File f = new File("EZOpen");
			//   Process p =  Runtime.getRuntime().exec("start \"\" "+f.getAbsolutePath()+"\\run.bat");
			//   p.waitFor();
		}
	}
	private final OutputStream ostrm_;
	private final InputStream istrm_;
}

