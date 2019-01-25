package TuringMachine;

import java.awt.*;
import javax.swing.*;

/**
 * ScrollPane to hold the Transition list
 * 
 * @author Owen F. Kellett
 * @version 1.0
 */
public class TransitionsPane extends JScrollPane {
  /**
	 * 
	 */
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

/**
 * Cell renderer for the TransitionsPane
 * 
 * @author Owen F. Kellett
 * @version 1.0
 */
class TransitionCellRenderer extends JLabel implements ListCellRenderer {
  /**
	 * 
	 */
  private static final long serialVersionUID = -2657134010283269621L;

  public TransitionCellRenderer() {
    setOpaque( true );
  }

  public Component getListCellRendererComponent( JList list, Object value,
      int index, boolean isSelected, boolean cellHasFocus ) {
    setText( ( (Edge)value ).listLabel() );
    if( ( (Edge)value ).currentEdge ) {
      setBackground( list.getSelectionBackground() );
      setForeground( list.getSelectionForeground() );
    }
    else {
      setBackground( list.getBackground() );
      setForeground( list.getForeground() );
    }

    return this;
  }
}

/**
 * List Model for the TransitionsPane - Maintains list of edges sorted by
 * starting state
 * 
 * @author Owen F. Kellett
 * @version 1.0
 */
class SortedListModel extends DefaultListModel {
  /**
	 * 
	 */
  private static final long serialVersionUID = 7273332800366200363L;

  public SortedListModel() {
    super();
  }

  /**
   * Adds an edge into the list in the appropriate sorted location according to
   * its starting state
   * 
   * @param obj
   */
  public void addSortedElement( Object obj ) {
    int j = 0;
    for( j = 0; j < size(); j++ ) {
      if( ( (Edge)getElementAt( j ) ).fromState.stateName
          .compareTo( ( (Edge)obj ).fromState.stateName ) < 0 )
        ;
      else if( ( (Edge)getElementAt( j ) ).fromState.stateName
          .compareTo( ( (Edge)obj ).fromState.stateName ) == 0 ) {
        if( ( (Edge)getElementAt( j ) ).oldChar < ( (Edge)obj ).oldChar )
          ;
        else {
          add( j, obj );
          break;
        }
      }
      else {
        add( j, obj );
        break;
      }
    }
    if( j > 0 && j == size() ) super.addElement( obj );
    if( size() == 0 ) super.addElement( obj );
  }
}
