package TuringMachine;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

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

public class GraphToolBar extends JPanel {
  /**
	 * 
	 */
  private static final long serialVersionUID = 4774255986563532563L;

  // selection modes
  public final static int SELECT = 0, INSERTSTATE = 1, INSERTEDGE = 2, DELETE = 3,
      SETSTART = 4, SETCURRENT = 5, SETHALT = 6;

  private JToggleButton select = new JToggleButton();
  private JToggleButton insertState = new JToggleButton();
  private JToggleButton insertEdge = new JToggleButton();
  private JToggleButton delete = new JToggleButton( "Delete" );
  private JToggleButton setStartState = new JToggleButton( "Set Start" );
  private JToggleButton setCurrentState = new JToggleButton( "Set Current" );
  private JToggleButton setHaltState = new JToggleButton( "Set Halt" );
  private ArrayList<JToggleButton> buttons = new ArrayList<JToggleButton>();
  private ImageIcon image1;
  private ImageIcon image2;
  private ImageIcon image3;

  public int selectionMode;

  public GraphToolBar() {
    setBorder( BorderFactory.createEmptyBorder( 10, 5, 10, 5 ) );
    // setOrientation(VERTICAL);
    JToggleButton[] buttonArray = {select, insertState, insertEdge, delete, setStartState, setCurrentState, setHaltState};
    for(JToggleButton b : buttonArray) {
      buttons.add(b);
    }
    
    image1 = new ImageIcon( TuringMachine.TuringMachineFrame.class
        .getResource( "/resources/select.gif" ) );
    image2 = new ImageIcon( TuringMachine.TuringMachineFrame.class
        .getResource( "/resources/insertstate.gif" ) );
    image3 = new ImageIcon( TuringMachine.TuringMachineFrame.class
        .getResource( "/resources/insertedge.gif" ) );
    select.setIcon( image1 );
    select.setToolTipText( "Select" );
    insertState.setIcon( image2 );
    insertState.setToolTipText( "Insert State" );
    insertEdge.setIcon( image3 );
    insertEdge.setToolTipText( "Insert Edge" );
    delete.setToolTipText( "Delete State/Edge" );
    setStartState.setToolTipText( "Set Start State" );
    setCurrentState.setToolTipText( "Set Current State" );
    setHaltState.setToolTipText( "Set Halt State" );
    setLayout( new GridLayout( 8, 1 ) );
    add( select );
    add( insertState );
    add( insertEdge );
    add( delete );
    add( new JLabel( "Set States", JLabel.CENTER ) );
    add( setStartState );
    add( setCurrentState );
    add( setHaltState );

    select.addActionListener( new ActionListener() {
      public void actionPerformed( ActionEvent e ) {
        selectionMode = SELECT;
        clearNotSelected();
      }
    } );
    insertState.addActionListener( new ActionListener() {
      public void actionPerformed( ActionEvent e ) {
        selectionMode = INSERTSTATE;
        clearNotSelected();
      }
    } );

    insertEdge.addActionListener( new ActionListener() {
      public void actionPerformed( ActionEvent e ) {
        selectionMode = INSERTEDGE;
        clearNotSelected();
      }
    } );

    delete.addActionListener( new ActionListener() {
      public void actionPerformed( ActionEvent e ) {
        selectionMode = DELETE;
        clearNotSelected();
      }
    } );

    setStartState.addActionListener( new ActionListener() {
      public void actionPerformed( ActionEvent e ) {
        selectionMode = SETSTART;
        clearNotSelected();
      }
    } );

    setCurrentState.addActionListener( new ActionListener() {
      public void actionPerformed( ActionEvent e ) {
        selectionMode = SETCURRENT;
        clearNotSelected();
      }
    } );
    setHaltState.addActionListener( new ActionListener() {
      public void actionPerformed( ActionEvent e ) {
        selectionMode = SETHALT;
        clearNotSelected();
      }
    } );
  }

  public Dimension getMinimumSize() {
    return new Dimension( 110, 500 );
  }

  public Dimension getPreferredSize() {
    return new Dimension( 110, 500 );
  }
  
  //Adds a border around the button currently selected
  //This is used when a key is held down to toggle selection modes
  public void highlightSelectionMode() {
    buttons.get(selectionMode).setSelected(true);
    clearNotSelected();
  }
  
  public void clearNotSelected() {
    for(int i = 0; i < buttons.size(); i++) {
      JToggleButton b = buttons.get(i);
      if(i != selectionMode) {
        b.setSelected(false);
      }
    }
  }
}