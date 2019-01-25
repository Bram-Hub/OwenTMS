package TuringMachine;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * ScrollPane to hold the Transition list
 * 
 * @author Owen F. Kellett
 * @version 1.0
 */
public class TransitionCellRenderer extends JLabel implements ListCellRenderer<Edge> {
  /**
	 * 
	 */
  private static final long serialVersionUID = -2657134010283269621L;

  public TransitionCellRenderer() {
    setOpaque( true );
  }
  
  public Component getListCellRendererComponent( JList<? extends Edge> list, Edge e,
      int index, boolean isSelected, boolean cellHasFocus ) {
    setText( e.listLabel() );
    if( e.currentEdge ) {
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


