package TuringMachine;

import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import java.io.*;

class State implements Serializable {
  private static final long serialVersionUID = 7903724405884412505L;
  double x;
  double y;
  String stateName;

  boolean finalState;
  boolean currentState;
  boolean startState;

  boolean highlight = false;

  public State( double x, double y, String name, boolean f ) {
    this.x = x;
    this.y = y;
    finalState = f;
    currentState = false;
    startState = false;
    stateName = name;
  }
}

class Edge {
  State fromState;
  State toState;

  char oldChar = 0;
  char newChar = 0;
  int direction = 0;

  boolean currentEdge = false;
  boolean highlight = false;

  double shiftLabel = 0;

  public Edge( State from, State to ) {
    fromState = from;
    toState = to;
  }

  String label() {
    String temp = new String();
    if( oldChar != 0 ) temp = new String( String.valueOf( oldChar ) );
    if( newChar != 0 ) {
      temp = temp.concat( ", " );
      temp = temp.concat( String.valueOf( newChar ) );
    }
    if( direction != TM.NULL ) {
      temp = temp.concat( ", " );
      if( direction == TM.LEFT )
        temp = temp.concat( "Left" );
      else if( direction == TM.RIGHT )
        temp = temp.concat( "Right" );
      else temp = temp.concat( "Stay" );
    }
    return temp;
  }

  String listLabel() {
    String temp = new String( "(" );
    temp = temp.concat( fromState.stateName );
    temp = temp.concat( ", " );
    temp = temp.concat( String.valueOf( oldChar ) );
    temp = temp.concat( ")  " );
    temp = temp.concat( "(" );
    temp = temp.concat( toState.stateName );
    if( newChar != 0 ) {
      temp = temp.concat( ", " );
      temp = temp.concat( String.valueOf( newChar ) );
    }
    if( direction != TM.NULL ) {
      temp = temp.concat( ", " );
      if( direction == TM.LEFT )
        temp = temp.concat( "Left" );
      else if( direction == TM.RIGHT )
        temp = temp.concat( "Right" );
      else temp = temp.concat( "Stay" );
    }
    temp = temp.concat( ")" );
    return temp;
  }
}

class TM implements Runnable {
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
  int tapePos;
  int leftMost;
  int rightMost;
  int initPos;
  int initNonBlanks, nonBlanks;
  int totalTransitions;
  int moving;
  // references to exterior components
  State currentState;
  Edge currentEdge;
  @SuppressWarnings( "unchecked" )
  Vector states;
  DefaultListModel transitions;
  MessagePanel messages;
  TransitionsPane transitionpanel;

  // TapePanel display;

  public TM() {
    initMachine( TAPESIZE / 2, "", new StringBuffer( "" ) );
    // tapepanel.getViewport().setView(tape);
  }
  
  

  /*
   * public void setTape(TapePanel tape) { // display = tape; }
   */
  public void setTransitions( DefaultListModel transitions ) {
    this.transitions = transitions;
  }

  @SuppressWarnings( "unchecked" )
  public void setStates( Vector states ) {
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

  public void clearEdge() {
    for( int j = 0; j < transitions.size(); j++ ) {
      Edge n = (Edge)transitions.elementAt( j );
      n.currentEdge = false;
    }
    currentEdge = null;
  }

  public boolean validTapeChar( char ch ) {
    return( miscUtil.isLetterOrDigit( ch ) || " +/*-!@#$%&()=,.[]".indexOf( ch ) > -1 );
  }

  @SuppressWarnings( "unchecked" )
  public boolean initMachine( int initPos, String initChars,
      StringBuffer errorMsg ) {

    this.initPos = initPos;
    // int numChars = initChars.length();
    Vector tapeIndicator = new Vector();
    Vector tapeData = new Vector();
    for( int i = 0; i < TAPESIZE; i++ ) {
      tapeIndicator.add( new Character( '0' ) );
      tapeData.add( new Character( DEFAULTCHAR ) );
    }
    tapeIndicator.setElementAt( new Character( '-' ), initPos );
    Vector tapeData2 = new Vector();
    tapeData2.add( tapeData );
    tapemodel.setDataVector( tapeData2, tapeIndicator );
    tape = new TapeTable( tapemodel , this);
    tape.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
    tape.getTableHeader().setReorderingAllowed(false);
    //tape.setAutoCreateColumnsFromModel(false);
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

    tapePos = initPos;
    leftMost = initPos;
    rightMost = initPos;
    tape.scrollRectToVisible( tape.getCellRect( 0, tapePos - 5, true ) );
    tape.scrollRectToVisible( tape.getCellRect( 0, tapePos + 5, true ) );
    initNonBlanks = 0;
    /*
     * for (int i=0; i < numChars; i++) { c = initChars.charAt(i); if (c == '_')
     * c = ' '; if (!validTapeChar(c)) {
     * errorMsg.append("Invalid tape character '" + c + "'"); return false; }
     * tape.setValueAt(new Character(c), 2, initPos+i); if (c != ' ')
     * initNonBlanks++; }
     */
    nonBlanks = initNonBlanks;
    totalTransitions = 0;

    return true;
  }

  public void loadInputString( String input, int startPos ) {
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

  public void scrollTape( int dir ) {
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
    // display.repaint();
  }

  public String transition() {
    go = true;
    String temp = new String();
    if( currentState == null ) {
      if( states.size() > 0 )
        setState( (State)states.elementAt( 0 ) );
      else {
        go = false;
        // reachedHaltingState = false;
        return temp.concat( "Machine not created" );
      }
    }
    if( currentState.finalState ) {
      go = false;
      return temp.concat( "Machine halted" );
    }
    temp = new String( "In state " + currentState.stateName + ", "
        + tape.getValueAt( 0, tapePos ).toString() + " read on tape:\n\t" );
    Edge fromTemp;
    String currentCharTemp = String.valueOf( tape.getValueAt( 0, tapePos ) );
    char currentCharTemp2 = currentCharTemp.charAt( 0 );
    Character currentChar;
    currentChar = new Character( currentCharTemp2 );
    for( int i = 0; i < transitions.size(); i++ ) {
      fromTemp = (Edge)transitions.elementAt( i );
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
        scrollTape( fromTemp.direction );
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
        messages.updateLabels( nonBlanks, totalTransitions );
        moving = fromTemp.direction;
        nextStateNotSet = false;
        return temp;
      }
    }
    // reachedHaltingState=false;
    go = false;
    return temp.concat( "No applicable transition found\nMachine halted" );
  }
}

class miscUtil {

  public static boolean isLetterOrDigit( char ch ) { // for Java 1.0
    String list = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
        + "0123456789";
    return( list.indexOf( ch ) > -1 );
  }
}