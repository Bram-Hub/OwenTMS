package TuringMachine;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JRadioButton;
import javax.swing.WindowConstants;

public class MachineTypePicker extends JDialog {

	private static final long serialVersionUID = 2298842176607713948L;
	
	ButtonGroup typeButtons;
	JRadioButton quadrupleButton;
	JRadioButton quintupleButton;
	JButton okButton;
	
	public MachineTypePicker() {
		
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setModal(true);
		setTitle("Machine Type");
		
		quadrupleButton = new JRadioButton("Quadruple Machine");
		quadrupleButton.setSelected(true);
		
		quintupleButton = new JRadioButton("Quintuple Machine");

		typeButtons = new ButtonGroup();
		typeButtons.add(quadrupleButton);
		typeButtons.add(quintupleButton);
		
		okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();
			}
		});
		
		Container pane = getContentPane();
		pane.setLayout(new GridLayout(3,1,0,0));
		pane.add(quadrupleButton);
		pane.add(quintupleButton);
		pane.add(okButton);
		
	}
	
	int getMachineType() {
		return quadrupleButton.isSelected() ? TM.QUADRUPLE : TM.QUINTUPLE;
	}
	
	public void center() {
	    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	    int screenWidth = screenSize.width;
	    int screenHeight = screenSize.height;

	    Dimension frameSize = this.getSize();
	    int x = ( screenWidth - frameSize.width ) / 2;
	    int y = ( screenHeight - frameSize.height ) / 2;

	    if( x < 0 ) {
	      x = 0;
	      frameSize.width = screenWidth;
	    }

	    if( y < 0 ) {
	      y = 0;
	      frameSize.height = screenHeight;
	    }
	    this.setLocation( x, y );
	  }
	
	public static int getNewMachineType() {
		MachineTypePicker mtp = new MachineTypePicker();
		mtp.pack();
		mtp.center();
		mtp.validate();
		mtp.setVisible(true);
		return mtp.getMachineType();
	}

	
}
