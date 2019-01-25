package TuringMachine;

import java.awt.Component;
import java.util.EventObject;

import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableCellEditor;

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


public class TapeEditor extends JTextField implements TableCellEditor
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 4124384501187126231L;
	private CellEditorListener cellEditorListener  = null;
    private String old;

    // Start editing
    @Override
    public Component getTableCellEditorComponent(JTable table, Object obj, boolean isSelected, int row, int column)
    {
    	old = obj.toString();
        super.setText("");
        setBackground( table.getBackground() );
        setForeground( table.getForeground() );
          
        return this;
    }

    // Retrieve edited value
    @Override
    public Object getCellEditorValue()
    {
    	if(super.getText().equals(""))
    	{
    		return old;
    	}
        return super.getText();
    }

    @Override
    public boolean isCellEditable(EventObject e)
    {
        return true;
    }

    @Override
    public boolean shouldSelectCell(EventObject e)
    {
        return true;
    }

    @Override
    public boolean stopCellEditing()
    {
        cellEditorListener.editingStopped(new ChangeEvent(this));
        return true;
    }

    @Override
    public void cancelCellEditing()
    {
        cellEditorListener.editingCanceled(new ChangeEvent(this));
    }

    @Override
    public void addCellEditorListener(CellEditorListener celleditorlistener)
    {
        cellEditorListener = celleditorlistener;
    }

    @Override
    public void removeCellEditorListener(CellEditorListener cellEditorListener)
    {
        if (this.cellEditorListener == cellEditorListener) cellEditorListener = null;
    }
}
