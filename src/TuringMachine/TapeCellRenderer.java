package TuringMachine;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Cell renderer for the TransitionsPane
 * 
 * @author Owen F. Kellett
 * @version 1.0
 */


public class TapeCellRenderer extends DefaultTableCellRenderer {

	  private static final long serialVersionUID = 1L;

	  public TapeCellRenderer() {
	    setOpaque( true );
	  }

	  public Component getTableCellRendererComponent( JTable table, Object value,
	      boolean isSelected, boolean hasFocus, int row, int column ) {
	    super.getTableCellRendererComponent(table, value, false, hasFocus, row, column);
	    this.setToolTipText("Enter ^ to set the pointer location");
	    setHorizontalAlignment(SwingConstants.CENTER);
	    return this;
	  }
	}


