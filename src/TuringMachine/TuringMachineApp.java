package TuringMachine;

import java.awt.*;
import javax.swing.UIManager;

/**
 * Main class - creates the TuringMachineFrame and places it on the screen
 * 
 * @author Owen F. Kellett
 * @version 1.0
 */

public class TuringMachineApp {
  private boolean packFrame = false;

  // Construct the application
  public TuringMachineApp() {
    TuringMachineFrame frame = new TuringMachineFrame();
    // Validate frames that have preset sizes
    // Pack frames that have useful preferred size info, e.g. from their layout
    if( packFrame ) {
      frame.pack();
    }
    else {
      frame.validate();
    }
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
  }

  // Main method
  public static void main( String[] args ) {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }
    catch ( Exception e ) {
      e.printStackTrace();
    }
    new TuringMachineApp();
  }
}
