package TuringMachine;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;


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

public class NewTransitionDialog extends JDialog implements ActionListener, DocumentListener {
  /**
	 * 
	 */
  private static final long serialVersionUID = -67446232348762031L;
  JLabel oldChar = new JLabel( "Character on Tape" );
  JLabel newChar = new JLabel( "New character" );
  JLabel moveDirection = new JLabel( "Move Direction" );
  JTextField oldCharText = new JTextField();
  JTextField newCharText = new JTextField( "NULL" );
  JComboBox<String> directionPick = new JComboBox<String>();
  JButton ok = new JButton( "OK" );
  JButton cancel = new JButton( "Cancel" );
  Edge transition;
  SortedListModel transitions;
  int machineType;
  boolean edit;
  TransitionsPane transitionpanel;
  MessagePanel mp;

  public NewTransitionDialog( Edge transition, SortedListModel transitions,
      int machineType, boolean edit, TransitionsPane transitionpanel, final MessagePanel mp) {
	this.mp = mp;
	setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
    this.transitionpanel = transitionpanel;
    this.edit = edit;
    this.machineType = machineType;
    this.transition = transition;
    this.transitions = transitions;
    setTitle( "New Transition Settings" );
    Container myPane = getContentPane();
    GridLayout myLayout = new GridLayout( 4, 2 );
    myLayout.setHgap( 20 );
    myPane.setLayout( myLayout );

    directionPick.addItem( "Left" );
    directionPick.addItem( "Right" );
    directionPick.addItem( "NULL" );
    directionPick.setSelectedItem( "NULL" );
    directionPick.setActionCommand("Direction");
    directionPick.addActionListener(this);
    
    newCharText.setActionCommand("CharText");
    newCharText.getDocument().addDocumentListener(this);

    if( edit ) {
      oldCharText.setText( String.valueOf( transition.oldChar ) );
      newCharText.setText( String.valueOf( transition.newChar ) );
      if( transition.newChar == TM.NULL ) newCharText.setText( "NULL" );
      if( transition.direction == TM.LEFT )
        directionPick.setSelectedItem( "Left" );
      else if( transition.direction == TM.RIGHT )
        directionPick.setSelectedItem( "Right" );
      else directionPick.setSelectedItem( "NULL" );
    }

    myPane.add( oldChar );
    myPane.add( oldCharText );
    myPane.add( newChar );
    myPane.add( newCharText );
    myPane.add( moveDirection );
    myPane.add( directionPick );
    myPane.add( ok );
    myPane.add( cancel );

    ok.addActionListener( new ActionListener() {
      public void actionPerformed( ActionEvent e ) {
        if( checkItems() ) {
          updateTransition();
          dispose();
        }
      }
    } );
    cancel.addActionListener( new ActionListener() {
      public void actionPerformed( ActionEvent e ) {
        removeTransition();
        mp.updateLabels( mp.machine.nonBlanks, mp.machine.totalTransitions, mp.machine.states.size(), mp.machine.transitions.size());

        dispose();
      }
    } );
  }

  void updateTransition() {
    transition.oldChar = oldCharText.getText().charAt( 0 );
    if( newCharText.getText().length() > 1 )
      transition.newChar = (char)TM.NULL;
    else transition.newChar = newCharText.getText().charAt( 0 );
    String dir = (String)directionPick.getSelectedItem();
    if( dir.equals( "Left" ) )
      transition.direction = TM.LEFT;
    else if( dir.equals( "Right" ) )
      transition.direction = TM.RIGHT;
    else if( dir.equals( "NULL" ) ) transition.direction = TM.NULL;
    transitionpanel.getViewport().repaint();
    transitions.removeElement( transition );
    transitions.addSortedElement( transition );
  }

  void removeTransition() {
    if( !edit ) transitions.removeElement( transition );
    
  }

  boolean checkItems() {
    String oldCharacter = oldCharText.getText();
    String newCharacter = newCharText.getText();
    if( newCharacter.equals( "NULL" ) )
      newCharacter = String.valueOf( (char)TM.NULL );
    if( oldCharacter.length() != 1 || newCharacter.length() != 1 ) {
      WarningBox temp = new WarningBox( "Error: Single Characters only", this );
      temp.validate();
      temp.setVisible( true );
      return false;
    }
    if( newCharacter.equals( String.valueOf( (char)TM.NULL ) )
        && ( (String)directionPick.getSelectedItem() ).equals( "NULL" ) ) {
      WarningBox temp = new WarningBox( "Error: No action specified", this );
      temp.validate();
      temp.setVisible( true );
      return false;
    }
    if( machineType == TM.QUADRUPLE
        && ( !newCharacter.equals( String.valueOf( (char)TM.NULL ) ) && !( (String)directionPick
            .getSelectedItem() ).equals( "NULL" ) ) ) {
      WarningBox temp = new WarningBox(
          "Error: Not a Quadruple Machine transition", this );
      temp.validate();
      temp.setVisible( true );
      return false;
    }
    if(machineType == TM.QUINTUPLE
        && ( newCharacter.equals( String.valueOf( (char)TM.NULL ) ) || ( (String)directionPick
            .getSelectedItem() ).equals( "NULL" ) ) ) {
      WarningBox temp = new WarningBox(
          "Error: Not a Quintuple Machine transition", this );
      temp.validate();
      temp.setVisible( true );
      return false;
    }
    if( !edit ) {
      for( int i = 0; i < transitions.size(); i++ ) {
        Edge n = transitions.elementAt( i );
        if( n.fromState == transition.fromState ) {
          if( n.oldChar == oldCharText.getText().charAt( 0 ) ) {
            WarningBox temp = new WarningBox(
                "Error: Makes machine non-deterministic", this );
            temp.validate();
            temp.setVisible( true );
            return false;
          }
        }
      }
    }
    return true;
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
  public void actionPerformed(ActionEvent e) {
    if(e.getActionCommand().equals("Direction")) {
      if(machineType == TM.QUADRUPLE) {
        if(directionPick.getSelectedItem().equals("NULL")) {
        	newCharText.setEnabled(true);
        } else {
        	newCharText.setEnabled(false);
        }
      }
    }
  }
  public void changedUpdate(DocumentEvent de) {
	  if(machineType == TM.QUADRUPLE) {
  		if(newCharText.getText().equals("NULL")) {
  			directionPick.setEnabled(true);
  		} else {
  			directionPick.setEnabled(false);
  		}
  	}
  }
  public void insertUpdate(DocumentEvent de) {
	  changedUpdate(de);
  }
  public void removeUpdate(DocumentEvent de) {
	  changedUpdate(de);
  }
}

