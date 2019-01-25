package TuringMachine;

import java.util.*;

import javax.swing.*;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.*;

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

/*
 * class TapePanel extends JPanel implements Runnable, MouseListener { static
 * final int MINW = 200, MINH = 50, CELLHEIGHT = 20, CELLWIDTH = 20;
 * 
 * int[] leftTriXs, rightTriXs, triYs; Dimension minSize; Thread scroller = new
 * Thread(this); int scrollDir; int width, height, xpos, ypos, numCells; Font
 * tapeFont;
 * 
 * Image offscreen; Dimension offscreensize; Graphics offgraphics;
 * 
 * //references to exterior components TM machine; Thread execution;
 * 
 * public TapePanel() { addMouseListener(this); minSize = new Dimension(MINW,
 * MINH); tapeFont = new Font("Helvetica", Font.BOLD, 14); setSize(600, 50); }
 * 
 * public void setMachine(TM machine) { this.machine = machine; } public void
 * setExecution(Thread execution) { this.execution = execution; }
 * 
 * public void initGraphics() { Dimension layout = new Dimension(size().width,
 * size().height); setSize(layout); width = layout.width; height =
 * layout.height; ypos = height/2 - CELLHEIGHT/2; numCells = width/CELLWIDTH -
 * 2; xpos = (width - numCells*CELLWIDTH) / 2; int[] ltx = {18, 1, 18, 18};
 * leftTriXs = ltx; int[] rtx = {width-19, width-2, width-19, width-19};
 * rightTriXs = rtx; int[] ty = {ypos, height/2, ypos+CELLHEIGHT, ypos}; triYs =
 * ty; repaint(); }
 * 
 * public synchronized void paint(Graphics g) { Dimension d = getSize(); if
 * ((offscreen == null) || (d.width != offscreensize.width) || (d.height !=
 * offscreensize.height)) { offscreen = createImage(d.width, d.height);
 * offscreensize = d; offgraphics = offscreen.getGraphics();
 * offgraphics.setFont(getFont()); }
 * 
 * initGraphics(); offgraphics.setFont(tapeFont); offgraphics.drawRect(0, 0,
 * width-1, height-1); if (machine.moving == TM.LEFT)
 * offgraphics.setColor(Color.yellow); else offgraphics.setColor(Color.red);
 * offgraphics.fillPolygon(leftTriXs, triYs, 4); if (machine.moving == TM.RIGHT)
 * offgraphics.setColor(Color.yellow); else offgraphics.setColor(Color.red);
 * offgraphics.fillPolygon(rightTriXs, triYs, 4);
 * offgraphics.setColor(Color.black); offgraphics.drawPolygon(leftTriXs, triYs,
 * 4); offgraphics.drawPolygon(rightTriXs, triYs, 4);
 * 
 * offgraphics.setColor(Color.black); int actualNumCells = numCells; /* if
 * (!machine.programmed) { for (int x=xpos, count=0; count < numCells;
 * x+=CELLWIDTH, count++) { if (count == numCells/2) { g.setColor(Color.red);
 * g.drawRect(x, ypos, CELLWIDTH, CELLHEIGHT); g.drawRect(x-1, ypos-1,
 * CELLWIDTH+2, CELLHEIGHT+2); g.setColor(Color.black); } else g.drawRect(x,
 * ypos, CELLWIDTH, CELLHEIGHT); if (count == numCells/2 + 1) {
 * g.setColor(Color.red); g.drawLine(x, ypos, x, ypos+CELLHEIGHT);
 * g.setColor(Color.black); } } } // else // { Vector tape = machine.tape; int
 * tapePos = machine.tapePos; int centerCell = numCells/2; int centerCellPos =
 * xpos + CELLWIDTH*centerCell; int charsToLeft = numCells/2; int charsToRight =
 * (numCells%2 == 0) ? numCells/2-1: numCells/2; int firstCell, lastCell,
 * startx; if (tapePos < charsToLeft) { firstCell = 0; startx = xpos + CELLWIDTH
 * * (charsToLeft - tapePos); actualNumCells -= (charsToLeft - tapePos); } else
 * { firstCell = tapePos - charsToLeft; startx = xpos; } if (tapePos +
 * charsToRight >= TM.TAPESIZE) actualNumCells -= ((tapePos+charsToRight+1) -
 * TM.TAPESIZE); lastCell = firstCell + actualNumCells - 1; for (int x=startx,
 * tpos = firstCell; tpos <= lastCell; x+=CELLWIDTH, tpos++) { if (x ==
 * centerCellPos) { offgraphics.setColor(Color.red); offgraphics.drawRect(x,
 * ypos, CELLWIDTH, CELLHEIGHT); offgraphics.drawRect(x-1, ypos-1, CELLWIDTH+2,
 * CELLHEIGHT+2); offgraphics.setColor(Color.black); } else {
 * offgraphics.drawRect(x, ypos, CELLWIDTH, CELLHEIGHT); } if (x ==
 * centerCellPos + CELLWIDTH) { offgraphics.setColor(Color.red);
 * offgraphics.drawLine(x, ypos, x, ypos+CELLHEIGHT);
 * offgraphics.setColor(Color.black); } if
 * (((Character)tape.elementAt(tpos)).charValue() != ' ')
 * offgraphics.drawString("" + ((Character)tape.elementAt(tpos)).charValue(),
 * x+CELLWIDTH/2-4, ypos+CELLHEIGHT*3/4); } g.drawImage(offscreen, 0, 0, null);
 * // } }
 * 
 * public void mouseEntered(MouseEvent e){} public void mouseExited(MouseEvent
 * e){} public void mouseDragged(MouseEvent e){}
 * 
 * public void mouseClicked(MouseEvent e){} public void mousePressed(MouseEvent
 * e) { System.out.println("Yes"); if (!execution.isAlive() &&
 * !scroller.isAlive()) { if (e.getX() >= leftTriXs[1] && e.getX() <=
 * leftTriXs[0] && e.getY() >= triYs[0] && e.getY() <= triYs[2]) { scrollDir =
 * TM.LEFT; machine.scrollTape(scrollDir); machine.moving = scrollDir;
 * repaint(); scroller = new Thread(this); scroller.start(); } else if (e.getX()
 * >= rightTriXs[0] && e.getX() <= rightTriXs[1] && e.getY() >= triYs[0] &&
 * e.getY() <= triYs[2]) { scrollDir = TM.RIGHT; machine.scrollTape(scrollDir);
 * machine.moving = scrollDir; repaint(); scroller = new Thread(this);
 * scroller.start(); } } }
 * 
 * public void mouseReleased(MouseEvent e) { if (scroller.isAlive()) {
 * scroller.stop(); machine.moving = TM.STAY; repaint(); } }
 * 
 * public void run() { while (machine.moving != TM.STAY) { try {
 * Thread.sleep(100); } catch (InterruptedException e) {}
 * machine.scrollTape(scrollDir); repaint(); } machine.moving = TM.STAY;
 * repaint(); }
 * 
 * public void start() { scroller.start(); }
 * 
 * public Dimension getMinimumSize() { return new Dimension(MINW, MINH); }
 * 
 * public Dimension getPreferredSize() { return new Dimension(MINW, MINH); } }
 */

class TapeTableModel extends DefaultTableModel {
  /**
	 * 
	 */
  private static final long serialVersionUID = 1L;

  public TapeTableModel() {
    super();
  }

  public void setColumnName( Object value, int col ) {

    // columnIdentifiers.setElementAt(value, col);
  }
}

class TapeTable extends JTable
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



class TapePanel extends JScrollPane {
  /**
	 * 
	 */
  private static final long serialVersionUID = 1L;

  public TapePanel() {
    super();
  }

  public TapePanel( JTable tape ) {
    super( tape );
  }

  public Dimension getMinimumSize() {
    return new Dimension( 500, 75 );
  }

  public Dimension getPreferredSize() {
    return new Dimension( 500, 75 );
  }
}

class TapeHeaderRenderer extends JLabel implements TableCellRenderer {
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
          .getResource( "tapeindex.gif" ) ) );
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

class TapeCellRenderer extends DefaultTableCellRenderer {
	  /**
		 * 
		 */
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


class TapeEditor extends JTextField implements TableCellEditor
{
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

    // Retrieve e dited value
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
    public void removeCellEditorListener(CellEditorListener celleditorlistener)
    {
        if (cellEditorListener == cellEditorListener) cellEditorListener = null;
    }
}
