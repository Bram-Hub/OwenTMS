package TuringMachine;

import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

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

class NewTransitionDialog extends JDialog {
  /**
	 * 
	 */
  private static final long serialVersionUID = -67446232348762031L;
  JLabel oldChar = new JLabel( "Character on Tape" );
  JLabel newChar = new JLabel( "New character" );
  JLabel moveDirection = new JLabel( "Move Direction" );
  JTextField oldCharText = new JTextField();
  JTextField newCharText = new JTextField( "NULL" );
  JComboBox directionPick = new JComboBox();
  JButton ok = new JButton( "OK" );
  JButton cancel = new JButton( "Cancel" );
  Edge transition;
  SortedListModel transitions;
  String machineType;
  boolean edit;
  TransitionsPane transitionpanel;

  public NewTransitionDialog( Edge transition, SortedListModel transitions,
      String machineType, boolean edit, TransitionsPane transitionpanel ) {
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
    if( machineType.equals( "Quadruple Machine" )
        && ( !newCharacter.equals( String.valueOf( (char)TM.NULL ) ) && !( (String)directionPick
            .getSelectedItem() ).equals( "NULL" ) ) ) {
      WarningBox temp = new WarningBox(
          "Error: Not a Quadruple Machine transition", this );
      temp.validate();
      temp.setVisible( true );
      return false;
    }
    if( machineType.equals( "Quintuple Machine" )
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
        Edge n = (Edge)transitions.elementAt( i );
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
}

class NewStateDialog extends JDialog {
  /**
	 * 
	 */
  private static final long serialVersionUID = 8314741997670570749L;
  JLabel stateName = new JLabel( "State Name: ", JLabel.RIGHT );
  JTextField stateText = new JTextField();
  JLabel halting = new JLabel( "Halting State: ", JLabel.RIGHT );
  JCheckBox halt = new JCheckBox();
  JButton ok = new JButton( "OK" );
  JButton cancel = new JButton( "Cancel" );
  State newState;
  Vector<?> states;

  NewStateDialog( State newState, Vector<?> states ) {
    this.newState = newState;
    this.states = states;
    Container mypane = getContentPane();
    mypane.setLayout( new GridLayout( 3, 2, 0, 0 ) );
    setTitle( "New State Settings" );

    stateText.setText( newState.stateName );
    mypane.add( stateName );
    mypane.add( stateText );
    mypane.add( halting );
    mypane.add( halt );
    mypane.add( ok );
    mypane.add( cancel );

    ok.addActionListener( new ActionListener() {
      public void actionPerformed( ActionEvent e ) {
        if( stateText.getText().length() > 0 ) {
          updateState();
          dispose();
        }
      }
    } );
    cancel.addActionListener( new ActionListener() {
      public void actionPerformed( ActionEvent e ) {
        if( stateText.getText().length() > 0 ) {
          removeState();
          dispose();
        }
      }
    } );
  }

  void updateState() {
    newState.stateName = stateText.getText();
    if( halt.isSelected() ) newState.finalState = true;
    boolean current = false;
    boolean start = false;
    for( int i = 0; i < states.size(); i++ ) {
      if( ( (State)states.elementAt( i ) ).currentState ) current = true;
      if( ( (State)states.elementAt( i ) ).startState ) start = true;
    }
    if( !current ) newState.currentState = true;
    if( !start ) newState.startState = true;
  }

  void removeState() {
    states.removeElement( newState );
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

class WarningBox extends JDialog {
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
