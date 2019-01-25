package TuringMachine;

import javax.swing.JPanel;
import java.awt.event.*;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.lang.Math;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class GraphPanel extends JPanel
    implements Runnable, MouseListener, MouseMotionListener
{
  //interior components
  Vector states = new Vector(100);
  SortedListModel transitions = new SortedListModel();
  State pick, tempState1, tempState2;
  Edge tempEdge, pickEdge;
  Image offscreen;
  Dimension offscreensize;
  Graphics offgraphics;
  int nextStateName = 0;

  Thread go;

  //references to exterior components
  GraphToolBar graphtoolbar;
  MessagePanel messagepanel;
  TransitionsPane transitionpanel;
  TM machine;

  public GraphPanel(GraphToolBar graphtoolbar)
  {
    addMouseListener(this);
    addMouseMotionListener(this);
    this.graphtoolbar = graphtoolbar;
  }

  public void setMessagePanel(MessagePanel messagepanel)
  {
    this.messagepanel = messagepanel;
  }

  public void setTransitionPanel(TransitionsPane transitionpanel)
  {
    this.transitionpanel = transitionpanel;
  }
  public void setMachine(TM machine)
  {
    this.machine = machine;
  }

  public void run()
  {
    Thread me = Thread.currentThread();
    while(me == go)
    {
      repaint();
      try
      {
        Thread.sleep(100);
      }
      catch (InterruptedException e)
      {
        break;
      }
    }
  }

  void addState(double x, double y, String name)
  {
    states.addElement(new State(x,y,name,false));
  }

  void addEdge(State from, State to)
  {
    transitions.addElement(new Edge(from, to));
  }



  final Color stateColor = Color.white;
  final Color selectedStateColor = Color.orange;
  final Color currentStateColor = Color.green;
  final Color highlightStateColor = Color.magenta;

  final Color edgeColor = Color.black;
  final Color selectedEdgeColor = Color.red;
  final Color currentEdgeColor = Color.green;

  public void paintNode(Graphics g, State n, FontMetrics fm)
  {
    int x = (int)n.x;
    int y = (int)n.y;
    if(n == pick)
      g.setColor(selectedStateColor);
    else if(n.currentState)
      g.setColor(currentStateColor);
    else
      g.setColor(stateColor);
    if(n.highlight)
      g.setColor(highlightStateColor);
    int w = fm.stringWidth(n.stateName) + 10;
    int h = fm.getHeight() + 10;
    g.fillOval(x - w/2, y - h / 2, w, h);
    g.setColor(Color.black);
    g.drawOval(x - w/2, y - h / 2, w, h);
    g.drawString(n.stateName, x - (w-10)/2, (y - (h-8)/2) + fm.getAscent());
    if(n.finalState)
      g.drawOval(x - w/2 + 3, y - h/2 + 3, w-6, h-6);
    if(n.startState)
    {
      g.setColor(Color.yellow);
      int xs[], ys[];
      xs = new int[] {x - w/2, x - w/2 - 10, x - w/2 - 10};
      ys = new int[] {y, y-h/3, y+h/3};
      g.fillPolygon(xs, ys, 3);
      g.setColor(Color.black);
      g.drawPolygon(xs, ys, 3);
    }
  }

  public void paintEdge(Graphics g, Edge e, FontMetrics fm)
  {
    if(Math.abs(e.shiftLabel) > Math.abs(e.fromState.x - e.toState.x))
      e.shiftLabel = 0;
    int x1 = (int)e.fromState.x;
    int y1 = (int)e.fromState.y;
    int xtemp2 = (int)e.toState.x;
    int x2 = xtemp2 - (int)e.shiftLabel;
    int ytemp2 = (int)e.toState.y;
    double ytemp22 = (float)ytemp2 + e.shiftLabel*(((float)y1-(float)e.toState.y)/((float)xtemp2-(float)x1));
    int y2 = (int)ytemp22;
    String label = e.label();
    int w = fm.stringWidth(label) + 5;
    int h = fm.getHeight();
    int len = (int)Math.abs(Math.sqrt((x1-xtemp2)*(x1-xtemp2) + (y1-ytemp2)*(y1-ytemp2)));
    if(e.currentEdge)
      g.setColor(currentEdgeColor);
    else
      g.setColor(edgeColor);

    if(len == 0)
    {
      int j = 0;
      int total = 0;
      for(j = 0; j < transitions.size(); j++)
      {
        Edge temp = (Edge)transitions.elementAt(j);
        if(temp == e)
          break;
        if(temp.fromState == e.fromState && temp.fromState == temp.toState)
          total += h + 2;
      }
      if(total == 0)
        g.drawOval(x1 - 10, y1 - 50, 20, 50);
      g.setColor(Color.yellow);
      if(e.highlight)
        g.setColor(selectedEdgeColor);
      if(e.currentEdge)
        g.setColor(currentEdgeColor);
      g.fillRect(x1 - w/2, y1 - 50 - h/2 - total, w, h);
      g.setColor(Color.black);
      g.drawRect(x1 - w/2, y1 - 50 - h/2 - total, w, h);
      g.drawString(label, x1 - (w-5)/2, (y1 - 50 - (h)/2) - total + fm.getAscent());
    }
    else
    {
      int j = 0;
      boolean line = true;
      for(j = 0; j < transitions.size(); j++)
      {
        Edge temp = (Edge)transitions.elementAt(j);
        if(temp == e)
          break;
        if((temp.fromState == e.fromState && temp.toState == e.toState) ||
           (temp.toState == e.fromState && temp.fromState == e.toState))
          line = false;
      }
      if(line)
        g.drawLine(x1, y1, xtemp2, (int)ytemp2);
      g.setColor(Color.yellow);
      if(e.highlight)
        g.setColor(selectedEdgeColor);
      if(e.currentEdge)
        g.setColor(currentEdgeColor);
      g.fillRect(x1 + (x2-x1)/2 - w/2, y1 + (y2-y1)/2 - h/2, w, h);
      g.setColor(Color.black);
      g.drawRect(x1 + (x2-x1)/2 - w/2, y1 + (y2-y1)/2 - h/2, w, h);
      g.drawString(label, x1 + (x2-x1)/2 - (w-5)/2, y1 + (y2-y1)/2 - h/2 + fm.getAscent());

      int xs[], ys[];
      if(x1 > x2)
      {
        xs = new int[] {x1 + (x2-x1)/2 - w/2 - 4, x1 + (x2-x1)/2 - w/2 - 17, x1 + (x2-x1)/2 - w/2 - 4};
        ys = new int[] {y1 + (y2-y1)/2 - h/2, y1 + (y2-y1)/2, y1 + (y2-y1)/2 + h/2};
      }
      else
      {
        xs = new int[] {x1 + (x2-x1)/2 + w/2 + 4, x1 + (x2-x1)/2 + w/2 + 17, x1 + (x2-x1)/2 + w/2 + 4};
        ys = new int[] {y1 + (y2-y1)/2 - h/2, y1 + (y2-y1)/2, y1 + (y2-y1)/2 + h/2};
      }
      g.setColor(Color.yellow);
      g.fillPolygon(xs, ys, 3);
      g.setColor(Color.black);
      g.drawPolygon(xs, ys, 3);
    }
  }

  //public synchronized void update(Graphics g)
  public synchronized void paint(Graphics g)
  {
    Dimension d = getSize();
    if ((offscreen == null) || (d.width != offscreensize.width) || (d.height != offscreensize.height)) {
      offscreen = createImage(d.width, d.height);
      offscreensize = d;
      offgraphics = offscreen.getGraphics();
      offgraphics.setFont(getFont());
    }

    offgraphics.setColor(getBackground());
    offgraphics.fillRect(0, 0, d.width, d.height);
    FontMetrics fm = offgraphics.getFontMetrics();
    for (int i = 0 ; i < transitions.size(); i++)
    {
      Edge e = (Edge)transitions.elementAt(i);
      paintEdge(offgraphics, e, fm);
    }


    for (int i = 0 ; i < states.size() ; i++)
    {
      paintNode(offgraphics, (State)states.elementAt(i), fm);
    }
    g.drawImage(offscreen, 0, 0, null);
  }

  //MouseListener events
  public void mouseClicked(MouseEvent e)
  {
    if(graphtoolbar.selectionMode == graphtoolbar.SELECT && e.getClickCount() == 2)
    {
      int x = e.getX();
      int y = e.getY();
      for(int i = 0; i < transitions.size(); i++)
      {
        Edge m = (Edge)transitions.elementAt(i);
        if(mouseInEdge(m, x, y))
        {
          NewTransitionDialog newTransition = new NewTransitionDialog(m, transitions, (String)messagepanel.machineType.getSelectedItem(), true, transitionpanel);
          newTransition.pack();
          newTransition.center();
          newTransition.validate();
          newTransition.show();
        }
      }
    }
    if(graphtoolbar.selectionMode == graphtoolbar.DELETE && e.getClickCount() == 1)
    {
      int x = e.getX();
      int y = e.getY();
      State destroy = null;
      for (int i = 0 ; i < states.size(); i++)
      {
        State n = (State)states.elementAt(i);
        if(mouseIn(n,x,y))
        {
          destroy = n;
        }
      }
      for(int i = 0; i < transitions.size(); i++)
      {
        Edge m = (Edge)transitions.elementAt(i);
        if(m.fromState.equals(destroy) || m.toState.equals(destroy))
        {
          transitions.removeElementAt(i);
          i--;
        }
      }
      if(destroy != null)
        states.removeElement(destroy);
      else
      {
        Edge gone = null;
        for(int i = 0; i < transitions.size(); i++)
        {
          Edge n = (Edge)transitions.elementAt(i);
          if(mouseInEdge(n,x,y))
            gone = n;
        }
        if(gone != null)
          transitions.removeElement(gone);
        gone = null;
      }
      destroy = null;
    }
    if(graphtoolbar.selectionMode == graphtoolbar.SETSTART)
    {
      int x = e.getX();
      int y = e.getY();
      for (int i = 0 ; i < states.size(); i++)
      {
        State n = (State)states.elementAt(i);
        if(mouseIn(n,x,y))
        {
          for(int j = 0; j < states.size(); j++)
            ((State)states.elementAt(j)).startState = false;
          n.startState = true;
        }
      }
    }
    if(graphtoolbar.selectionMode == graphtoolbar.SETCURRENT)
    {
      int x = e.getX();
      int y = e.getY();
      for (int i = 0 ; i < states.size(); i++)
      {
        State n = (State)states.elementAt(i);
        if(mouseIn(n,x,y))
        {
          for(int j = 0; j < states.size(); j++)
            ((State)states.elementAt(j)).currentState = false;
          n.currentState = true;
          machine.currentState = n;
          machine.clearEdge();
        }
      }
    }
    if(graphtoolbar.selectionMode == graphtoolbar.SETHALT)
    {
      int x = e.getX();
      int y = e.getY();
      for (int i = 0 ; i < states.size(); i++)
      {
        State n = (State)states.elementAt(i);
        if(mouseIn(n,x,y))
        {
          if(n.finalState)
            n.finalState = false;
          else
            n.finalState = true;
        }
      }
    }
  }

  public void mousePressed(MouseEvent e)
  {
    if(graphtoolbar.selectionMode == graphtoolbar.SELECT)
    {
      int x = e.getX();
      int y = e.getY();
      for (int i = 0 ; i < states.size(); i++)
      {
        State n = (State)states.elementAt(i);
        if(mouseIn(n,x,y))
        {
          pick = n;
          pick.x = x;
          pick.y = y;
        }
      }
      if(pick == null)
      {
        for(int i = 0; i < transitions.size(); i++)
        {
          Edge n = (Edge)transitions.elementAt(i);
          if(mouseInEdge(n, x, y))
          {
            pickEdge = n;
            if(pickEdge.fromState.x < pickEdge.toState.x)
            {
              if(e.getX() > pickEdge.fromState.x && e.getX() < pickEdge.toState.x)
                pickEdge.shiftLabel =
                (pickEdge.fromState.x + (pickEdge.toState.x - pickEdge.fromState.x)/2 - e.getX())*2;
            }
            else
            {
              if(e.getX() < pickEdge.fromState.x && e.getX() > pickEdge.toState.x)
               pickEdge.shiftLabel =
                (pickEdge.fromState.x + (pickEdge.toState.x - pickEdge.fromState.x)/2 - e.getX())*2;
            }
          }
        }
      }
    }
    else if(graphtoolbar.selectionMode == graphtoolbar.INSERTSTATE)
    {
      addState(e.getX(), e.getY(), String.valueOf(states.size()));
      pick = (State)states.lastElement();
    }
    else if(graphtoolbar.selectionMode == graphtoolbar.INSERTEDGE)
    {
      int x = e.getX();
      int y = e.getY();
      for (int i = 0 ; i < states.size(); i++)
      {
        State n = (State)states.elementAt(i);
        if(mouseIn(n,x,y))
        {
          tempState1 = n;
          tempState2 = new State(e.getX(), e.getY(), "Temp", false);
          addEdge(tempState1, tempState2);
        }
      }
    }
    repaint();
    e.consume();
  }

  public void mouseReleased(MouseEvent e)
  {
    if(graphtoolbar.selectionMode == graphtoolbar.INSERTEDGE)
    {
      if(tempState2 != null)
      {
        int x = e.getX();
        int y = e.getY();
        int i;
        Edge current = (Edge)transitions.lastElement();
        for (i = 0 ; i < states.size(); i++)
        {
          State n = (State)states.elementAt(i);
          if(mouseIn(n,x,y))
          {
            current.toState = n;
            NewTransitionDialog newTransition = new NewTransitionDialog(current, transitions, (String)messagepanel.machineType.getSelectedItem(), false, transitionpanel);
            newTransition.pack();
            newTransition.center();
            newTransition.validate();
            newTransition.show();
            break;
          }
        }
        if(i == states.size())
        {
          transitions.removeElement(current);
        }
        tempState1 = null;
        tempState2 = null;
      }
    }
    else if(graphtoolbar.selectionMode == graphtoolbar.INSERTSTATE)
    {
      if(pick != null)
      {
        pick.x = e.getX();
        pick.y = e.getY();
        NewStateDialog newState = new NewStateDialog(pick, states);
        newState.pack();
        newState.center();
        newState.validate();
        newState.show();
        pick = null;
      }
    }
    else
    {
      if(pick != null)
      {
        pick.x = e.getX();
        pick.y = e.getY();
        pick = null;
      }
      if(pickEdge != null)
      {
        if(pickEdge.fromState.x < pickEdge.toState.x)
        {
          if(e.getX() > pickEdge.fromState.x && e.getX() < pickEdge.toState.x)
            pickEdge.shiftLabel =
            (pickEdge.fromState.x + (pickEdge.toState.x - pickEdge.fromState.x)/2 - e.getX())*2;
        }
        else
        {
          if(e.getX() < pickEdge.fromState.x && e.getX() > pickEdge.toState.x)
           pickEdge.shiftLabel =
            (pickEdge.fromState.x + (pickEdge.toState.x - pickEdge.fromState.x)/2 - e.getX())*2;
            }
        pickEdge = null;
      }
    }
    repaint();
    e.consume();
  }

  public void mouseEntered(MouseEvent e){}
  public void mouseExited(MouseEvent e){}

  //MouseMotionListener events
  public void mouseDragged(MouseEvent e)
  {
    if(graphtoolbar.selectionMode == graphtoolbar.INSERTEDGE)
    {
      if(tempState2 != null)
      {
        tempState2.x = e.getX();
        tempState2.y = e.getY();
      }
      int x = e.getX();
      int y = e.getY();
      for (int i = 0 ; i < states.size(); i++)
      {
        State n = (State)states.elementAt(i);
        if(mouseIn(n,x,y))
          n.highlight = true;
        else
          n.highlight = false;
      }
    }
    else
    {
      if(states.size() > 0 && pick != null)
      {
        pick.x = e.getX();
        pick.y = e.getY();
      }
      if(pickEdge != null)
      {
        if(pickEdge.fromState.x < pickEdge.toState.x)
        {
          if(e.getX() > pickEdge.fromState.x && e.getX() < pickEdge.toState.x)
            pickEdge.shiftLabel =
            (pickEdge.fromState.x + (pickEdge.toState.x - pickEdge.fromState.x)/2 - e.getX())*2;
        }
        else
        {
          if(e.getX() < pickEdge.fromState.x && e.getX() > pickEdge.toState.x)
           pickEdge.shiftLabel =
            (pickEdge.fromState.x + (pickEdge.toState.x - pickEdge.fromState.x)/2 - e.getX())*2;
            }
      }
    }
    repaint();
    e.consume();
  }

  public void mouseMoved(MouseEvent e)
  {
    if(graphtoolbar.selectionMode != graphtoolbar.INSERTSTATE)
    {
      int x = e.getX();
      int y = e.getY();
      for (int i = 0 ; i < states.size(); i++)
      {
        State n = (State)states.elementAt(i);
        if(mouseIn(n,x,y))
          n.highlight = true;
        else
          n.highlight = false;
      }
      for(int i = 0; i < transitions.size(); i++)
      {
        Edge n = (Edge)transitions.elementAt(i);
        if(mouseInEdge(n,x,y))
          n.highlight = true;
        else
          n.highlight = false;
      }
    }
  }

  public boolean mouseIn(State check, int x, int y)
  {
    FontMetrics fm = offgraphics.getFontMetrics();
    int w = fm.stringWidth(check.stateName) + 10;
    int h = fm.getHeight() + 10;
    if(x > check.x - w/2 && x < check.x + w/2 &&
       y > check.y - h/2 && y < check.y + w/2)
      return true;
    return false;
  }

  public boolean mouseInEdge(Edge e, int x, int y)
  {
    FontMetrics fm = offgraphics.getFontMetrics();
    String label = e.label();
    int w = fm.stringWidth(label) + 5;
    int h = fm.getHeight();
    int x1 = (int)e.fromState.x;
    int y1 = (int)e.fromState.y;
    if(e.fromState == e.toState)
    {
      int j = 0;
      int total = 0;
      for(j = 0; j < transitions.size(); j++)
      {
        Edge temp = (Edge)transitions.elementAt(j);
        if(temp == e)
          break;
        if(temp.fromState == e.fromState && temp.fromState == temp.toState)
          total += h + 2;
      }
      if(x > x1 - w/2 && x < x1 + w/2 &&
         y > y1 - 50 - h/2 - total && y < y1 - 50 + h/2 - total)
        return true;
      else return false;
    }
    int xtemp2 = (int)e.toState.x;
    int x2 = xtemp2 - (int)e.shiftLabel;
    int ytemp2 = (int)e.toState.y;
    double ytemp22 = (float)ytemp2 + e.shiftLabel*(((float)y1-(float)e.toState.y)/((float)xtemp2-(float)x1));
    int y2 = (int)ytemp22;
    if(x > x1 + (x2-x1)/2 - w/2 &&
       x < x1 + (x2-x1)/2 + w/2 &&
       y > y1 + (y2-y1)/2 - h/2 &&
       y < y1 + (y2-y1)/2 + h/2)
      return true;
    return false;
  }

  public void start()
  {
    go = new Thread(this);
    go.start();
  }

  public Dimension getMinimumSize()
  {
    return new Dimension(300, 500);
  }

  public Dimension getPreferredSize()
  {
    return new Dimension(300, 500);
  }
}