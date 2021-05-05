package TuringMachine;


public class Edge {
  State fromState;
  State toState;

  char oldChar = 0;
  char newChar = 0;

  //0 for null, 1 for left, 2 for right, and 3 for do nothing
  int direction = 0;

  boolean currentEdge = false;
  boolean highlight = false;

  double shiftLabel = 0;

  public Edge( State from, State to ) {
    fromState = from;
    toState = to;
  }

  //label displayed for the edge
  String label() {
    //returns the string to be displayed
    String temp = new String();
    //adds the description of the change in character
    if( oldChar != 0 ) temp = new String( String.valueOf( oldChar ) );
    if( newChar != 0 ) {
      temp = temp.concat( ", " );
      temp = temp.concat( String.valueOf( newChar ) );
    }
    //sets the text label for the direction
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
    //lists the state it is moving fron, then the old character
    //then the state it is moving to
    String temp = new String( "(" );
    temp = temp.concat( fromState.stateName );
    temp = temp.concat( ", " );
    temp = temp.concat( String.valueOf( oldChar ) );
    temp = temp.concat( ")  " );
    temp = temp.concat( "(" );
    temp = temp.concat( toState.stateName );
    //if the character is being changed
    if( newChar != 0 ) {
      temp = temp.concat( ", " );
      temp = temp.concat( String.valueOf( newChar ) );
    }
    //adds the description of how the read head is moved
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

