package TuringMachine;

import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JTable;
import javax.swing.table.TableColumn;

public class TM implements Runnable {
  // transition results
  public static final int SUCCESS = 0, HALTED = -1, NOTFOUND = -2,
      ABNORMAL = -3, NOPROG = -4, USERINT = -5;
  // speeds
  public static final int SLOW = 0, FAST = 1, VERYFAST = 2, COMPUTE = 3;
  // moving direction
  public static final int NULL = 0, LEFT = 1, RIGHT = 2, STAY = 3;
  // tapesize
  public static final int TAPESIZE = 1000;
  // tape cell size
  public static final int CELLSIZE = 15;
  public static final char DEFAULTCHAR = '0';
  public static final int QUADRUPLE = 4, QUINTUPLE = 5;

  public boolean go = true;

  // public boolean reachedHaltingState = false;

  boolean programmed = false;
  boolean nextStateNotSet = false;

  // interior components
  int speed;
  TapeTableModel tapemodel = new TapeTableModel();
  TapeTable tape;
  //current position of the read head
  int tapePos;
  //leftmost position on the tape that has a character other than the default character
  int leftMost;
  //same as above but for the rightmost position
  int rightMost;
  //starting position of the read head
  int initPos;
  int initNonBlanks, nonBlanks;
  int totalTransitions;
  int moving;
  // references to exterior components
  //the current state being looked at
  State currentState;
  //the current edge being looked at
  Edge currentEdge;
  //list of states
  Vector<State> states;
  //list of transitiona
  DefaultListModel<Edge> transitions;
  MessagePanel messages;
  TransitionsPane transitionpanel;
  int machineType;
  //constructor calls for the initialization of the machine
  public TM() {
    initMachine( TAPESIZE / 2, "", new StringBuffer( "" ) );
  }
  
  //basic setters
  public void setTransitions( DefaultListModel<Edge> transitions ) {
    this.transitions = transitions;
  }
  
  public void setStates( Vector<State> states ) {
    this.states = states;
  }

  public void setMessagePanel( MessagePanel messages ) {
    this.messages = messages;
  }

  public void setTransitionPanel( TransitionsPane transitionpanel ) {
    this.transitionpanel = transitionpanel;
  }

  public void setSpeed( String newSpeed ) {
    if( newSpeed.equals( "Slow" ) )
      speed = SLOW;
    else if( newSpeed.equals( "Fast" ) )
      speed = FAST;
    else if( newSpeed.equals( "Very Fast" ) )
      speed = VERYFAST;
    else speed = COMPUTE;
  }
  
  public void setState( State newState ) {
    if( currentState != null ) currentState.currentState = false;
    currentState = newState;
    currentState.currentState = true;
  }
 
  public void setEdge( Edge newEdge ) {
    if( currentEdge != null ) currentEdge.currentEdge = false;
    currentEdge = newEdge;
    currentEdge.currentEdge = true;
  }
  //sets currentEdge to null and making sure there are no transitions out of it
  public void clearEdge() {
    for( int j = 0; j < transitions.size(); j++ ) {
      Edge n = transitions.elementAt( j );
      n.currentEdge = false;
    }
    currentEdge = null;
  }
  //checks whether or not the inputted character is a valid tape character
  public boolean validTapeChar( char ch ) {
    return( Character.isLetterOrDigit( ch ) || " +/*-!@#$%&()=,.[]".indexOf( ch ) > -1 );
  }
  //sets up the machine
  public boolean initMachine( int initPos, String initChars,
      StringBuffer errorMsg ) {
    
    //settimg the initial position
    this.initPos = initPos;

    //setting up the tape
    Vector<Character> tapeIndicator = new Vector<Character>();
    Vector<Character> tapeData = new Vector<Character>();
    //clears the tape
    for( int i = 0; i < TAPESIZE; i++ ) {
      tapeIndicator.add( new Character( '0' ) );
      tapeData.add( new Character( DEFAULTCHAR ) );
    }
    tapeIndicator.setElementAt( new Character( '-' ), initPos );
    Vector<Vector<Character>> tapeData2 = new Vector<Vector<Character>>();
    tapeData2.add( tapeData );
    tapemodel.setDataVector( tapeData2, tapeIndicator );
    tape = new TapeTable( tapemodel , this);
    tape.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
    tape.getTableHeader().setReorderingAllowed(false);
    //sets up the visualization for the tape
    for( int j = 0; j < TAPESIZE; j++ ) {
      TableColumn col = tape.getColumnModel().getColumn( j );
      col.setResizable(false);
      col.setMinWidth( CELLSIZE );
      col.setMaxWidth( CELLSIZE );
      col.setPreferredWidth( CELLSIZE );
      col.setCellRenderer(new TapeCellRenderer() );
      col.setCellEditor(new TapeEditor());
      col.setHeaderRenderer( new TapeHeaderRenderer() );
    }
    //setting the initial tape values
    tapePos = initPos;
    leftMost = initPos;
    rightMost = initPos;

    //making the tape visible
    tape.scrollRectToVisible( tape.getCellRect( 0, tapePos - 5, true ) );
    tape.scrollRectToVisible( tape.getCellRect( 0, tapePos + 5, true ) );
    initNonBlanks = 0;

    nonBlanks = initNonBlanks;
    totalTransitions = 0;

    return true;
  }

