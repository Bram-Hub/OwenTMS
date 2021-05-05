package TuringMachine;

import java.io.Serializable;
//class for states the machine can be in
public class State implements Serializable {
  private static final long serialVersionUID = 7903724405884412505L;
  //x and y coordinates of the position of the state
  double x;
  double y;
  String stateName;

  //whether or not it is a halting state
  boolean finalState;
  //whether or not the read head is currently looking at it
  boolean currentState;
  //whether or not it is the first state to be executed
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

