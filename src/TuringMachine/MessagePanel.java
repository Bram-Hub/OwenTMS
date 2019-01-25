package TuringMachine;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

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
  JComboBox<String> speed;
  JLabel nonblanks;
  JLabel totaltransitions;
  JTextField inputString;
  JTextField inputsQuantity;
  JLabel stateCount;
  JLabel edgeCount;
  MultipleInputs miWindow;

  // references to exterior components
  TM machine;
  Thread execution;

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
    speed = new JComboBox<String>();
    speed.addItem( "Slow" );
    speed.addItem( "Fast" );
    speed.addItem( "Very Fast" );
    speed.addItem( "Compute" );
    buttonpanel.add( start );
    buttonpanel.add( stop );
    buttonpanel.add( step );
    buttonpanel.add( speedLabel );
    buttonpanel.add( speed );

    JButton resetAll = new JButton( "Reset All State"),
            reset = new JButton( "Reset Machine" ), 
            clearTape = new JButton( "Clear Tape" ), 
            loadInput = new JButton( "Load Input String" ), 
            multipleInputs = new JButton( "Multiple Inputs" );

    nonblanks = new JLabel( new String( "Non 0's on Tape: " ).concat( String
        .valueOf( 0 ) ) );
    totaltransitions = new JLabel( new String( "Transitions Made: " )
        .concat( String.valueOf( 0 ) ) );

    inputString = new JTextField();
    inputString.setToolTipText("Use [ ] to indicate starting position");
    edgeCount = new JLabel();
    stateCount = new JLabel();
    
    optionspanel = new JPanel();
    optionspanel.setLayout( new GridLayout( 5, 2, 5, 0 ) );
    optionspanel.add( reset );
    optionspanel.add( totaltransitions );
    optionspanel.add( clearTape );
    optionspanel.add( nonblanks );
    optionspanel.add( loadInput );
    optionspanel.add( inputString );
    optionspanel.add( resetAll );
    optionspanel.add( multipleInputs );
    optionspanel.add(stateCount);
    optionspanel.add(edgeCount);
    optionspanel.setPreferredSize( new Dimension( 310, 150 ) );

    add( buttonpanel, BorderLayout.WEST );
    add( scrollmessages, BorderLayout.CENTER );
    add( optionspanel, BorderLayout.EAST );

    start.addActionListener( new ActionListener() {
      public void actionPerformed( ActionEvent e ) {
    	 machine.tape.editCellAt(-1, -1);
    	 machine.tape.clearSelection();
        if( execution.isAlive() )
          addMessage( "Already running" );
        else {
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
            updateLabels( machine.nonBlanks, machine.totalTransitions, machine.states.size(), machine.transitions.size() );
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

    resetAll.addActionListener( new ActionListener() {
        public void actionPerformed( ActionEvent e ) {
            resetMachineAction();
            clearTapeAction();
            loadInputAction();
        }
        });

    reset.addActionListener( new ActionListener() {
      public void actionPerformed( ActionEvent e ) {
        resetMachineAction();
      }
    } );

    clearTape.addActionListener( new ActionListener() {
      public void actionPerformed( ActionEvent e ) {
        clearTapeAction();
      }
    } );

    loadInput.addActionListener( new ActionListener() {
      public void actionPerformed( ActionEvent e ) {
        loadInputAction();
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

  public void loadInputAction() {
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
        addMessage( "Improper use of []" );
        return;
    }
    if(left == 0)
        start = 0;
    else
    {
        if(rightpos - leftpos != 2)
        {
            addMessage( "Improper use of []" );
            return;
        }
        start = leftpos;
        temp1 = input.substring(0,leftpos) + input.substring(leftpos+1, rightpos) + input.substring(rightpos+1);
        input = temp1;
    }
    machine.loadInputString( input, start);
    addMessage( input.concat( " loaded onto tape" ) );
    updateLabels( machine.nonBlanks, machine.totalTransitions, machine.states.size(), machine.transitions.size());

  }

  public void clearTapeAction() {
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
        updateLabels( machine.nonBlanks, machine.totalTransitions,machine.states.size(), machine.transitions.size()  );
        addMessage( "Tape Cleared" );

  }
  
  public void repaint() {
	  super.repaint();
  }

  public void resetMachineAction() {
        for( int i = 0; i < machine.states.size(); i++ ) {
          State n = machine.states.elementAt( i );
          n.currentState = false;
          if( n.startState ) {
            n.currentState = true;
            machine.currentState = n;
          }
        }
        machine.currentEdge = null;
        for( int i = 0; i < machine.transitions.size(); i++ ) {
          Edge n = machine.transitions.elementAt( i );
          n.currentEdge = false;
        }
        machine.totalTransitions = 0;
        updateLabels( machine.nonBlanks, machine.totalTransitions, machine.states.size(), machine.transitions.size()  );
        addMessage( "Machine Reset" );

  }

  public void setMachine( TM machine ) {
    this.machine = machine;
  }

  public void setExecution( Thread execution ) {
    this.execution = execution;
  }

  public void addMessage( String msg ) {
    messages.append( msg + "\n" );
    scrollmessages.getVerticalScrollBar().setValue(
        scrollmessages.getVerticalScrollBar().getMaximum() );
  }

  public void updateLabels( int nonBlankChars, int transitions, int numStates, int numTransitions) {
    nonblanks.setText( new String( " Non 0's on Tape: " ).concat( String
        .valueOf( nonBlankChars ) ) );
    totaltransitions.setText( new String( "Transitions Made: " ).concat( String
        .valueOf( transitions ) ) );
    stateCount.setText( new String("States: ").concat(String.valueOf(numStates)));
    edgeCount.setText( new String("Transitions: ").concat(String.valueOf(numTransitions)));
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

  public Dimension getMinimumSize() {
    return new Dimension( 500, 150 );
  }

  public Dimension getPreferredSize() {
    return new Dimension( 500, 150 );
  }
}
