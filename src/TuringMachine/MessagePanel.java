package TuringMachine;

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

public class MessagePanel extends JPanel {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  // interior components
  JScrollPane scrollmessages;
  JPanel buttonpanel;
  JPanel optionspanel;
  JTextArea messages;
  JLabel currentState;
  JComboBox speed;
  JComboBox machineType;
  JLabel nonblanks;
  JLabel totaltransitions;
  JTextField inputString;
  JTextField inputsQuantity;
  MultipleInputs miWindow;

  // references to exterior components
  TM machine;
  Thread execution;
  // TapePanel tapeDisplay;

  public MessagePanel() {
    setLayout( new BorderLayout() );
    setBorder( BorderFactory.createTitledBorder( "Control Panel" ) );
    
    miWindow = new MultipleInputs( 5 );
    miWindow.setMessagePanel( this );
    
    messages = new JTextArea();
    messages.setAutoscrolls( true );
    messages.setEditable( false );

    scrollmessages = new JScrollPane( messages );

    setFont( new Font( "Helvetica", Font.BOLD, 14 ) );

    buttonpanel = new JPanel();
    buttonpanel.setLayout( new GridLayout( 5, 1 ) );

    currentState = new JLabel( "State: H", JLabel.CENTER );
    JButton start = new JButton( "Start" ), 
            stop = new JButton( "Stop" ),
            step = new JButton( "Step" );
    JLabel speedLabel = new JLabel( "Speed", JLabel.CENTER );
    speed = new JComboBox();
    speed.addItem( "Slow" );
    speed.addItem( "Fast" );
    speed.addItem( "Very Fast" );
    speed.addItem( "Compute" );
    buttonpanel.add( start );
    buttonpanel.add( stop );
    buttonpanel.add( step );
    buttonpanel.add( speedLabel );
    buttonpanel.add( speed );

    JButton reset = new JButton( "Reset Machine" ), 
            clearTape = new JButton( "Clear Tape" ), 
            loadInput = new JButton( "Load Input String" ), 
            multipleInputs = new JButton( "Multiple Inputs" );

    nonblanks = new JLabel( new String( "Non 0's on Tape: " ).concat( String
        .valueOf( 0 ) ) );
    totaltransitions = new JLabel( new String( "Transitions Made: " )
        .concat( String.valueOf( 0 ) ) );
    JLabel machineTypeLabel = new JLabel( "Machine Type:", JLabel.RIGHT );

    inputString = new JTextField();
    inputString.setToolTipText("Use [ ] to indicate starting position");
    machineType = new JComboBox();
    machineType.addItem( "Quadruple Machine" );
    machineType.addItem( "Quintuple Machine" );
    optionspanel = new JPanel();
    optionspanel.setLayout( new GridLayout( 5, 2, 5, 0 ) );
    optionspanel.add( reset );
    optionspanel.add( totaltransitions );
    optionspanel.add( clearTape );
    optionspanel.add( nonblanks );
    optionspanel.add( machineTypeLabel );
    optionspanel.add( machineType );
    optionspanel.add( loadInput );
    optionspanel.add( inputString );
    optionspanel.add( multipleInputs );
    optionspanel.setPreferredSize( new Dimension( 310, 150 ) );

    add( buttonpanel, BorderLayout.WEST );
    add( scrollmessages, BorderLayout.CENTER );
    add( optionspanel, BorderLayout.EAST );

    /*
     * setLayout(gbl);
     * 
     * // add current state label gbcon.gridx = 0; gbcon.gridy = 0;
     * gbcon.gridwidth = 6; gbcon.anchor = GridBagConstraints.NORTH; //
     * gbcon.weighty = 0.1; gbl.setConstraints(currentState, gbcon);
     * add(currentState);
     * 
     * // add start, stop, and step control buttons gbcon.gridy = 1;
     * gbcon.gridwidth = 1; gbcon.insets = new Insets(0,0,0,40); //gbcon.weightx
     * = 0.2; gbcon.weighty = 0.1; gbl.setConstraints(start, gbcon); add(start);
     * gbcon.gridx = 1; gbl.setConstraints(stop, gbcon); add(stop); gbcon.gridx
     * = 2; gbl.setConstraints(resume, gbcon); add(resume); gbcon.gridx = 3;
     * gbl.setConstraints(step, gbcon); add(step);
     * 
     * // add speed pull-down list control gbcon.gridx = 4; gbcon.insets = new
     * Insets(0,0,0,5); gbl.setConstraints(speedLabel, gbcon); add(speedLabel);
     * gbcon.gridx = 5; gbl.setConstraints(speed, gbcon); add(speed);
     * 
     * // add message area gbcon.gridx = 0; gbcon.gridy = 2; gbcon.insets = new
     * Insets(0,0,5,0); gbcon.gridwidth = 6; gbcon.fill =
     * GridBagConstraints.BOTH; gbcon.anchor = GridBagConstraints.SOUTH;
     * gbcon.weightx = 0; //gbcon.weighty = 0.8;
     * gbl.setConstraints(scrollmessages, gbcon); add(scrollmessages);
     */

    start.addActionListener( new ActionListener() {
      public void actionPerformed( ActionEvent e ) {
    	 machine.tape.editCellAt(-1, -1);
    	 machine.tape.clearSelection();
        if( execution.isAlive() )
          addMessage( "Already running" );
        else {
          // StringBuffer errorMsg = new StringBuffer(50);
          // boolean success = true;
          // machine.initMachine(5000, "", new StringBuffer(""));
          machine.setSpeed( (String)speed.getSelectedItem() );
          addMessage( "Running..." );
          execution = new Thread( machine );
          execution.start();
          addMessage( "Machine Started" );
        }
      }
    } );

    stop.addActionListener( new ActionListener() {
      @SuppressWarnings( "deprecation" )
      public void actionPerformed( ActionEvent e ) {
        if( execution.isAlive() ) {
          execution.stop();
          if( machine.nextStateNotSet ) {
            machine.setState( machine.currentEdge.toState );
            machine.totalTransitions++;
            updateLabels( machine.nonBlanks, machine.totalTransitions );
          }
          machine.clearEdge();
          machine.moving = TM.STAY;
          machine.tape.setEnabled(true);
          // tapeDisplay.repaint();
          addMessage( "User interrupt\nMachine halted" );
        }
        else addMessage( "Machine is not running" );
      }
    } );

    step.addActionListener( new ActionListener() {
      public void actionPerformed( ActionEvent e ) {
        String result;
        result = machine.transition();
        machine.moving = TM.STAY;

        // tapeDisplay.repaint();
        addMessage( result );
      }
    } );

    reset.addActionListener( new ActionListener() {
      public void actionPerformed( ActionEvent e ) {
        for( int i = 0; i < machine.states.size(); i++ ) {
          State n = (State)machine.states.elementAt( i );
          n.currentState = false;
          if( n.startState ) {
            n.currentState = true;
            machine.currentState = n;
          }
        }
        machine.currentEdge = null;
        for( int i = 0; i < machine.transitions.size(); i++ ) {
          Edge n = (Edge)machine.transitions.elementAt( i );
          n.currentEdge = false;
        }
        machine.totalTransitions = 0;
        updateLabels( machine.nonBlanks, machine.totalTransitions );
//        machine.leftMost = machine.tapePos;
//        machine.rightMost = machine.tapePos;
        addMessage( "Machine Reset" );
      }
    } );

    clearTape.addActionListener( new ActionListener() {
      public void actionPerformed( ActionEvent e ) {
     	 machine.tape.editCellAt(-1, -1);
     	 machine.tape.clearSelection();
	  for( int j = 0; j < machine.tape.getColumnCount(); j++ ) {
          machine.tape.setValueAt( new Character( '0' ), 0, j );
          machine.tape.getColumnModel().getColumn( j ).setHeaderValue(
        		  new Character( '0' ) );
        }
	
			
		machine.tape.getColumnModel().getColumn( machine.tape.getColumnCount()/2 ).setHeaderValue(
		          new Character( '-' ) );
		machine.tape.getTableHeader().repaint();
		machine.tapePos = machine.tape.getColumnCount()/2;
		machine.tape.scrollRectToVisible( machine.tape.getCellRect( 0, machine.tapePos - 5, true ) );
		machine.tape.scrollRectToVisible( machine.tape.getCellRect( 0, machine.tapePos + 5, true ) );
		
	
        machine.nonBlanks = 0;
        updateLabels( machine.nonBlanks, machine.totalTransitions );
	
        addMessage( "Tape Cleared" );
      }
    } );

    loadInput.addActionListener( new ActionListener() {
      public void actionPerformed( ActionEvent e ) {
    	  int left = 0;
    	  int leftpos = -1;
    	  int right = 0;
    	  int rightpos = -1;
    	  int start;
    	  String temp1;
    	  String input = inputString.getText();
      	 machine.tape.editCellAt(-1, -1);
      	 machine.tape.clearSelection();
    	for(int i = 0; i < input.length(); i++)
    	{
    		if(!machine.validTapeChar(input.charAt(i)))
    		{
    			addMessage( "Tape input contains invalid characters" );
    			return;
    		}
    		if(input.charAt(i) == '[')
    		{
    			left++;
    			if(leftpos < 0)
    				leftpos = i;
    		}
    		if(input.charAt(i) == ']')
    		{
    			right++;
    			if(rightpos < 0)
    				rightpos = i;
    		}
    	}
    	if(!((left == 0 && right == 0) || (left == 1 && right == 1)))
    	{
    		addMessage( "Impropper use of []" );
    		return;
    	}
    	if(left == 0)
    		start = 0;
    	else
    	{
    		if(rightpos - leftpos != 2)
    		{
    			addMessage( "Impropper use of []" );
    			return;
    		}
    		start = leftpos;
    		temp1 = input.substring(0,leftpos) + input.substring(leftpos+1, rightpos) + input.substring(rightpos+1);
    		input = temp1;
    	}
        machine.loadInputString( input, start);
        addMessage( input.concat( " loaded onto tape" ) );
        updateLabels( machine.nonBlanks, machine.totalTransitions );
      }
    } );
    /*
     * pops up window, window has textfields for user to enter tape entries, and
     * a run button that sets the output textfields to what is left on the tape
     * after the inputs are run.
     */
    multipleInputs.addActionListener( new ActionListener() {
      public void actionPerformed( ActionEvent e ) {
      	machine.tape.editCellAt(-1, -1);
      	machine.tape.clearSelection();
        miWindow.setMachine( machine );
        miWindow.setThread( execution );
        miWindow.setVisible( true );
      }
    } );
  }

