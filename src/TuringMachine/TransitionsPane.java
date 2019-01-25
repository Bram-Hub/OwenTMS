package TuringMachine;

import java.awt.Dimension;

import javax.swing.JScrollPane;

/**
 * ScrollPane to hold the Transition list
 * 
 * @author Owen F. Kellett
 * @version 1.0
 */
public class TransitionsPane extends JScrollPane {
  private static final long serialVersionUID = 5285771421231264288L;

  /**
   * Default constructor
   */
  public TransitionsPane() {
    super();
  }

  /**
   * Overloaded function from JScrollPane
   * 
   * @return - Minimum dimension of 120 x 500 pixels
   */
  public Dimension getMinimumSize() {
    return new Dimension( 120, 500 );
  }

  /**
   * Overloaded function from JScrollPane
   * 
   * @return - Preferred dimension of 120 x 500 pixels
   */
  public Dimension getPreferredSize() {
    return new Dimension( 120, 500 );
  }
}


