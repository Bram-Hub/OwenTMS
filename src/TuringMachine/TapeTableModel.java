package TuringMachine;

import javax.swing.table.DefaultTableModel;

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


public class TapeTableModel extends DefaultTableModel {
  /**
	 * 
	 */
  private static final long serialVersionUID = 1L;

  public TapeTableModel() {
    super();
  }
  
  @Override
  public Class<?> getColumnClass(int columnIndex) {
	  return Character.class;
  }

  public void setColumnName( Object value, int col ) {

    // columnIdentifiers.setElementAt(value, col);
  }
}

