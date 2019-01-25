package TuringMachine;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

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


public class TapeTable extends JTable
{
	private static final long serialVersionUID = 1L;
	TM machine = null;
	
	TapeTable()
	{
		super();
	}
	
	TapeTable(TableModel dm, TM machine1)
	{
		super(dm);
		machine = machine1;
	}
	
	public void setValueAt(Object value, int row, int column)
	{
		if(value instanceof String)
		{
			String a = (String) value;
			if(a.charAt(a.length()-1) == '^')
			{
				for( int j = 0; j < getColumnCount(); j++ ) {
			          getColumnModel().getColumn( j ).setHeaderValue(
			        		  new Character( '0' ) );
			    }
				super.getColumnModel().getColumn(column).setHeaderValue(new Character( '-' ) );
				machine.tapePos = column;
				getTableHeader().repaint();
				super.setValueAt(super.getValueAt(row, column), row, column);
			}
			else if(a.length() > 0 &&
					"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789 /*-!@#$%&()=,.".indexOf(a.charAt(a.length()-1)) != -1)
			{
				super.setValueAt(a.charAt(a.length()-1), row, column);
			}
			else
				super.setValueAt('0', row, column);
		}
		else
		{
			super.setValueAt(value, row, column);
		}
	}
	
	public void createDefaultColumnsFromModel()
   {
	     assert columnModel != null : "The columnModel must not be null.";
	
	     // remove existing columns
	     int columnIndex = columnModel.getColumnCount() - 1;
	   
	     // add new columns to match the TableModel
	     int columnCount = dataModel.getColumnCount();
	     for (int c = columnIndex+1; c < columnCount; c++)
	     {
	       TableColumn column = new TableColumn(c);
	       column.setResizable(false);
	       column.setMinWidth( TM.CELLSIZE );
	       column.setMaxWidth( TM.CELLSIZE );
	       column.setPreferredWidth( TM.CELLSIZE );
	       column.setCellRenderer(new TapeCellRenderer() );
	       column.setCellEditor(new TapeEditor());
	       column.setHeaderRenderer( new TapeHeaderRenderer() );
	       column.setIdentifier(dataModel.getColumnName(c));
	       column.setHeaderValue(dataModel.getColumnName(c));
	       columnModel.addColumn(column);
	       column.addPropertyChangeListener(new TableColumnPropertyChangeHandler());
	     }
   }
	
	class TableColumnPropertyChangeHandler implements PropertyChangeListener
	   {
	     /**
	      * Receives notification that a property of the observed TableColumns has
	      * changed.
	      * 
	      * @param ev the property change event
	      */
	     public void propertyChange(PropertyChangeEvent ev)
	    {
	       if (ev.getPropertyName().equals("preferredWidth"))
	         {
	           JTableHeader header = getTableHeader();
	           if (header != null)
	             // Do nothing if the table is in the resizing mode.
	             if (header.getResizingColumn() == null)
	               {
	                 TableColumn col = (TableColumn) ev.getSource();
	                 header.setResizingColumn(col);
	                 doLayout();
	                 header.setResizingColumn(null);
	               }
	         }
	     }
	   }
	
}



