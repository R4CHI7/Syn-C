import javax.swing.* ;
import java.awt.* ;
import java.awt.event.*;
import java.io.* ;

public class MainUI extends JFrame implements ActionListener {
	String dir = null, file ;
	private JScrollPane scrollPane1;
	private JTextArea codeArea;
	private JButton browseBtn;
	private JButton convertBtn;
	private JButton compileBtn;
	private JTabbedPane TP;
	private JPanel consolePanel;
	private JTextArea consoleArea;
	private JScrollPane scrollPane2, scrollPane3;
	MainUI() {

		scrollPane1 = new JScrollPane();
		codeArea = new JTextArea();
		browseBtn = new JButton();
		convertBtn = new JButton();
		compileBtn = new JButton();
		TP = new JTabbedPane();
		consolePanel = new JPanel();
		scrollPane3 = new JScrollPane();
		consoleArea = new JTextArea();

		//======== this ========
		setResizable(false);
		setTitle("Syn-C");
		Container contentPane = getContentPane();
		contentPane.setLayout(null);

		//======== scrollPane1 ========
		{
			scrollPane1.setViewportView(codeArea);
		}
		contentPane.add(scrollPane1);
		scrollPane1.setBounds(5, 85, 875, 375);

		//---- browseBtn ----
		browseBtn.setText("Browse");
		contentPane.add(browseBtn);
		browseBtn.setBounds(190, 20, 100, 45);
		browseBtn.addActionListener(this) ;

		//---- convertBtn ----
		convertBtn.setText("Convert");
		contentPane.add(convertBtn);
		convertBtn.setBounds(385, 20, 110, 45);
		convertBtn.addActionListener(this) ;

		//---- compileBtn ----
		compileBtn.setText("Compile");
		contentPane.add(compileBtn);
		compileBtn.setBounds(580, 20, 165, 45);
		compileBtn.addActionListener(this) ;

		//======== TP ========
		{

			//======== consolePanel ========
			{
				consolePanel.setMinimumSize(new Dimension(900, 85));
				consolePanel.setPreferredSize(new Dimension(900, 85));

				consolePanel.setLayout(null);

				//======== scrollPane3 ========
				{
					scrollPane3.setViewportView(consoleArea);
				}
				consolePanel.add(scrollPane3);
				scrollPane3.setBounds(0, 0, 850, 80);

				{ // compute preferred size
					Dimension preferredSize = new Dimension();
					for(int i = 0; i < consolePanel.getComponentCount(); i++) {
						Rectangle bounds = consolePanel.getComponent(i).getBounds();
						preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
						preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
					}
					Insets insets = consolePanel.getInsets();
					preferredSize.width += insets.right;
					preferredSize.height += insets.bottom;
					consolePanel.setMinimumSize(preferredSize);
					consolePanel.setPreferredSize(preferredSize);
				}
			}
			TP.addTab("Console", consolePanel);

		}
		contentPane.add(TP);
		TP.setBounds(15, 475, 795, 130);

		contentPane.setPreferredSize(new Dimension(905, 640));
		pack();
		setLocationRelativeTo(getOwner());

		setVisible(true) ;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE) ;


   		File delFile = new File("temp.c") ;
   		delFile.delete() ;
   		delFile = new File("temp") ;
   		delFile.delete() ;

	}

	public void actionPerformed(ActionEvent ae) {
		if(ae.getSource() == browseBtn) {
			FileDialog inp = new FileDialog(this, "Input File...") ;
			inp.setVisible(true) ;
			file = inp.getFile();
			dir = inp.getDirectory();
			try {
				FileReader reader = new FileReader(dir+file) ; 
				BufferedReader br = new BufferedReader(reader) ;
				codeArea.read(br, null) ;
				br.close() ;
			}
			catch(Exception e) {
				System.out.println(e) ;
			}
		}

		else if(ae.getSource() == convertBtn) {
			if(dir != null) {
				String param  ;
				param = "./flex_output " + dir + " " + file ;
				try{
				Process p = Runtime.getRuntime().exec(param) ;
				}
				catch (IOException e) {System.out.println(" procccess not read"+e);}
				try {
					FileReader reader = new FileReader("output.c") ; 
					BufferedReader br = new BufferedReader(reader) ;
					codeArea.read(br, null) ;
					br.close() ;
			}
			catch(Exception e) {
				System.out.println(e) ;
			}
			}
		}

		else {

			boolean run = true ;
			consoleArea.setText("") ;

			// COMPILE

			try {
			    BufferedWriter fileOut = new BufferedWriter(new FileWriter("temp.c")); 
			    String myString1 =codeArea.getText();
			    String myString2 = myString1.replace("\r", "\n");

			    fileOut.write(myString2);
			    fileOut.close();
			} 
			catch (IOException ioe) {
			    ioe.printStackTrace();
			}

			try {
			    String line;
			    Process p = Runtime.getRuntime().exec("gcc -o temp temp.c");
			    BufferedReader bri = new BufferedReader(new InputStreamReader(p.getInputStream()));
			    BufferedReader bre = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			    while ((line = bri.readLine()) != null) {
			      	consoleArea.append(line) ;
			    }
			    bri.close();
			    while ((line = bre.readLine()) != null) {
			      	consoleArea.append(line) ;
			      	consoleArea.append("\n") ;
			    }
			    bre.close();
			    p.waitFor();
			    if(consoleArea.getLineCount() != 0)
			    	run = false ;
			}
			catch (Exception err) {
			    consoleArea.append(err.toString()) ;
   			}

   			// RUN

   			if(consoleArea.getText().trim().length() == 0) {
   				consoleArea.setText("Compilation Successfull!") ;
   			} 

   			File delFile = new File("temp.c") ;
   			delFile.delete() ;
   			delFile = new File("temp") ;
   			delFile.delete() ;

		}

	}

	public static void main(String[] args) {
		new MainUI() ;
	}
}