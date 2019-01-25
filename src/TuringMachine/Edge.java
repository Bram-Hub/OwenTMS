package TuringMachine;


public class Edge {
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

