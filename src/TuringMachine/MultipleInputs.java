package TuringMachine;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import java.util.Vector;

public class MultipleInputs extends JFrame {

  private static final long serialVersionUID = 1L;

  private int nPuts;
  private Thread action;
  
  private TM machine;
  private MessagePanel mp;
  private Thread execution;
  private Vector<JTextField> inputs;
  private Vector<JLabel> outputs;

  private JLabel inLabel;
  private JLabel outLabel;
  private JButton run;
  private JButton stop;
  
  public void setMachine( TM m ){
    this.machine = m;
  }
  
  public void setMessagePanel( MessagePanel mp ){
    this.mp = mp;
  }

  public void setThread( Thread e ){
    this.execution = e;
  }

  public void setInputs(Vector<String> input){
      for(int i = 0; i<input.size(); i++){
          this.inputs.get(i).setText(input.get(i));
      }
  }

  public Vector<JTextField> getInputs(){
      return inputs;
  }

  MultipleInputs( int n ) {
    super( "Multiple Inputs" );
    this.setVisible( false );
    this.setBounds( 0, 0, 500, 250 );
    this.setLayout( new GridLayout( n + 2, 2, 10, 10 ) );
    
    this.nPuts = n;

    // Center the window
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension frameSize = this.getSize();
    if( frameSize.height > screenSize.height ) {
      frameSize.height = screenSize.height;
    }
    if( frameSize.width > screenSize.width ) {
      frameSize.width = screenSize.width;
    }
    this.setLocation( ( screenSize.width - frameSize.width ) / 2,
        ( screenSize.height - frameSize.height ) / 2 );

    inLabel = new JLabel( "Inputs" );
    outLabel = new JLabel( "Outputs" );
    inLabel.setVerticalAlignment( SwingConstants.BOTTOM );
    inLabel.setHorizontalAlignment( SwingConstants.CENTER );
    outLabel.setVerticalAlignment( SwingConstants.BOTTOM );
    outLabel.setHorizontalAlignment( SwingConstants.CENTER );

    inputs = new Vector<JTextField>();
    outputs = new Vector<JLabel>();
    for( int i = 0; i < n; ++i ) {
      inputs.add( new JTextField() );
      JLabel temp = new JLabel();
      temp.setBackground( Color.WHITE );
      temp.setOpaque( true );
      temp.setHorizontalAlignment( JLabel.CENTER );
      outputs.add( temp );
    }

    run = new JButton( "Run" );
    stop = new JButton( "Stop" );

    this.add( inLabel );
    this.add( outLabel );
    for( int i = 0; i < n; ++i ) {
      this.add( inputs.get( i ) );
      this.add( outputs.get( i ) );
    }
    this.add( run );
    this.add( stop );

    run.addActionListener( new ActionListener() {
      public void actionPerformed( ActionEvent e ) {
        action = new Thread( new Runner() );
        action.start();
      }
    } );

    stop.addActionListener( new ActionListener() {
      public void actionPerformed( ActionEvent e ) {
        stopAll();
      }
    } );
  }

  private class Runner implements Runnable {
    
    public void run() {
      /* SET MACHINE SPEED */
      machine.setSpeed( "Compute" );
      for( int i = 0; i < nPuts; i++ ) {
        if( inputs.get( i ).getText().equals( "" ) ) continue;
        
        /* CLEAR TAPE */
        for( int j = 0; j < machine.tape.getColumnCount(); j++ ) {
          machine.tape.setValueAt( new Character( '0' ), 0, j );
        }
        machine.nonBlanks = 0;
        mp.updateLabels( machine.nonBlanks, machine.totalTransitions );
        mp.addMessage( "Tape Cleared" );
  
        /* LOAD INPUT STRING */
        machine.loadInputString( inputs.get( i ).getText() );
        mp.addMessage( inputs.get( i ).getText().concat(
            " loaded onto tape (Input " + i + ")" ) );
        mp.updateLabels( machine.nonBlanks, machine.totalTransitions );
        
        /* RESET MACHINE */
        for( int x = 0; x < machine.states.size(); x++ ) {
          State n = (State)machine.states.elementAt( x );
          n.currentState = false;
          if( n.startState ) {
            n.currentState = true;
            machine.currentState = n;
          }
        }
        machine.currentEdge = null;
        for( int x = 0; x < machine.transitions.size(); x++ ) {
          Edge n = (Edge)machine.transitions.elementAt( x );
          n.currentEdge = false;
        }
        machine.totalTransitions = 0;
        mp.updateLabels( machine.nonBlanks, machine.totalTransitions );
        machine.leftMost = machine.tapePos;
        machine.rightMost = machine.tapePos;
        mp.addMessage( "Machine Reset" );
  
        /* RUN */
        mp.addMessage( "Running..." );
        execution = new Thread( machine );
        execution.start();
        mp.addMessage( "Machine Started" );
  
        while ( execution.isAlive() ) {} // problems
  
        /* PRINT OUTPUT TO OUTPUT TEXTFIELDS */
        outputs.get( i ).setText( machine.printTape() );
      }
    }
  }

  @SuppressWarnings( "deprecation" )
  private void stopAll() {
    mp.addMessage( "User interrupt\nMachine halted" );
    execution.stop();
    action.stop();
    this.setVisible( false );
    JOptionPane.showMessageDialog( this,
        "User interrupted batch inputs.",
        "Machine Halted",
        JOptionPane.WARNING_MESSAGE );
  }
}
