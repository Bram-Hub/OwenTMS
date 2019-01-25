package TuringMachine;

import javax.swing.DefaultListModel;

/**
 * ScrollPane to hold the Transition list
 * 
 * @author Owen F. Kellett
 * @version 1.0
 */

/**
 * List Model for the TransitionsPane - Maintains list of edges sorted by
 * starting state
 * 
 * @author Owen F. Kellett
 * @version 1.0
 */
public class SortedListModel extends DefaultListModel<Edge> {
  private static final long serialVersionUID = 7273332800366200363L;

  public SortedListModel() {
    super();
  }

  /**
   * Adds an edge into the list in the appropriate sorted location according to
   * its starting state
   * 
   * @param e
   */
  public void addSortedElement( Edge e ) {
    int j = 0;
    for( j = 0; j < size(); j++ ) {
      if((getElementAt(j)).fromState.stateName
          .compareTo((e).fromState.stateName)< 0)
        ;
      else if( ( getElementAt( j ) ).fromState.stateName
          .compareTo( ( e ).fromState.stateName ) == 0 ) {
        if( ( getElementAt( j ) ).oldChar < ( e ).oldChar )
          ;
        else {
          add( j, e );
          break;
        }
      }
      else {
        add( j, e );
        break;
      }
    }
    if( j > 0 && j == size() ) super.addElement( e );
    if( isEmpty() ) super.addElement( e );
  }

  public void update() {
      fireContentsChanged(this,0,size()-1);
  }
}
