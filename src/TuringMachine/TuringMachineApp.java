package TuringMachine;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.UIManager;

/**
 * Main class - creates the TuringMachineFrame and places it on the screen
 * 
 * @author Owen F. Kellett
 * @version 1.0
 */

public class TuringMachineApp {
  private int instances;

  public TuringMachineApp() {
	  instances = 0;
  }
  
  public void close() {
	  instances--;
	  if(instances == 0) {
		  System.exit(0);
	  }
  }
  
  public void createNewInstance(File inputFile, int machineType) {
	    JFrame frame = new TuringMachineFrame(this,inputFile, machineType);
	    // Validate frames that have preset sizes
	    // Pack frames that have useful preferred size info, e.g. from their layout

	    frame.validate();
	    frame.setSize( new Dimension( 1024, 764 ) );
	    // Center the window
	    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	    Dimension frameSize = frame.getSize();
	    if( frameSize.height > screenSize.height ) {
	      frameSize.height = screenSize.height;
	    }
	    if( frameSize.width > screenSize.width ) {
	      frameSize.width = screenSize.width;
	    }
	    frame.setLocation( ( screenSize.width - frameSize.width ) / 2,
	        ( screenSize.height - frameSize.height ) / 2 );
	    frame.setVisible( true );
	    instances++;
  }
  public void createNewInstance(File inputFile) {
	  createNewInstance(inputFile, 0);
  }
  
  public static String getUsage() {
	  return "Usage: java -jar TuringMachine4.jar [input file] [-type=quad|quint]";
  }

  // Main method
  public static void main( String[] args ) {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }
    catch ( Exception e ) {
      e.printStackTrace();
    }
    File file = null;
    int machineType = 0;
    if(args.length > 0) {
        for(String arg : args) {
        	if(arg.equals("-h") || arg.equals("--help")) {
        		System.out.println(getUsage());
        		System.exit(0);
        	}
            if(arg.toLowerCase().startsWith("-type=")) {
            	String type = arg.split("=")[1].toLowerCase();
            	if(type.startsWith("quad")) {
            		machineType = TM.QUADRUPLE;
            	} else if(type.startsWith("quint")) {
            		machineType = TM.QUINTUPLE;
            	} else {
            		System.out.println("\'" + type + "\' is not a valid transition type!");
            		System.exit(0);
            	}
            } else {
    	        file = new File(arg);
    	        if(!file.exists()) {
    	        	System.out.println("Input file \'" + arg + "\' does not exist!");
    	        	System.exit(0);
    	        	file = null;
    	        }
            }
        }
    }
    TuringMachineApp tma = new TuringMachineApp();
    tma.createNewInstance(file,machineType);
  }
}
