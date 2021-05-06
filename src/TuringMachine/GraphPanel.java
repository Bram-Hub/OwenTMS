package TuringMachine;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import TuringMachine.MultipleInputs.Runner;

/**
 * Title: Description: Copyright: Copyright (c) 2002 Company:
 * 
 * @author unascribed
 * @version 1.0
 */

//Comparator for State objects
class StateComparator implements Comparator<State> {
	public int compare(State s1, State s2) {
		return s1.stateName.compareTo(s2.stateName);
	}	
}

public class GraphPanel extends JPanel implements Runnable, MouseListener,
    MouseMotionListener, MouseWheelListener, ChangeListener, KeyListener {

  private static final long serialVersionUID = -4396915196603152278L;
  // interior components
  Vector<State> states = new Vector<State>( 100 );
  Set<State> selectedStates = new HashSet<State>();
  ArrayList<State> clipStates = new ArrayList<State>();
  ArrayList<Edge> clipEdges = new ArrayList<Edge>();
  boolean multiSelect = false;
  boolean draggingNewState = false;
  SortedListModel transitions = new SortedListModel();
  State tempState1, tempState2;
  Edge tempEdge, pickEdge;
  Image offscreen;
  Dimension offscreensize;
  Graphics offgraphics;
  int nextStateName = 0;

  Thread go;

  // references to exterior components
  GraphToolBar graphtoolbar;
  MessagePanel messagepanel;
  TransitionsPane transitionpanel;
  TM machine;
  JSlider zoomSlider;

  private int zoomLevel = 0;
  private int minZoomLevel = -10;
  private int maxZoomLevel = 10;
  private double zoomMultiplicationFactor = 1.1;

  private Point lastMouseLocation;
  private Point dragStartScreen;
  private Point dragEndScreen;

  private int h = 26;
  private int w = 26;
  
  //Initial shortcut keys
  public static char copyKey = 'c';
  public static char pasteKey = 'v';
  public static char saveKey = 's';
  public static char openKey = 'o';
  private boolean ctrlHeld = false;
  
  public GraphPanel( GraphToolBar graphtoolbar ) {
    addMouseListener( this );
    addMouseMotionListener( this );
    addMouseWheelListener( this );
    addKeyListener( this );
    this.graphtoolbar = graphtoolbar;
  }
 

  public void setMessagePanel( MessagePanel messagepanel ) {
    this.messagepanel = messagepanel;
  }

  public void setTransitionPanel( TransitionsPane transitionpanel ) {
    this.transitionpanel = transitionpanel;
  }

  public void setMachine( TM machine ) {
    this.machine = machine;
  }
  
  public void setSlider( JSlider slider ) {
    this.zoomSlider = slider;    
  }
  
  public void run() {
    Thread me = Thread.currentThread();
    while ( me == go ) {
      graphtoolbar.highlightSelectionMode();
      repaint();
      if(KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner() == null) {
          requestFocusInWindow();
      } 
      try {
        Thread.sleep( 100 );
      }
      catch ( InterruptedException e ) {
        break;
      }
    }
  }

  void addState( double x, double y, String name ) {
    State s = new State(x, y, name, false);

    if(states.size() == 0) {
        s.currentState = true;
        s.startState = true;
    }

    states.addElement( s );
  }

  void addEdge( State from, State to ) {
    transitions.addElement( new Edge( from, to ) );
  }

  final Color stateColor = Color.white;
  final Color selectedStateColor = Color.orange;
  final Color currentStateColor = Color.green;
  final Color highlightStateColor = Color.magenta;

  final Color edgeColor = Color.black;
  final Color selectedEdgeColor = Color.red;
  final Color currentEdgeColor = Color.green;

  public void paintNode( Graphics g1, State n, FontMetrics fm ) {
    Graphics2D g = (Graphics2D)g1;
    g.setRenderingHint( RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON );
    g.setRenderingHint( RenderingHints.KEY_STROKE_CONTROL,
        RenderingHints.VALUE_STROKE_PURE );

    int x = (int)n.x;
    int y = (int)n.y;
    if( selectedStates.contains(n) )
      g.setColor( selectedStateColor );
    else if( n.currentState )
      g.setColor( currentStateColor );
    else g.setColor( stateColor );
    if( n.highlight ) g.setColor( highlightStateColor );

    int stringWidth = fm.stringWidth( n.stateName );
    int w2 = w;
    if( stringWidth > w )
      w2 += stringWidth;
    //h = w;
    Ellipse2D state = new Ellipse2D.Double( x - w2 / 2, y - h / 2, w2, h );
    g.fill( state );
    g.setColor( Color.black );
    g.setStroke( new BasicStroke( 1 ) );
    g.draw( state );
    g.drawString( n.stateName, (int)state.getCenterX() - stringWidth / 2,
        (int)state.getCenterY() + fm.getAscent() / 2 );

    if( n.finalState )
      g.drawOval( x - w2 / 2 + 3, y - h / 2 + 3, w2 - 6, h - 6 );
    if( n.startState ) {
      g.setColor( Color.yellow );
      int xs[], ys[];
      xs = new int[] { x - w2 / 2, x - w2 / 2 - 10, x - w2 / 2 - 10 };
      ys = new int[] { y, y - h / 3, y + h / 3 };
      g.fillPolygon( xs, ys, 3 );
      g.setColor( Color.black );
      g.drawPolygon( xs, ys, 3 );
    }
  }

  public void paintEdge( Graphics g1, Edge e, FontMetrics fm ) {
    Graphics2D g = (Graphics2D)g1;
    g.setRenderingHint( RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON );
    g.setRenderingHint( RenderingHints.KEY_STROKE_CONTROL,
        RenderingHints.VALUE_STROKE_PURE );
    if( Math.abs( e.shiftLabel ) > 50 )
      e.shiftLabel = 0;
    int x1 = (int)e.fromState.x;
    int y1 = (int)e.fromState.y;
    int xtemp2 = (int)e.toState.x;
    int x2 = xtemp2;
    int ytemp2 = (int)e.toState.y;
    int y2 = ytemp2;
    String label = e.label();
    int w = fm.stringWidth( label ) + 5;
    int h = fm.getHeight();
    int len = (int)Math.abs( Math.sqrt( ( x1 - xtemp2 ) * ( x1 - xtemp2 )
        + ( y1 - ytemp2 ) * ( y1 - ytemp2 ) ) );
    if( e.currentEdge )
      g.setColor( currentEdgeColor );
    else g.setColor( edgeColor );

    if( len == 0 ) {
      int j = 0;
      int total = 0;
      for( j = 0; j < transitions.size(); j++ ) {
        Edge temp = transitions.elementAt( j );
        if( temp == e ) break;
        if( temp.fromState == e.fromState && temp.fromState == temp.toState )
          total += h + 2;
      }
      if( total == 0 ) g.drawOval( x1 - 10, y1 - 50, 20, 50 );
      g.setColor( Color.yellow );
      if( e.highlight ) g.setColor( selectedEdgeColor );
      if( e.currentEdge ) g.setColor( currentEdgeColor );
      g.fillRect( x1 - w / 2, y1 - 50 - h / 2 - total, w, h );
      g.setColor( Color.black );
      g.drawRect( x1 - w / 2, y1 - 50 - h / 2 - total, w, h );
      g.drawString( label, x1 - ( w - 5 ) / 2, ( y1 - 50 - ( h ) / 2 ) - total
          + fm.getAscent() );
    }
    else {
      int j = 0;
      boolean line = true;
      for( j = 0; j < transitions.size(); j++ ) {
        Edge temp = transitions.elementAt( j );
        if( temp == e ) break;
        if(  temp.fromState == e.fromState && temp.toState == e.toState  )
          line = false;
      }
     if( line ) {
      QuadCurve2D q = new QuadCurve2D.Float();
      
      
      q.setCurve(x1, y1, (x1+xtemp2)/2 - (y1-ytemp2)/Math.sqrt(1+len/10), (y1+ytemp2)/2 + (x1-xtemp2)/Math.sqrt(1+len/10), xtemp2, ytemp2);
      g.draw(q); }
      
     int curvx = (int) ((x1+x2)/2 - (y1-y2)/Math.sqrt(1+len/10));
     int curvy = (int) ((y1+y2)/2 + (x1-x2)/Math.sqrt(1+len/10));
     int avex = curvePoint(x1, curvx, x2, (e.shiftLabel+50)/100);
     int avey = curvePoint(y1, curvy, y2, (e.shiftLabel+50)/100);
      
      g.setColor( Color.yellow );
      if( e.highlight ) g.setColor( selectedEdgeColor );
      if( e.currentEdge ) g.setColor( currentEdgeColor );
      
      g.fillRect( avex - w / 2, avey  - h / 2, w, h );     
      g.setColor( Color.black );
      g.drawRect( avex - w / 2, avey - h / 2,
          w, h );
      g.drawString( label, avex  - ( w - 5 ) / 2, avey - h / 2 + fm.getAscent() );

      int xs[], ys[];
      if( x1 > x2 ) {
        xs = new int[] { avex  - w / 2 - 4,
        		avex - w / 2 - 17, avex - w / 2 - 4 };
        ys = new int[] { avey - h / 2, avey,
        		avey + h / 2 };
      }
      else {
        xs = new int[] { avex  + w / 2 + 4,
        		avex + w / 2 + 17, avex  + w / 2 + 4 };
        ys = new int[] { avey - h / 2, avey,
        		avey + h / 2 };
      }
      g.setColor( Color.yellow );
      g.fillPolygon( xs, ys, 3 );
      g.setColor( Color.black );
      g.drawPolygon( xs, ys, 3 );
    }
  }

  public synchronized void paint( Graphics g1 ) {
    Graphics2D g = (Graphics2D)g1;
    g.setRenderingHint( RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON );
    g.setRenderingHint( RenderingHints.KEY_STROKE_CONTROL,
        RenderingHints.VALUE_STROKE_PURE );
    Dimension d = getSize();

    if( ( offscreen == null ) || ( d.width != offscreensize.width )
        || ( d.height != offscreensize.height ) ) {
      offscreen = createImage( d.width, d.height );
      offscreensize = d;
      offgraphics = offscreen.getGraphics();
    }
    offgraphics.setFont( getFont() );

    offgraphics.setColor( getBackground() );
    offgraphics.fillRect( 0, 0, d.width, d.height );
    FontMetrics fm = offgraphics.getFontMetrics();
    for(int i = 0; i < transitions.size(); i++ ) {
      Edge e = transitions.elementAt( i );
      paintEdge( offgraphics, e, fm );
    }

    for(State state : states) {
      paintNode( offgraphics, state, fm );
    }
    g.drawImage( offscreen, 0, 0, null );
  }

  // MouseListener events
  public void mouseClicked( MouseEvent e ) {
	requestFocusInWindow();
	int x = e.getX();
    int y = e.getY();
    
    if( graphtoolbar.selectionMode == GraphToolBar.SELECT //Click twice on a transition to edit it
        && e.getClickCount() == 1 ) {
      for( int i = 0; i < transitions.size(); i++ ) {
        Edge m = transitions.elementAt( i );
        if( mouseInEdge( m, x, y ) && SwingUtilities.isRightMouseButton(e)) {
          //the code in this bracket could be made into a function, but it only appears twice and would need repeat code around it (for settings) anyways
          NewTransitionDialog newTransition = new NewTransitionDialog( m, //TODO: make this allowed for any selection mode
              transitions, machine.machineType,
              true, transitionpanel, messagepanel );
          newTransition.pack();
          //TODO: make the location of a new popup toggleable (center on screen, set to mouse location, etc)
          //newTransition.center(); //center on screen
          newTransition.setLocation(e.getXOnScreen(), e.getYOnScreen()); //set to mouse location
          newTransition.toFront(); //TODO: make this toggleable. it puts new popups at the front of all other windows
          newTransition.validate();
          newTransition.setVisible( true );
        }
      }
      for( State n : states ) {
          if(mouseIn (n, x, y)) {
              if(SwingUtilities.isRightMouseButton(e)) {
                EditStateDialog editState = new EditStateDialog(n,states,this);
                editState.pack();
                //TODO: make the location of a new popup toggleable (center on screen, set to mouse location, etc)
                //editState.center(); //center on screen
                editState.setLocation(e.getXOnScreen(), e.getYOnScreen()); //set to mouse location
                editState.toFront(); //TODO: make this toggleable. it puts new popups at the front of all other windows
                editState.validate();
                editState.setVisible(true);
              }
              if(SwingUtilities.isLeftMouseButton(e) && !multiSelect) {
                selectedStates.clear();
                selectedStates.add(n);
              }
          }
      }
      
      if(SwingUtilities.isLeftMouseButton(e) && !mouseIn(selectedStates, x, y)) {
        selectedStates.clear();
        System.out.println("shouldnt be doing this for an rclick");
      }
    }
    if( graphtoolbar.selectionMode == GraphToolBar.DELETE && e.getClickCount() == 1 ) { //Delete an object (state or transition)        
      State destroy = null;
      for( int i = 0; i < states.size(); i++ ) {
        State n = states.elementAt( i );
        if( mouseIn( n, x, y ) ) {
          destroy = n;
        }
      }
      for( int i = 0; i < transitions.size(); i++ ) {
        Edge m = transitions.elementAt( i );
        if( m.fromState.equals( destroy ) || m.toState.equals( destroy ) ) {
          transitions.removeElementAt( i );
          i--;
        }
      }
      if( destroy != null ) {
        if(selectedStates.contains(destroy)) {
          for (State n : selectedStates) {
            states.removeElement(n);
          }
          selectedStates.clear();
        } else {
          states.removeElement( destroy );
        }
      } else {
        Edge gone = null;
        for( int i = 0; i < transitions.size(); i++ ) {
          Edge n = transitions.elementAt( i );
          if( mouseInEdge( n, x, y ) ) gone = n;
        }
        if( gone != null ) transitions.removeElement( gone );
        gone = null;
      }
      destroy = null;
    }
    if( graphtoolbar.selectionMode == GraphToolBar.SETSTART && SwingUtilities.isLeftMouseButton(e) ) { //sets starting state
      for( int i = 0; i < states.size(); i++ ) {
        State n = states.elementAt( i );
        if( mouseIn( n, x, y ) ) {
          for( int j = 0; j < states.size(); j++ )
            ( states.elementAt( j ) ).startState = false;
          n.startState = true;
        }
      }
    }
    if( graphtoolbar.selectionMode == GraphToolBar.SETCURRENT && SwingUtilities.isLeftMouseButton(e)) { //sets current state
      for( int i = 0; i < states.size(); i++ ) {
        State n = states.elementAt( i );
        if( mouseIn( n, x, y ) ) {
          for( int j = 0; j < states.size(); j++ )
            ( states.elementAt( j ) ).currentState = false;
          n.currentState = true;
          machine.currentState = n;
          machine.clearEdge();
        }
      }
    }
    if( graphtoolbar.selectionMode == GraphToolBar.SETHALT && SwingUtilities.isLeftMouseButton(e)) { //makes a state into a halting state
      for( int i = 0; i < states.size(); i++ ) {
        State n = states.elementAt( i );
        if( mouseIn( n, x, y ) ) {
          if( n.finalState )
            n.finalState = false;
          else n.finalState = true;
        }
      }
    }
  }

  public void mousePressed( MouseEvent e ) {
    int x = e.getX();
    int y = e.getY();
    lastMouseLocation = new Point(x, y);
    
    if( graphtoolbar.selectionMode == GraphToolBar.SELECT ) {
      if(SwingUtilities.isLeftMouseButton(e)) {
        for( int i = 0; i < states.size(); i++ ) {
          State n = states.elementAt( i );
          if( mouseIn( n, x, y ) ) {
            if(!multiSelect && !selectedStates.contains(n)) {
              selectedStates.clear();
            }
            selectedStates.add(n);
            break;
          }
        }
      }
      
      for( int i = 0; i < transitions.size(); i++ ) {
        Edge n = transitions.elementAt( i );
        if( SwingUtilities.isLeftMouseButton(e) && mouseInEdge( n, x, y ) ) {
          selectedStates.clear();
          pickEdge = n;
          if( pickEdge.fromState.x < pickEdge.toState.x ) {
            if( e.getX() > pickEdge.fromState.x
                && e.getX() < pickEdge.toState.x )
              pickEdge.shiftLabel = ((e.getX() - pickEdge.fromState.x ) / ( pickEdge.toState.x - pickEdge.fromState.x ) * 100 -50);
          }
          else {
            if( e.getX() < pickEdge.fromState.x
                && e.getX() > pickEdge.toState.x )
              pickEdge.shiftLabel = -((e.getX() - pickEdge.toState.x ) / ( pickEdge.fromState.x - pickEdge.toState.x ) * 100 -50);
          }
          break;
        }
      }
      
      
    }
    else if( graphtoolbar.selectionMode == GraphToolBar.INSERTSTATE && SwingUtilities.isLeftMouseButton(e)) {
      addState( e.getX(), e.getY(), getNextNodeName() );
      if(multiSelect) {
        selectedStates.add(states.lastElement());
      }
      draggingNewState = true;
    }
    else if( graphtoolbar.selectionMode == GraphToolBar.INSERTEDGE && SwingUtilities.isLeftMouseButton(e)) {    
      for( int i = 0; i < states.size(); i++ ) {
        State n = states.elementAt( i );
        if( mouseIn( n, x, y ) ) {
          tempState1 = n;
          tempState2 = new State( e.getX(), e.getY(), "Temp", false );
          addEdge( tempState1, tempState2 );
        }
      }
    } //TODO add delete multi-select 
    
    //set mouse cursor if dragging states or panning camera
    if( pickEdge == null && (!selectedStates.isEmpty() || SwingUtilities.isRightMouseButton(e))) {
      dragStartScreen = e.getPoint();
      dragEndScreen = null;
      setCursor( new Cursor( Cursor.MOVE_CURSOR ) );
    }
    
    messagepanel.updateLabels( machine.nonBlanks, machine.totalTransitions, machine.states.size(), machine.transitions.size());
    repaint();
    e.consume();
  }

  public void mouseReleased( MouseEvent e ) {
    int x = e.getX();
    int y = e.getY();
    if( graphtoolbar.selectionMode == GraphToolBar.INSERTSTATE && SwingUtilities.isLeftMouseButton(e)) {
      draggingNewState = false;
    }
    if( graphtoolbar.selectionMode == GraphToolBar.INSERTEDGE && SwingUtilities.isLeftMouseButton(e)) {
      if( tempState2 != null ) {
        int i;
        Edge current = transitions.lastElement();
        for( i = 0; i < states.size(); i++ ) {
          State n = states.elementAt( i );
          if( mouseIn( n, x, y ) ) {
            current.toState = n;
            NewTransitionDialog newTransition = new NewTransitionDialog( current,
                transitions, machine.machineType,
                true, transitionpanel, messagepanel );
            newTransition.pack();
            //TODO: make the location of a new popup toggleable (center on screen, set to mouse location, etc)
            //newTransition.center(); //center on screen
            newTransition.setLocation(e.getXOnScreen(), e.getYOnScreen()); //set to mouse location
            newTransition.toFront(); //TODO: make this toggleable. it puts new popups at the front of all other windows
            newTransition.validate();
            newTransition.setVisible( true );
            break;
          }
        }
        if( i == states.size() ) {
          transitions.removeElement( current );
        }
        tempState1 = null;
        tempState2 = null;
      }
    }
    else {
      if( pickEdge != null ) {
        if( pickEdge.fromState.x < pickEdge.toState.x ) {
          if( e.getX() > pickEdge.fromState.x && e.getX() < pickEdge.toState.x )
            pickEdge.shiftLabel = ((e.getX() - pickEdge.fromState.x ) / ( pickEdge.toState.x - pickEdge.fromState.x ) * 100 -50);
        }
        else {
          if( e.getX() < pickEdge.fromState.x && e.getX() > pickEdge.toState.x )
            pickEdge.shiftLabel = -((e.getX() - pickEdge.toState.x ) / ( pickEdge.fromState.x - pickEdge.toState.x ) * 100 -50);
        }
        pickEdge = null;
      }
    }
    messagepanel.updateLabels( machine.nonBlanks, machine.totalTransitions, machine.states.size(), machine.transitions.size());
    repaint();
    e.consume();
  }

  public void mouseEntered( MouseEvent e ) {
  }

  public void mouseExited( MouseEvent e ) {
  }

  // MouseMotionListener events
  public void mouseDragged( MouseEvent e ) {
    int x = e.getX();
    int y = e.getY();
    if( !mouseIn(selectedStates, e.getX(), e.getY() )
        && pickEdge == null && SwingUtilities.isRightMouseButton(e)) {
      moveCamera( e );
    }
    if( graphtoolbar.selectionMode == GraphToolBar.INSERTEDGE ) {
      if( tempState2 != null ) {
        tempState2.x = e.getX();
        tempState2.y = e.getY();
      }
      for( int i = 0; i < states.size(); i++ ) {
        State n = states.elementAt( i );
        if( mouseIn( n, x, y ) )
          n.highlight = true;
        else n.highlight = false;
      }
    } else { //moving states, edges
      if(SwingUtilities.isLeftMouseButton(e)) {
        if(graphtoolbar.selectionMode == GraphToolBar.INSERTSTATE && draggingNewState && !selectedStates.contains(states.lastElement())) {
          State n = states.lastElement();
          n.x += x-lastMouseLocation.getX();
          n.y += y-lastMouseLocation.getY();
        } else {
          for(State n : selectedStates) {
            n.x += x-lastMouseLocation.getX();
            n.y += y-lastMouseLocation.getY();
          }
        }
        if( pickEdge != null ) {
          if( pickEdge.fromState.x < pickEdge.toState.x ) {
            if( e.getX() > pickEdge.fromState.x && e.getX() < pickEdge.toState.x )
              pickEdge.shiftLabel = ((e.getX() - pickEdge.fromState.x ) / ( pickEdge.toState.x - pickEdge.fromState.x ) * 100 -50);
          }
          else {
            if( e.getX() < pickEdge.fromState.x && e.getX() > pickEdge.toState.x )
              pickEdge.shiftLabel = -((e.getX() - pickEdge.toState.x ) / ( pickEdge.fromState.x - pickEdge.toState.x ) * 100 -50);
          }
        }
      }
    }
    lastMouseLocation = new Point(x, y);
    repaint();
    e.consume();
  }

  public void mouseMoved( MouseEvent e ) {
    //if( graphtoolbar.selectionMode != GraphToolBar.INSERTSTATE ) {
      int x = e.getX();
      int y = e.getY();
      boolean hover = false;
      for( int i = 0; i < states.size(); i++ ) {
        State n = states.elementAt( i );
        if( mouseIn( n, x, y ) ) {
          n.highlight = true;
          setCursor( new Cursor( Cursor.HAND_CURSOR ) );
          hover = true;
          break;
        }
        else {
          n.highlight = false;
        }
      }
      for( int i = 0; i < transitions.size(); i++ ) {
        Edge n = transitions.elementAt( i );
        if( mouseInEdge( n, x, y ) ) {
          n.highlight = true;
          setCursor( new Cursor( Cursor.HAND_CURSOR ) );
          hover = true;
          break;
        }
        else {
          n.highlight = false;
        }
      }
      if( !hover ) setCursor( new Cursor( Cursor.DEFAULT_CURSOR ) );
    }
  //}

  // MouseWheelListener events
  public void mouseWheelMoved( MouseWheelEvent e ) {
    zoomCamera( e );
  }

  //checks if (x,y) is within the state being checked
  public boolean mouseIn( State check, int x, int y ) {
    if( x > check.x - w / 2 && x < check.x + w / 2 && y > check.y - h / 2
        && y < check.y + w / 2 ) return true;
    return false;
  }
  
  //checks if (x,y) is within any of the states in the vector being checked
  public boolean mouseIn( Set<State> check, int x, int y ) {
    for( State n : check ) {
      if( mouseIn( n, x, y ) ) {
        return true;
      }
    }
    return false;
  }

  public boolean mouseInEdge( Edge e, int x, int y ) {
    FontMetrics fm = offgraphics.getFontMetrics();
    String label = e.label();
    int w = fm.stringWidth( label ) + 5;
    int h = fm.getHeight();
    int x1 = (int)e.fromState.x;
    int y1 = (int)e.fromState.y;
    if( e.fromState == e.toState ) {
      int j = 0;
      int total = 0;
      for( j = 0; j < transitions.size(); j++ ) {
        Edge temp = transitions.elementAt( j );
        if( temp == e ) break;
        if( temp.fromState == e.fromState && temp.fromState == temp.toState )
          total += h + 2;
      }
      if( x > x1 - w / 2 && x < x1 + w / 2 && y > y1 - 50 - h / 2 - total
          && y < y1 - 50 + h / 2 - total )
        return true;
      else return false;
    }
    int xtemp2 = (int)e.toState.x;
    int x2 = xtemp2;
    int ytemp2 = (int)e.toState.y;
    int y2 = ytemp2;
    int len = (int)Math.abs( Math.sqrt( ( x1 - xtemp2 ) * ( x1 - xtemp2 )
            + ( y1 - ytemp2 ) * ( y1 - ytemp2 ) ) );
    int curvx = (int) ((x1+x2)/2 - (y1-y2)/Math.sqrt(1+len/10));
    int curvy = (int) ((y1+y2)/2 + (x1-x2)/Math.sqrt(1+len/10));
    int avex = curvePoint(x1, curvx, x2, (e.shiftLabel+50)/100);
    int avey = curvePoint(y1, curvy, y2, (e.shiftLabel+50)/100);
    
    
    if( x > avex - w / 2 && x < avex + w / 2
        && y > avey - h / 2 && y < avey + h / 2 )
      return true;
    return false;
  }

  private boolean nameAlreadyExists( String name ) {
      for(State s : states) {
          if(s.stateName.equals(name)) {
              return true;
          }
      }
      return false;
  }

  private String getNextNodeName() {
      int i = 0; //states.size();
      while( nameAlreadyExists( String.valueOf( i ) ) ) {
          i++;
      }
      return String.valueOf( i );
  }

  private void moveCamera( MouseEvent e ) {
    dragEndScreen = e.getPoint();
    double dx = dragEndScreen.getX() - dragStartScreen.getX();
    double dy = dragEndScreen.getY() - dragStartScreen.getY();
    for( int i = 0; i < states.size(); i++ ) {
      State current = states.elementAt( i );
      current.x += dx;
      current.y += dy;
    }
    dragStartScreen = dragEndScreen;
    dragEndScreen = null;
    this.repaint();
  }
  
  private void moveCamera( Point2D.Double start, Point end ) {
    double dx = end.getX() - start.getX();
    double dy = end.getY() - start.getY();
    for( int i = 0; i < states.size(); i++ ) {
      State current = states.elementAt( i );
      current.x += dx;
      current.y += dy;
    }
    this.repaint();
  }

  private void zoomCamera( MouseWheelEvent e ) {
    int wheelRotation = e.getWheelRotation();
    if( wheelRotation > 0 ) {
      if( zoomLevel < maxZoomLevel ) {
        zoomLevel++;
        Font oldF = getFont();
        Font newF = oldF.deriveFont( (float)( oldF.getSize2D() * ( 1 / zoomMultiplicationFactor ) )  );
        setFont( newF );
        double tempW = (double)w;
        double tempH = (double)h;
        tempW *= (1 / zoomMultiplicationFactor);
        tempH *= (1 / zoomMultiplicationFactor);
        w = (int)Math.ceil( tempW );
        h = (int)Math.ceil( tempH );
        for( int i = 0; i < states.size(); i++ ) {
          State current = states.elementAt( i );
          current.x *= (1 / zoomMultiplicationFactor);
          current.y *= (1 / zoomMultiplicationFactor);
        }
        double x = ( e.getX() * (1 / zoomMultiplicationFactor) );
        double y = ( e.getY() * (1 / zoomMultiplicationFactor) );
        Point2D.Double p = new Point2D.Double( x, y );
        moveCamera( p, e.getPoint() );
        this.repaint();
      }
    }
    else {
      if( zoomLevel > minZoomLevel ) {
        zoomLevel--;
        Font oldF = getFont();
        Font newF = oldF.deriveFont( (float)( oldF.getSize2D() * zoomMultiplicationFactor )  );
        setFont( newF );
        w *= ( zoomMultiplicationFactor );
        h *= ( zoomMultiplicationFactor );      
        for( int i = 0; i < states.size(); i++ ) {
          State current = states.elementAt( i );
          current.x *= zoomMultiplicationFactor;
          current.y *= zoomMultiplicationFactor;
        }
        double x = ( e.getX() * zoomMultiplicationFactor );
        double y = ( e.getY() * zoomMultiplicationFactor );
        Point2D.Double p = new Point2D.Double( x, y );
        moveCamera( p, e.getPoint() );
        this.repaint();
      }
    }
    zoomSlider.setValue( -1 * zoomLevel );
  }
  
  public void stateChanged( ChangeEvent e ) {
    int newLevel = -1 * ((JSlider)e.getSource()).getValue();
    if( newLevel > zoomLevel ) {
      for( int j = 0; j < (newLevel - zoomLevel); ++j ) {
        zoomLevel++;
        Font oldF = getFont();
        Font newF = oldF.deriveFont( (float)( oldF.getSize2D() * ( 1 / zoomMultiplicationFactor ) )  );
        setFont( newF );
        double tempW = (double)w;
        double tempH = (double)h;
        tempW *= (1 / zoomMultiplicationFactor);
        tempH *= (1 / zoomMultiplicationFactor);
        w = (int)Math.ceil( tempW );
        h = (int)Math.ceil( tempH );
        for( int i = 0; i < states.size(); i++ ) {
          State current = states.elementAt( i );
          current.x *= (1 / zoomMultiplicationFactor);
          current.y *= (1 / zoomMultiplicationFactor);
        }
        this.repaint();
      }
    }
    else if( newLevel < zoomLevel ){
      for( int j = 0; j < (zoomLevel - newLevel); ++j ) {
        zoomLevel--;
        Font oldF = getFont();
        Font newF = oldF.deriveFont( (float)( oldF.getSize2D() * zoomMultiplicationFactor )  );
        setFont( newF );
        w *= ( zoomMultiplicationFactor );
        h *= ( zoomMultiplicationFactor );        
        for( int i = 0; i < states.size(); i++ ) {
          State current = states.elementAt( i );
          current.x *= zoomMultiplicationFactor;
          current.y *= zoomMultiplicationFactor;
        }
        this.repaint();
      }
    }
  }
  
  public int curvePoint(int p0, int p1, int p2, double offset)
  {
	  double offset1 = offset;
	  if(offset > .95)
		  offset1 = .95;
	  if(offset < .05)
		  offset1 = .05;
	  return (int) ((1-offset1)*((1-offset1)*p0 + offset1*p1) + offset1*((1-offset1)*p1 + offset1*p2)) ;
  }

  public void start() {
    go = new Thread( this );
    go.start();
  }

  public Dimension getMinimumSize() {
    return new Dimension( 300, 500 );
  }

  public Dimension getPreferredSize() {
    return new Dimension( 300, 500 );
  }

  public void update() {
      transitions.update();
  }
  
  public void copy() {
	  clipStates.clear();
	  Map<State, State> stateMap = new HashMap<State, State>();
	  for (State s : selectedStates) {
		  State newState = new State(s.x, s.y, s.stateName, s.finalState);
		  clipStates.add(newState);
		  stateMap.put(s, newState);
	  }
	  clipEdges.clear();
	  ArrayList<Edge> edges = new ArrayList<Edge>();
	  for (int i = 0; i < transitions.size(); i++) {
		  edges.add(transitions.get(i));
	  }
	  for (Edge e : edges) {
		  if (stateMap.containsKey(e.toState) && stateMap.containsKey(e.fromState)) {
			  Edge newEdge = new Edge(stateMap.get(e.fromState), stateMap.get(e.toState));
			  newEdge.shiftLabel = e.shiftLabel;
			  newEdge.oldChar = e.oldChar;
			  newEdge.newChar = e.newChar;
			  newEdge.direction = e.direction;
			  clipEdges.add(newEdge);
		  }
	  }
  }
  
  public void paste() {
	  Set<State> newStatesSet = new HashSet<State>();
	  ArrayList<State> newStates = new ArrayList<State>();
	  double tmpX, tmpY;
	  String tmpName;
	  //Might need to sort clipStates somehow
	  StateComparator comparator = new StateComparator();
	  clipStates.sort(comparator);
	  for (int i = 0; i < clipStates.size(); i++) {
		  State oldState = clipStates.get(i);
		  tmpX = oldState.x + 20;
		  tmpY = oldState.y + 20;
		  tmpName = getNextNodeName();
		  State newState = new State(tmpX, tmpY, tmpName, oldState.finalState);
		  states.addElement(newState);
		  newStatesSet.add(newState);
		  newStates.add(newState);
	  }
	  for (Edge e : clipEdges) {
		  int stateFrom = clipStates.indexOf(e.fromState);
		  int stateTo = clipStates.indexOf(e.toState);
		  Edge newEdge = new Edge(newStates.get(stateFrom), newStates.get(stateTo));
		  newEdge.shiftLabel = e.shiftLabel;
		  newEdge.oldChar = e.oldChar;
		  newEdge.newChar = e.newChar;
		  newEdge.direction = e.direction;
		  transitions.addElement(newEdge);
	  }
	  selectedStates = newStatesSet;
  }


  @Override
  public void keyTyped(KeyEvent e) {
    //System.out.println("key typed");
  }

  @Override
  public void keyPressed(KeyEvent e) {
    //System.out.println("key pressed");
    
    //Hold down LShift to multi-select
    if(e.getKeyCode() == 16) { 
      multiSelect = true;
    }
    
    if(e.getKeyChar() == '1') {
      graphtoolbar.selectionMode = GraphToolBar.SELECT;
    }
    if(e.getKeyChar() == '2') {
      graphtoolbar.selectionMode = GraphToolBar.INSERTSTATE;
    }
    if(e.getKeyChar() == '3') {
      graphtoolbar.selectionMode = GraphToolBar.INSERTEDGE;
    }
    if(e.getKeyChar() == '4') {
      graphtoolbar.selectionMode = GraphToolBar.DELETE;
    }
    if(e.getKeyChar() == '5') {
      graphtoolbar.selectionMode = GraphToolBar.SETSTART;
    }
    if(e.getKeyChar() == '6') {
      graphtoolbar.selectionMode = GraphToolBar.SETCURRENT;
    }
    if(e.getKeyChar() == '7') {
      graphtoolbar.selectionMode = GraphToolBar.SETHALT;
    }
    
    //Determines if ctrl is currently being held
    if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
    	ctrlHeld = true;
    }
    
    //Allows for shortcuts, only if ctrl is being held
    if (ctrlHeld) {
    	if (e.getKeyCode() + 32 == (int)copyKey) {
    		System.out.println("Copied!");
    		copy();
    	}
		if (e.getKeyCode() + 32 == pasteKey) {
			System.out.println("Pasted!");
			paste();
		}
		if (e.getKeyCode() + 32 == saveKey) {
			//This is copy/pasted from TuringMachineFrame.java, since the method in
			//that class cannot be called in this context
			TMFileChooser filechooser = new TMFileChooser();
		    filechooser.setGraphPanel(this);
		    int confirm = filechooser.showSaveDialog(this);
		    if(confirm == TMFileChooser.APPROVE_OPTION)
		    {
		      TMFileChooser.curdir = filechooser.getCurrentDirectory().toString();
		      File newSelect;
		      File select = filechooser.getSelectedFile();
		      if(select.getName().endsWith(".tm"))
		        newSelect = select;
		      else
		        newSelect = new File(select.getParent() + File.separator + select.getName() + ".tm");
		      if(newSelect.exists())
		      {
		        int answer = JOptionPane.showConfirmDialog(this, newSelect.getName()+" already exists, overwrite?",
		            "Error",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
		        switch(answer)
		        {
		          case JOptionPane.YES_OPTION :
		            filechooser.saveFile(newSelect);
		            break;
		          case JOptionPane.NO_OPTION :
		            return;
		        }
		      }
		      else
		        filechooser.saveFile(newSelect);
		    }
		    ctrlHeld = false;
		}
		if (e.getKeyCode() + 32 == openKey) {
			System.out.println("Opening!");
			TMFileChooser filechooser = new TMFileChooser();
		    filechooser.setGraphPanel(this);
		    int confirm = filechooser.showOpenDialog(this);
		    if(confirm == TMFileChooser.APPROVE_OPTION)
		    {
		    	openFile(filechooser.getSelectedFile());
		    }
		    ctrlHeld = false;
		}
    }
    
  }

  public void openFile(File file) {
	  TMFileChooser filechooser = new TMFileChooser();
	  filechooser.setGraphPanel(this);
	  if(file.getName().endsWith("tm"))
	    filechooser.openFile(file);
	  else if(file.getName().endsWith("tmo"))
	    filechooser.openTMOFile(file);
	  else if(file.getName().endsWith("txt"))
	    filechooser.openTapeFile(file);
	  else
	    filechooser.openXMLFile(file);
  }

  @Override
  public void keyReleased(KeyEvent e) {
    //System.out.println("key released");
    
    //Hold down LShift to multi-select
    if(e.getKeyCode() == 16) {
      multiSelect = false;
    }
    
  //Determines if ctrl is released
    if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
    	ctrlHeld = false;
    }
  }
}
