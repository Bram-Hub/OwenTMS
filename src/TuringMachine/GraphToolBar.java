package TuringMachine;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
  private ImageIcon image1;
  private ImageIcon image2;
  private ImageIcon image3;

  public int selectionMode;

  public GraphToolBar() {
    setBorder( BorderFactory.createEmptyBorder( 10, 5, 10, 5 ) );
    // setOrientation(VERTICAL);

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
        insertState.setSelected( false );
        insertEdge.setSelected( false );
        delete.setSelected( false );
        setStartState.setSelected( false );
        setCurrentState.setSelected( false );
        setHaltState.setSelected( false );
        selectionMode = SELECT;
      }
    } );
    insertState.addActionListener( new ActionListener() {
      public void actionPerformed( ActionEvent e ) {
        select.setSelected( false );
        insertEdge.setSelected( false );
        delete.setSelected( false );
        setStartState.setSelected( false );
        setCurrentState.setSelected( false );
        setHaltState.setSelected( false );
        selectionMode = INSERTSTATE;
      }
    } );

    insertEdge.addActionListener( new ActionListener() {
      public void actionPerformed( ActionEvent e ) {
        insertState.setSelected( false );
        select.setSelected( false );
        delete.setSelected( false );
        setStartState.setSelected( false );
        setCurrentState.setSelected( false );
        setHaltState.setSelected( false );
        selectionMode = INSERTEDGE;
      }
    } );

    delete.addActionListener( new ActionListener() {
      public void actionPerformed( ActionEvent e ) {
        insertState.setSelected( false );
        insertEdge.setSelected( false );
        select.setSelected( false );
        setStartState.setSelected( false );
        setCurrentState.setSelected( false );
        setHaltState.setSelected( false );
        selectionMode = DELETE;
      }
    } );

    setStartState.addActionListener( new ActionListener() {
      public void actionPerformed( ActionEvent e ) {
        insertState.setSelected( false );
        insertEdge.setSelected( false );
        select.setSelected( false );
        delete.setSelected( false );
        setCurrentState.setSelected( false );
        setHaltState.setSelected( false );
        selectionMode = SETSTART;
      }
    } );

    setCurrentState.addActionListener( new ActionListener() {
      public void actionPerformed( ActionEvent e ) {
        insertState.setSelected( false );
        insertEdge.setSelected( false );
        select.setSelected( false );
        setStartState.setSelected( false );
        delete.setSelected( false );
        setHaltState.setSelected( false );
        selectionMode = SETCURRENT;
      }
    } );
    setHaltState.addActionListener( new ActionListener() {
      public void actionPerformed( ActionEvent e ) {
        insertState.setSelected( false );
        insertEdge.setSelected( false );
        select.setSelected( false );
        setStartState.setSelected( false );
        delete.setSelected( false );
        setCurrentState.setSelected( false );
        selectionMode = SETHALT;
      }
    } );
  }

  public Dimension getMinimumSize() {
    return new Dimension( 110, 500 );
  }

  public Dimension getPreferredSize() {
    return new Dimension( 110, 500 );
  }
}