  //loads the given input string into the tape at a given starting position
  //changing the dimensions of the tape to match the input
  public void loadInputString( String input, int startPos ) {
      //makes sure the tape is empty
	  for( int j = 0; j < tape.getColumnCount(); j++ ) {
          tape.getColumnModel().getColumn( j ).setHeaderValue(
        		  new Character( '0' ) );
        }
	  tape.getColumnModel().getColumn( tapePos + startPos ).setHeaderValue(
	          new Character( '-' ) );
	  
    for( int j = 0; j < input.length(); j++ ) {
      if( input.charAt( j ) != '0'
          && ( (Character)tape.getValueAt( 0, tapePos + j ) ).charValue() == '0' )
        nonBlanks++;
      if( input.charAt( j ) == '0'
          && ( (Character)tape.getValueAt( 0, tapePos + j ) ).charValue() != '0' )
        nonBlanks--;
      tape.setValueAt( new Character( input.charAt( j ) ), 0, tapePos + j );
    }
    leftMost = tapePos;
    rightMost = tapePos + input.length() - 1;
    tapePos += startPos;
    tape.getTableHeader().repaint();
  }

  // Returns a string representing what is on the tape (not including
  // infinite 0's on either end)
  public String printTape() {
    String output = new String();
    String temp;
    for( int j = 0; j < ( rightMost - leftMost + 1 ); j++ ) {
      temp = output;
      if((leftMost + j) == tapePos)
      {
	      output = temp + '['
	          + ( (Character)tape.getValueAt( 0, leftMost + j ) ).charValue() + ']';
      }
      else
      {
	      output = temp
		          + ( (Character)tape.getValueAt( 0, leftMost + j ) ).charValue();
      }
    }
    return output;
  }

  //scrolls the tape when the cursor goes to move off of the visible tape
  public void scrollTape( int dir ) {
    //case for moving left
    if( dir == LEFT ) {
      if( tapePos <= 20 ) {
        for( int j = 0; j < TAPESIZE; j++ ) {
          tapemodel.addColumn('0', new Object[]{'0'});
          tape.moveColumn( tape.getColumnCount() - 1, 0 );
        }
        tapePos += TAPESIZE;
        leftMost += TAPESIZE;
        rightMost += TAPESIZE;
      }
      tape.getColumnModel().getColumn( tapePos ).setHeaderValue(
          new Character( '0' ) );
      tapePos--;
      tape.getColumnModel().getColumn( tapePos ).setHeaderValue(
          new Character( '-' ) );
      tape.getTableHeader().repaint();
      tape.scrollRectToVisible( tape.getCellRect( 0, tapePos - 5, true ) );
      tape.scrollRectToVisible( tape.getCellRect( 0, tapePos + 5, true ) );
      if( tapePos < leftMost ) leftMost = tapePos;
    }
    //case for moving right
    else if( dir == RIGHT ) {
      if( tapePos >= tape.getColumnCount() - 20 ) {
        for( int j = 0; j < TAPESIZE; j++ ) {
          tapemodel.addColumn('0', new Object[]{'0'});
        }
      }
      tape.getColumnModel().getColumn( tapePos ).setHeaderValue(
          new Character( '0' ) );
      tapePos++;
      tape.getColumnModel().getColumn( tapePos ).setHeaderValue(
          new Character( '-' ) );
      tape.getTableHeader().repaint();
      tape.scrollRectToVisible( tape.getCellRect( 0, tapePos - 5, true ) );
      tape.scrollRectToVisible( tape.getCellRect( 0, tapePos + 5, true ) );
      if( tapePos > rightMost ) rightMost = tapePos;
    }
  }