  public void setMachine( TM machine ) {
    this.machine = machine;
  }

  public void setExecution( Thread execution ) {
    this.execution = execution;
  }

  /*
   * public void setTapeDisplay(TapePanel display) { // tapeDisplay = display; }
   */

  public void addMessage( String msg ) {
    messages.append( msg + "\n" );
    scrollmessages.getVerticalScrollBar().setValue(
        scrollmessages.getVerticalScrollBar().getMaximum() );
  }

  public void updateLabels( int nonBlankChars, int transitions ) {
    nonblanks.setText( new String( " Non 0's on Tape: " ).concat( String
        .valueOf( nonBlankChars ) ) );
    totaltransitions.setText( new String( "Transitions Made: " ).concat( String
        .valueOf( transitions ) ) );
  }

  public void showResults( int haltReason ) {
    addMessage( "\nMachine halted:" );
    switch ( haltReason ) {
    case TM.HALTED:
      addMessage( "Halt state reached" );
      break;
    case TM.ABNORMAL:
      addMessage( "The machine ran off the tape" );
      break;
    case TM.NOTFOUND:
      addMessage( "No applicable transition found" );
      break;
    case TM.USERINT:
      addMessage( "User interrupt" );
      break;
    }
    addMessage( "" + machine.totalTransitions + " total transitions" );
    addMessage( "" + machine.nonBlanks + " non-blank characters on tape" );
  }

