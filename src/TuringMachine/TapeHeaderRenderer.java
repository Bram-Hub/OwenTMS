package TuringMachine;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2002
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author unascribed
 * @version 1.0
 */


public class TapeHeaderRenderer extends JLabel implements TableCellRenderer {
  /**
	 * 
	 */
  private static final long serialVersionUID = 1L;

  public TapeHeaderRenderer() {
    setOpaque( true );
  }

  public Component getTableCellRendererComponent( JTable table, Object value,
      boolean isSelected, boolean hasFocus, int row, int column ) {
    if( value.toString().equals( "-" ) )
      setIcon( new ImageIcon( TuringMachine.TuringMachineFrame.class
          .getResource( "/resources/tapeindex.gif" ) ) );
    else setIcon( null );
    
    setHorizontalAlignment(SwingConstants.CENTER);
    
    if( isSelected ) {
      setBackground( table.getSelectionBackground() );
      setForeground( table.getSelectionForeground() );
    }
    else {
      setBackground( table.getBackground() );
      setForeground( table.getForeground() );
    }

    return this;
  }
}

