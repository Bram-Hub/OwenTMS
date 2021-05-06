package TuringMachine;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class TuringMachineFrame_ShortcutBox extends JDialog implements ActionListener, KeyListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel panel1 = new JPanel();
	private JPanel panel2 = new JPanel();
	private JPanel insetsPanel1 = new JPanel();
	private JPanel insetsPanel2 = new JPanel();
	private JPanel insetsPanel3 = new JPanel();
	private JPanel insetsPanel4 = new JPanel();
	private JButton button1 = new JButton();
	private JLabel imageLabel = new JLabel();
	private JLabel label1 = new JLabel();
	private JLabel label2 = new JLabel();
	private JLabel label3 = new JLabel();
	private JLabel label4 = new JLabel();
	private BorderLayout borderLayout1 = new BorderLayout();
	private BorderLayout borderLayout2 = new BorderLayout();
	private FlowLayout flowLayout1 = new FlowLayout();
	private GridLayout gridLayout1 = new GridLayout();
	private GridLayout gridLayout2 = new GridLayout();
	
	//Initialize shortcut values
	private String copyKey = GraphPanel.copyKey + "";
	private String pasteKey = GraphPanel.pasteKey + "";
	private String saveKey = GraphPanel.saveKey + "";
	private String openKey = GraphPanel.openKey + "";	
	private JButton setCopy = new JButton("Set Copy");
	private JButton setPaste = new JButton("Set Paste");
	private JButton setSave = new JButton("Set Save");
	private JButton setOpen = new JButton("Set Open");
	private char changeChar = ' ';
	private boolean changingCopy = false, changingPaste = false, changingSave = false, changingOpen = false;
	
	public TuringMachineFrame_ShortcutBox( Frame parent ) {
		super( parent );
		enableEvents( AWTEvent.WINDOW_EVENT_MASK );
		try {
		  jbInit();
		}
		catch ( Exception e ) {
		  e.printStackTrace();
		}
	}
	
	// Component initialization
	private void jbInit() throws Exception {
		// imageLabel.setIcon(new
		// ImageIcon(TuringMachineFrame_AboutBox.class.getResource("[Your Image]")));
		this.setTitle( "Register Shortcuts" );
		panel1.setLayout( borderLayout1 );
		panel2.setLayout( borderLayout2 );
		insetsPanel1.setLayout( flowLayout1 );
		insetsPanel2.setLayout( flowLayout1 );
		insetsPanel2.setBorder( BorderFactory.createEmptyBorder( 100, 100, 100, 100 ) );
		gridLayout1.setRows( 4 );
		gridLayout1.setColumns( 1 );
		label1.setText( "Copy: Ctrl + " + copyKey );
		label2.setText( "Paste: Ctrl + " + pasteKey );
		label3.setText( "Save: Ctrl + " + saveKey );
		label4.setText( "Open: Ctrl + " + openKey );
		insetsPanel3.setLayout( gridLayout1 );
		insetsPanel3.setBorder( BorderFactory.createEmptyBorder( 10, 60, 10, 10 ) );
		button1.setText( "OK" );
		button1.addActionListener( this );
		insetsPanel2.add( imageLabel, null );
		panel2.add( insetsPanel2, BorderLayout.WEST );
		this.getContentPane().add( panel1, null );
		insetsPanel3.add( label1, null );
		insetsPanel3.add( label2, null );
		insetsPanel3.add( label3, null );
		insetsPanel3.add( label4, null );
		panel2.add( insetsPanel3, BorderLayout.CENTER );
		insetsPanel1.add( button1, null );
		panel1.add( insetsPanel1, BorderLayout.SOUTH );
		panel1.add( panel2, BorderLayout.NORTH );
		insetsPanel4.setLayout(gridLayout2);
		addKeyListener(this);
		
		
		//Add button functionality
		setCopy.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				System.out.println(requestFocusInWindow());
				setCopy.setText("Type key");
				setPaste.setText("Set Paste");
				setSave.setText("Set Save");
				setOpen.setText("Set Open");
				changingCopy = true;
				changingPaste = false;
				changingSave = false;
				changingOpen = false;
				TuringMachineFrame_ShortcutBox.writeConfig();
			}
		});
		setPaste.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				System.out.println(requestFocusInWindow());
				setCopy.setText("Set Copy");
				setPaste.setText("Type key");
				setSave.setText("Set Save");
				setOpen.setText("Set Open");
				changingCopy = false;
				changingPaste = true;
				changingSave = false;
				changingOpen = false;
                TuringMachineFrame_ShortcutBox.writeConfig();
			}
		});
		setSave.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				System.out.println(requestFocusInWindow());
				setCopy.setText("Set Copy");
				setPaste.setText("Set Paste");
				setSave.setText("Type key");
				setOpen.setText("Set Open");
				changingCopy = false;
				changingPaste = false;
				changingSave = true;
				changingOpen = false;
                TuringMachineFrame_ShortcutBox.writeConfig();
	      }
		});
		setOpen.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				System.out.println(requestFocusInWindow());
				setCopy.setText("Set Copy");
				setPaste.setText("Set Paste");
				setSave.setText("Set Save");
				setOpen.setText("Type key");
				changingCopy = false;
				changingPaste = false;
				changingSave = false;
				changingOpen = true;
                TuringMachineFrame_ShortcutBox.writeConfig();
			}
		});
		
		insetsPanel4.add(setCopy);
		insetsPanel4.add(setPaste);
		insetsPanel4.add(setSave);
		insetsPanel4.add(setOpen);
		panel2.add(insetsPanel4, BorderLayout.WEST);
		setResizable( true );
	}
	
	//Reads shortcuts from a config file
	public static void readConfig() {
	  try {
	    BufferedReader reader = new BufferedReader(new FileReader("config.txt"));
	    String line = null;
        
        while ((line = reader.readLine()) != null) {
          if(line.contains("copy: ")) {
            int i = line.indexOf(":");
            GraphPanel.copyKey = line.charAt(i+2);
            
          } else if(line.contains("paste: ")) {
            int i = line.indexOf(":");
            GraphPanel.pasteKey = line.charAt(i+2);
            
          } else if(line.contains("save: ")) {
            int i = line.indexOf(":");
            GraphPanel.saveKey = line.charAt(i+2);
            
          } else if(line.contains("open file: ")) {
            int i = line.indexOf(":");
            GraphPanel.openKey = line.charAt(i+2);
          } 
        }
        
        reader.close();
	  } catch(FileNotFoundException f) {
	    f.printStackTrace();
	  } catch(IOException e) {
	    e.printStackTrace();
	  } catch(IndexOutOfBoundsException i) {
	    System.err.println("Make sure config lines are of format function: [key]");
	  }  
	}
	
	public static void writeConfig() {
	  try {
        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append("copy: " + GraphPanel.copyKey + System.lineSeparator());
        strBuffer.append("paste: " + GraphPanel.pasteKey + System.lineSeparator());
        strBuffer.append("save: " + GraphPanel.saveKey + System.lineSeparator());
        strBuffer.append("open file: " + GraphPanel.openKey + System.lineSeparator());
        
        FileWriter writer = new FileWriter("config.txt");
        writer.append(strBuffer.toString());
        writer.close();
        
	  } catch(FileNotFoundException f) {
	    
	  } catch(IOException e) {
	    
	  }
	}
	
	// Overridden so we can exit when window is closed
	protected void processWindowEvent( WindowEvent e ) {
		if( e.getID() == WindowEvent.WINDOW_CLOSING ) {
		  cancel();
		}
		super.processWindowEvent( e );
		}
		
		// Close the dialog
		void cancel() {
		dispose();
		}
		
		// Close the dialog on a button event
		public void actionPerformed( ActionEvent e ) {
		if( e.getSource() == button1 ) {
		  cancel();
		}
	}

		@Override
		public void keyTyped(KeyEvent e) {
			
		}

		@Override
		public void keyPressed(KeyEvent e) {
			changeChar = e.getKeyChar();
			if (e.getKeyChar() >= 33 && e.getKeyChar() <= 126) { //Should cover most keys 
				if (changingCopy) {
					if (!(e.getKeyChar() == pasteKey.charAt(0) || e.getKeyChar() == saveKey.charAt(0)
							|| e.getKeyChar() == openKey.charAt(0))) {
						copyKey = changeChar + "";
						GraphPanel.copyKey = changeChar;
					}
					setCopy.setText("Set Copy");
					label1.setText( "Copy: Ctrl + " + copyKey );
					changingCopy = false;
				}
				if (changingPaste) {
					if (!(e.getKeyChar() == copyKey.charAt(0) || e.getKeyChar() == saveKey.charAt(0)
							|| e.getKeyChar() == openKey.charAt(0))) {
						pasteKey = changeChar + "";
						GraphPanel.pasteKey = changeChar;
					}
					setPaste.setText("Set Paste");
					label2.setText( "Paste: Ctrl + " + pasteKey );
					changingPaste = false;
				}
				if (changingSave) {
					if (!(e.getKeyChar() == pasteKey.charAt(0) || e.getKeyChar() == copyKey.charAt(0)
							|| e.getKeyChar() == openKey.charAt(0))) {
						saveKey = changeChar + "";
						GraphPanel.saveKey = changeChar;
					}
					setSave.setText("Set Save");
					label3.setText( "Save: Ctrl + " + saveKey );
					changingSave = false;
				}
				if (changingOpen) {
					if (!(e.getKeyChar() == pasteKey.charAt(0) || e.getKeyChar() == saveKey.charAt(0)
							|| e.getKeyChar() == copyKey.charAt(0))) {
						openKey = changeChar + "";
						GraphPanel.openKey = changeChar;
					}
					setOpen.setText("Set Open");
					label4.setText( "Open: Ctrl + " + openKey );
					changingOpen = false;
				}
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
			
		}
}