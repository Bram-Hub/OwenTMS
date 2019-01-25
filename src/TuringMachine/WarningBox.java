package TuringMachine;

import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;


/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2002
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author unascribed
 * @version 1.0
 */

public class WarningBox extends JDialog {
  /**
	 * 
	 */
  private static final long serialVersionUID = -1706140609251353405L;

  public WarningBox( String warning, Dialog owner ) {
    super( owner );
    JLabel warn = new JLabel( warning, JLabel.CENTER );
    JButton ok = new JButton( "OK" );
    Container myPane = getContentPane();
    GridLayout myLayout = new GridLayout( 2, 1 );
    myPane.setLayout( myLayout );
    myPane.add( warn );
    myPane.add( ok );
    FontMetrics fm = myPane.getFontMetrics( myPane.getFont() );
    int width = fm.stringWidth( warning );
    setSize( width + 20, 75 );
    center();
    setTitle( "Error" );

    ok.addActionListener( new ActionListener() {
      public void actionPerformed( ActionEvent e ) {
        dispose();
      }
    } );
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
}