  //runs the turing machine
  public void run() {
    // reachedHaltingState = true;
    go = true;
    tape.setEnabled(false);
    while ( go ) {
      if( speed == SLOW )
        try {
          Thread.sleep( 500 );
        }
        catch ( InterruptedException e ) {
        }
      else if( speed == FAST )
        try {
          Thread.sleep( 100 );
        }
        catch ( InterruptedException e ) {
        }
      else if( speed == VERYFAST ) try {
        Thread.sleep( 25 );
      }
      catch ( InterruptedException e ) {
      }
      messages.addMessage( transition() );
    }
    clearEdge();
    moving = STAY;
    tape.setEnabled(true);
  }

  //transitions from one state to another
  public String transition() {
    go = true;
   //temp is a string that holds the message that details the transition preformed
   String temp = new String();
    //case for an empty machine
    if( currentState == null ) {
      if( states.size() > 0 )
        setState( states.elementAt( 0 ) );
      else {
        go = false;
        // reachedHaltingState = false;
        return temp.concat( "Machine not created" );
      }
    }
    //explicit halting case
    if( currentState.finalState ) {
      go = false;
      return temp.concat( "Machine halted" );
    }
    //case where there is a valid transition
    //reads out the current state
    temp = new String( "In state " + currentState.stateName + ", "
        + tape.getValueAt( 0, tapePos ).toString() + " read on tape:\n\t" );
    Edge fromTemp;
    String currentCharTemp = String.valueOf( tape.getValueAt( 0, tapePos ) );
    char currentCharTemp2 = currentCharTemp.charAt( 0 );
    Character currentChar;
    currentChar = new Character( currentCharTemp2 );
    //loop looks for the applicable transition
    for( int i = 0; i < transitions.size(); i++ ) {
      fromTemp = transitions.elementAt( i );
      //if it finds a matching transition it performs it and adds the description
      //to the return message
      if( fromTemp.fromState == currentState
          && fromTemp.oldChar == currentChar.charValue() ) {
        temp = temp
            .concat( "Transition to state " + fromTemp.toState.stateName );
        if( fromTemp.newChar != 0 ) {
          if( fromTemp.newChar == '0' && currentChar.charValue() != '0' )
            nonBlanks--;
          else if( fromTemp.newChar != '0' && currentChar.charValue() == '0' )
            nonBlanks++;
          tape.setValueAt( new Character( fromTemp.newChar ), 0, tapePos );
          temp = temp.concat( ", " + String.valueOf( fromTemp.newChar )
              + " written" );
        }
        //if the tape needs to be scrolled
        scrollTape( fromTemp.direction );
        //if the read head is being moved
        if( fromTemp.direction != NULL ) {
          temp = temp.concat( ", " );
          switch ( fromTemp.direction ) {
          case RIGHT:
            temp = temp.concat( "Move read head Right" );
            break;
          case LEFT:
            temp = temp.concat( "Move read head Left" );
            break;
          }
        }
        setEdge( fromTemp );
        nextStateNotSet = true;
        transitionpanel.getViewport().repaint();
        //performs the action at the speed set when run is called
        if( speed == SLOW )
          try {
            Thread.sleep( 500 );
          }
          catch ( InterruptedException e ) {
          }
        else if( speed == FAST )
          try {
            Thread.sleep( 100 );
          }
          catch ( InterruptedException e ) {
          }
        else if( speed == VERYFAST ) try {
          Thread.sleep( 25 );
        }
        catch ( InterruptedException e ) {
        }
        setState( fromTemp.toState );
        totalTransitions++;
        messages.updateLabels( nonBlanks, totalTransitions,states.size(), transitions.size());

        moving = fromTemp.direction;
        nextStateNotSet = false;
        return temp;
      }
    }
    //implicit halting case
    // reachedHaltingState=false;
    go = false;
    return temp.concat( "No applicable transition found\nMachine halted" );
  }
}