  /*
   * public boolean action(Event evt, Object arg) {
   * 
   * 
   * if (evt.target instanceof JButton) { String command =
   * ((JButton)evt.target).getLabel(); addMessage(command); int result; if
   * (command.equals("Step")) { result = machine.transition(); machine.moving =
   * TM.STAY; // tapeDisplay.repaint(); if (result == TM.HALTED)
   * addMessage("Machine is halted"); else if (result == TM.ABNORMAL)
   * addMessage("The machine has run\noff the end of the tape"); else if (result
   * == TM.NOTFOUND) addMessage("No applicable transition found"); else if
   * (result == TM.NOPROG) addMessage("No program entered"); return true; } else
   * if (command.equals("Start") || command.equals("Resume")) { if
   * (execution.isAlive()) addMessage("Already running"); else { StringBuffer
   * errorMsg = new StringBuffer(50); boolean success = true; if
   * (command.equals("Start")) { System.out.println("Started");
   * machine.initMachine(5000, "", new StringBuffer(""));
   * machine.setSpeed((String)speed.getSelectedItem());
   * addMessage("Running..."); execution = new Thread(machine);
   * execution.start(); } else
   * addMessage("Error initializing machine:\n"+errorMsg); } } else if
   * (command.equals("Stop")) { if (execution.isAlive()) { execution.stop();
   * machine.moving = TM.STAY; // tapeDisplay.repaint();
   * showResults(TM.USERINT); } else addMessage("Machine is not running"); } }
   * return false; }
   */

  public Dimension getMinimumSize() {
    return new Dimension( 500, 150 );
  }

  public Dimension getPreferredSize() {
    return new Dimension( 500, 150 );
  }
}