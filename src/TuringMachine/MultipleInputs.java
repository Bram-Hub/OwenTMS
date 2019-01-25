package TuringMachine;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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
  private Vector<JTextField> outputs;

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
    this.setLayout( new GridLayout( n + 2, 3, 10, 10 ) );
    
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
    outputs = new Vector<JTextField>();
    for( int i = 0; i < n; ++i ) {
      inputs.add( new JTextField() );
      JTextField temp2 = new JTextField("0") ;
      temp2.setHorizontalAlignment(JTextField.CENTER);
      JTextField temp = new JTextField();
      temp.setEditable(false);
      temp.setBorder(null);
      temp.setBackground( Color.WHITE );
      temp.setOpaque( true );
      temp.setHorizontalAlignment( JTextField.CENTER );
      temp.setCaretPosition(0);
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

    addWindowListener(new WindowAdapter()
    {
       public void windowClosing(WindowEvent e)
       {
    	 machine.tape.setEnabled(true);
         stopAll();
       }
    });
    
    run.addActionListener( new ActionListener() {
      public void actionPerformed( ActionEvent e ) {
        action = new Thread( new Runner() );
        action.start();
      }
    } );

    stop.addActionListener( new ActionListener() {
      public void actionPerformed( ActionEvent e ) {
    	machine.tape.setEnabled(true);
        stopAll();
      }
    } );
  }

  private class Runner implements Runnable {
    
    @SuppressWarnings("deprecation")
	public void run() {
    	Vector<String> inputstrings = new Vector<String>();
    	Vector<Integer> starts = new Vector<Integer>();
    	for( int i = 0; i < nPuts; i++ ) {
    		inputstrings.add(inputs.get( i ).getText());
    		starts.add(-1);
    	}
    	/* VALIDATE INPUT */
    	if(!validInput(inputstrings, starts))
    	{
    		JOptionPane.showMessageDialog( null,
    		        "One or more of the input are improperly formatted.",
    		        "Invalid Parameters",
    		        JOptionPane.WARNING_MESSAGE );
    		action.stop();
    	}
      /* SET MACHINE SPEED */
      machine.setSpeed( "Compute" );
      for( int i = 0; i < nPuts; i++ ) {
        if( inputstrings.get( i ).equals( "" ) ) continue;
        
        /* CLEAR TAPE */
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
        mp.updateLabels( machine.nonBlanks, machine.totalTransitions );
	
       mp.addMessage( "Tape Cleared" );
        /* LOAD INPUT STRING */
        machine.loadInputString( inputstrings.get( i ), starts.get(i)); 
        mp.addMessage( inputstrings.get( i ).concat(
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
       // machine.leftMost = machine.tapePos;
        //machine.rightMost = machine.tapePos;
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
    
    public boolean validInput(Vector<String >inputstrings, Vector<Integer> starts)
    {
    	for(int j = 0; j < inputstrings.size(); j++)
    	{
	  	  int left = 0;
	  	  int leftpos = -1;
	  	  int right = 0;
	  	  int rightpos = -1;
	  	  String temp1;
	  	  for(int i = 0; i < inputstrings.elementAt(j).length(); i++)
	  	  {
	  		  if(!machine.validTapeChar(inputstrings.elementAt(j).charAt(i)))
	  		  {
	  			  return false;
	  		  }
	  		  if(inputstrings.elementAt(j).charAt(i) == '[')
	  		  {
	  			  left++;
	  			  if(leftpos < 0)
	  				  leftpos = i;
	  		  }
	  		  if(inputstrings.elementAt(j).charAt(i) == ']')
	  		  {
	  			  right++;
	  			  if(rightpos < 0)
	  				  rightpos = i;
	  		  }
	  	  }
	  	  if(!((left == 0 && right == 0) || (left == 1 && right == 1)))
	  	  {
	  		  return false;
	  	  }
	  	  if(left == 0)
	  		starts.set(j, 0);
	  	  else
	  	  {
	  		  if(rightpos - leftpos != 2)
	  		  {
	  			  return false;
	  		  }
	  		  starts.set(j, leftpos);
	  		  temp1 = inputstrings.elementAt(j).substring(0,leftpos)
	  				  + inputstrings.elementAt(j).substring(leftpos+1, rightpos)
	  				  + inputstrings.elementAt(j).substring(rightpos+1);
	  		  inputstrings.set(j, temp1);
	  	  }
    	}
    	return true;
    }
}

  @SuppressWarnings( "deprecation" )
  private void stopAll() {
    execution.stop();
    this.setVisible( false );
    if(null != action)
    {
    	mp.addMessage( "User interrupt\nMachine halted" );
    	action.stop();
	    if(action.isAlive())
	    JOptionPane.showMessageDialog( this,
	        "User interrupted batch inputs.",
	        "Machine Halted",
	        JOptionPane.WARNING_MESSAGE );
    }
  }
}
