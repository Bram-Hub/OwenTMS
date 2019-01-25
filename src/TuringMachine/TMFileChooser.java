package TuringMachine;

import javax.swing.JFileChooser;
import java.io.*;
import java.lang.Exception;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class TMFileChooser extends JFileChooser
{
  GraphPanel graphpanel;
  public JTextField maximum;

  public TMFileChooser()
  {
    TMFileFilter filter = new TMFileFilter();
    filter.addExtension("tm");
    filter.addExtension("tmo");
    filter.setDescription("Turing Machine files");
    setFileFilter(filter);
  }
  public TMFileChooser(int variation)
  {
    if(variation == 1)
    {
      TMFileFilter filter = new TMFileFilter();
      filter.addExtension("html");
      filter.setDescription("Turing Machine execution files");
      setFileFilter(filter);
      JPanel optionPanel = new JPanel();
      BorderLayout b1 = new BorderLayout();
      optionPanel.setLayout(b1);

      JLabel maxLabel = new JLabel("Maximum Transition Limit : ");
      maximum = new JTextField(8);
      JPanel maxTransitionPanel = new JPanel();
      maxTransitionPanel.add(maxLabel);
      maxTransitionPanel.add(maximum);

      JLabel explainLabel = new JLabel("  Set Limit to account for possible non-halters");
      explainLabel.setSize(100,100);

      optionPanel.add(explainLabel, BorderLayout.CENTER);
      optionPanel.add(maxTransitionPanel, BorderLayout.SOUTH);
      setAccessory(optionPanel);
      maximum.setText("500");
    }
    else if(variation == 2)
    {
      TMFileFilter filter = new TMFileFilter();
      filter.addExtension("tmo");
      filter.setDescription("Turing Machine Graph files");
      setFileFilter(filter);
    }
  }

  public void setGraphPanel(GraphPanel gp)
  {
    graphpanel = gp;
  }

  public void saveFile(File save)
  {
    try
    {
      FileOutputStream outfile = new FileOutputStream(save);
      ObjectOutputStream saver = new ObjectOutputStream(outfile);
      saver.writeObject(graphpanel.states);
      /*saver.writeInt(graphpanel.states.size());
      for(int j = 0; j < graphpanel.states.size(); j++)
      {
        State s = (State)graphpanel.states.elementAt(j);
        saver.writeDouble(s.x);
        saver.writeDouble(s.y);
        saver.writeChars(s.stateName);
        saver.writeBoolean(s.finalState);
        saver.writeBoolean(s.startState);
      }*/
      saver.writeInt(graphpanel.transitions.size());
      for(int i = 0; i < graphpanel.transitions.size(); i++)
      {
        Edge e = (Edge)graphpanel.transitions.elementAt(i);
        saver.writeChar(e.oldChar);
        saver.writeChar(e.newChar);
        saver.writeInt(e.direction);
        saver.writeDouble(e.shiftLabel);
        saver.writeDouble(e.fromState.x);
        saver.writeDouble(e.fromState.y);
        saver.writeObject(e.fromState.stateName);
        saver.writeDouble(e.toState.x);
        saver.writeDouble(e.toState.y);
        saver.writeObject(e.toState.stateName);
      }
      saver.flush();
      saver.close();
    }
    catch(Exception e){e.printStackTrace();}
  }

  public void saveTMOFile(File save)
  {
    try
    {
      FileOutputStream out = new FileOutputStream(save);
      PrintStream saver = new PrintStream(out);
      int start = 0;
      boolean implicit = true;
      int numStates = 0;

      saver.println("digraph TM");
      saver.println("{");
      saver.println("/*Settings*/");
      saver.println("rankdir = LR;");
      saver.println("start [shape=plaintext];");
      saver.println("halt [shape=plaintext];");

      for(int j = 0; j < graphpanel.states.size(); j++)
      {
        State temp = (State)graphpanel.states.elementAt(j);
        if(!temp.finalState)
        {
          saver.print(temp.stateName);
          saver.println(" [shape=circle];");
          numStates++;
        }
        else
          implicit = false;
        if(temp.startState)
          start = j;
      }

      saver.println("/*GraphOutput*/");
      saver.print("/*Num states = ");
      saver.print(numStates);
      saver.println("*/");
      saver.println("/*Quadruple*/");
      if(implicit)
        saver.println("/*Implicit*/");
      else
        saver.println("/*Explicit*/");
      saver.print("start -> ");
      saver.print(((State)graphpanel.states.elementAt(start)).stateName);
      saver.println(";");
      for(int j = 0; j < graphpanel.transitions.size(); j++)
      {
        Edge temp = (Edge)graphpanel.transitions.elementAt(j);
        saver.print(temp.fromState.stateName);
        saver.print(" -> ");
        if(temp.toState.finalState)
          saver.print("halt");
        else
          saver.print(temp.toState.stateName);
        saver.print(" [label=\"");
        saver.print(temp.oldChar);
        saver.print(":");
        if(temp.newChar != TM.NULL)
          saver.print(temp.newChar);
        else
        {
          if(temp.direction == TM.RIGHT)
            saver.print("R");
          else
            saver.print("L");
        }
        saver.println("\"];");
      }
      if(implicit)
      {
        for(int j = 0; j < graphpanel.states.size(); j++)
        {
          boolean zero = false, one = false;
          State tempState = (State)graphpanel.states.elementAt(j);
          for(int i = 0; i < graphpanel.transitions.size(); i++)
          {
            Edge tempEdge = (Edge)graphpanel.transitions.elementAt(i);
            if(tempEdge.fromState == tempState)
            {
              if(tempEdge.oldChar == '0')
                zero = true;
              if(tempEdge.oldChar == '1')
                one = true;
            }
          }
          if(!one)
          {
            saver.print(tempState.stateName);
            saver.print(" -> ");
            saver.print("halt");
            saver.println(" [label=\"1\"];");
          }
          if(!zero)
          {
            saver.print(tempState.stateName);
            saver.print(" -> ");
            saver.print("halt");
            saver.println(" [label=\"0\"];");
          }
        }
      }
      saver.println("/*EndGraphOutput*/");
      saver.println("}");
      saver.flush();
      saver.close();

    }
    catch(Exception e){e.printStackTrace();}
  }

  public void openFile(File open)
  {
    try
    {
      FileInputStream infile = new FileInputStream(open);
      ObjectInputStream opener = new ObjectInputStream(infile);
      graphpanel.states = (Vector)opener.readObject();
      int edges = opener.readInt();
      char oldChar;
      char newChar;
      int direction;
      double shiftLabel;
      double fromX;
      double fromY;
      String fromName;
      double toX;
      double toY;
      String toName;
      State from = new State(0,0,"temp",false);
      State to = new State(0,0,"temp",false);

      graphpanel.transitions = new SortedListModel();
      for(int j = 0; j < edges; j++)
      {
        oldChar = opener.readChar();
        newChar = opener.readChar();
        direction = opener.readInt();
        shiftLabel = opener.readDouble();
        fromX = opener.readDouble();
        fromY = opener.readDouble();
        fromName = (String)opener.readObject();
        toX = opener.readDouble();
        toY = opener.readDouble();
        toName = (String)opener.readObject();
        for(int i = 0; i < graphpanel.states.size(); i++)
        {
          State temp = (State)graphpanel.states.elementAt(i);
          if(temp.x == fromX && temp.y == fromY && temp.stateName == fromName)
            from = temp;
          if(temp.x == toX && temp.y == toY && temp.stateName == toName)
            to = temp;
        }
        Edge insert = new Edge(from, to);
        insert.oldChar = oldChar;
        insert.newChar = newChar;
        insert.direction = direction;
        insert.shiftLabel = shiftLabel;
        graphpanel.transitions.addSortedElement(insert);
      }
      graphpanel.machine.currentEdge = null;
      graphpanel.machine.currentState = null;
      graphpanel.machine.states = graphpanel.states;
      graphpanel.machine.transitions = graphpanel.transitions;
      JList transitions = new JList(graphpanel.transitions);
      transitions.setCellRenderer(new TransitionCellRenderer());
      graphpanel.transitionpanel.getViewport().setView(transitions);
      for(int k = 0; k < graphpanel.states.size(); k++)
      {
        State temp = (State)graphpanel.states.elementAt(k);
        temp.currentState = false;
        temp.highlight = false;
      }
    }
    catch(Exception e){e.printStackTrace();}
  }

  public void openTMOFile(File open)
  {
	int tempIndexOne;
	int tempIndexTwo;
	String tempString;
    try
    {
      BufferedReader infile = new BufferedReader(new FileReader(open));
      String text;
      boolean implicit = false;

      char oldChar = ' ';
      char newChar = ' ';
      int direction = 0;
      State from = new State(0,0,"temp",false);
      State to = new State(0,0,"temp",false);
      int haltOffset = 0;
      boolean haltTransition = false;

      graphpanel.states = new Vector();
      graphpanel.transitions = new SortedListModel();
      Dimension d = graphpanel.getSize();
      Random locationGenerator = new Random();

      while(!infile.readLine().endsWith("/*GraphOutput*/"));
      text = infile.readLine();
	tempIndexOne = text.indexOf(" = ");
	tempIndexTwo = text.indexOf("*/");
	tempString = text.substring(tempIndexOne+3,tempIndexTwo);
      int numStates = Integer.valueOf(tempString).intValue();
      for(int j = 0; j < numStates; j++)
        graphpanel.addState(locationGenerator.nextInt((int)d.getWidth()), locationGenerator.nextInt((int)d.getHeight()), String.valueOf(j));

      text = infile.readLine(); //quadruple halt
      text = infile.readLine();
      if(text.endsWith("/*Implicit*/"))
         implicit = true;
      //text = infile.readLine(); //start transition (ignored)

      text = infile.readLine();
      while(!text.endsWith("/*EndGraphOutput*/"))
      {
        haltOffset = 0;
        haltTransition = false;
        if(text.indexOf("start") != -1)
        {
          State temp = (State)graphpanel.states.elementAt(0);
          temp.startState = false;
          int j = 0;
          while(text.charAt(j) == ' ' || text.charAt(j) == 9)
            j++;

		tempIndexOne = text.indexOf(">");
		tempIndexTwo = text.indexOf(";");
		tempString = text.substring(tempIndexOne+2,tempIndexTwo);

          temp = (State)graphpanel.states.elementAt(Integer.valueOf(tempString).intValue());
          temp.startState = true;
          text = infile.readLine();
          continue;
        }
        if(text.indexOf("halt") != -1)
        {
          if(implicit)
          {
            text = infile.readLine();
            continue;
          }
          else
          {
            haltTransition = true;
            graphpanel.addState(locationGenerator.nextInt((int)d.getWidth()), locationGenerator.nextInt((int)d.getHeight()), String.valueOf(numStates));
            haltOffset = 3;
          }
        }
        int j = 0;
        while(text.charAt(j) == ' ' || text.charAt(j) == 9)
          j++;
	tempIndexOne = text.indexOf("->");
	tempIndexTwo = text.indexOf("label");
	tempString = text.substring(0,tempIndexOne-1);
        from = (State)graphpanel.states.elementAt(Integer.valueOf(tempString).intValue());
        if(!haltTransition)
	{
	  tempString = text.substring(tempIndexOne+3,tempIndexTwo-2);
          to = (State)graphpanel.states.elementAt(Integer.valueOf(tempString).intValue());
	}
        else
        {
          to = (State)graphpanel.states.elementAt(graphpanel.states.size()-1);
          to.finalState = true;
        }
        oldChar = text.charAt(tempIndexTwo+7);
        if(text.charAt(tempIndexTwo+9) == '0' || text.charAt(tempIndexTwo+9) == '1')
        {
          newChar = text.charAt(tempIndexTwo+9);
          direction = TM.NULL;
        }
        else
        {
          newChar = (char)TM.NULL;
          if(text.charAt(tempIndexTwo+9) == 'L')
            direction = TM.LEFT;
          else
            direction = TM.RIGHT;
        }

        Edge insert = new Edge(from, to);
        insert.oldChar = oldChar;
        insert.newChar = newChar;
        insert.direction = direction;
        graphpanel.transitions.addSortedElement(insert);
        text = infile.readLine();
      }

      graphpanel.machine.currentEdge = null;
      graphpanel.machine.currentState = null;
      graphpanel.machine.states = graphpanel.states;
      graphpanel.machine.transitions = graphpanel.transitions;
      JList transitions = new JList(graphpanel.transitions);
      transitions.setCellRenderer(new TransitionCellRenderer());
      graphpanel.transitionpanel.getViewport().setView(transitions);
      for(int k = 0; k < graphpanel.states.size(); k++)
      {
        State temp = (State)graphpanel.states.elementAt(k);
        temp.currentState = false;
        temp.highlight = false;
      }

    }
    catch(Exception e){e.printStackTrace();}
  }
}

class ExecutionSaver extends Thread
{
  public File save;
  public int number;
  public SaveProgressDialog saveProgressDialog;
  GraphPanel graphpanel;

  public ExecutionSaver(File save, int number, SaveProgressDialog saveProgressDialog, GraphPanel graphpanel)
  {
    this.save = save;
    this.number = number;
    this.saveProgressDialog = saveProgressDialog;
    this.graphpanel = graphpanel;
  }

  public void saveSequence()
  {
    try
    {
      Vector tapeStates = new Vector();
      Vector tapePositions = new Vector();
      Vector currentStates = new Vector();
      int furthestLeft = graphpanel.machine.TAPESIZE -1;
      int furthestRight = 0;
      int transitionsMade = 0;
      for(int i = 0; i < number; i++)
      {
        Vector tape = new Vector();
        for(int j = 0; j < graphpanel.machine.TAPESIZE; j++)
          tape.add(graphpanel.machine.tape.getValueAt(0,j));
        tapeStates.add(tape);

        tapePositions.add(new Integer(graphpanel.machine.leftMost));
        tapePositions.add(new Integer(graphpanel.machine.rightMost));
        tapePositions.add(new Integer(graphpanel.machine.tapePos));
        if(i == 0 || graphpanel.machine.leftMost < furthestLeft)
          furthestLeft = graphpanel.machine.leftMost;
        if(i == 0 || graphpanel.machine.rightMost > furthestRight)
          furthestRight = graphpanel.machine.rightMost;

        if(graphpanel.machine.currentState == null)
        {
          if(graphpanel.machine.states.size() > 0)
            graphpanel.machine.setState((TuringMachine.State)graphpanel.machine.states.elementAt(0));
        }
        currentStates.add(graphpanel.machine.currentState.stateName);


        graphpanel.machine.transition();
        transitionsMade = i+1;
        if(!graphpanel.machine.go)
          break;
        saveProgressDialog.saveProgressBar.setValue(i);
      }

      FileOutputStream out = new FileOutputStream(save);
      PrintStream saver = new PrintStream(out);

      saver.println("<html><head><title>Turing Machine Execution</title></head><body>");
      saver.println("<font face = Courier>");

      for(int i = 0; i < transitionsMade; i++)
      {
        boolean print = false;
        Vector temp = (Vector)tapeStates.elementAt(i);
        Integer tempLeft = (Integer)tapePositions.elementAt(i*3);
        Integer tempRight = (Integer)tapePositions.elementAt(i*3 + 1);
        Integer tempPos = (Integer)tapePositions.elementAt(i*3 + 2);
        for(int j = furthestLeft; j <= furthestRight; j++)
        {
          if(j == tempPos.intValue())
            saver.print("<b>");
          if(print)
          {
            if(j <= tempRight.intValue())
              saver.print(((Character)temp.elementAt(j)).charValue());
            else
              saver.print("&nbsp;");
          }
          else if(((Character)temp.elementAt(j)).charValue() != graphpanel.machine.DEFAULTCHAR ||
                  tempLeft.intValue() == j)
          {
              print = true;
              if(j <= tempRight.intValue())
                saver.print(((Character)temp.elementAt(j)).charValue());
              else
                saver.print("&nbsp;");
          }
          else
            saver.print("&nbsp;");
          if(j == tempPos.intValue())
            saver.print("</b>");
        }
        saver.print("&nbsp;&nbsp;State ");
        saver.print((String)currentStates.elementAt(i));
        saver.println("<br>");
        saveProgressDialog.saveProgressBar.setValue(2*number -transitionsMade + i);
      }
      saver.println("</font></body></html>");
      saver.flush();
      saver.close();
      saveProgressDialog.dispose();
    }
    catch(Exception e){e.printStackTrace();}
  }

  public void run()
  {
    saveSequence();
  }
}

class SaveProgressDialog extends JDialog
{
  public JProgressBar saveProgressBar;
  public JLabel indicator;

  public SaveProgressDialog(int number, Frame owner)
  {
    super(owner);
    saveProgressBar = new JProgressBar(0,number*2);
    indicator = new JLabel("Saving Execution Sequence...");
    Container myPane = getContentPane();
    GridLayout myLayout = new GridLayout(2,1);
    myPane.setLayout(myLayout);
    myPane.add(indicator);
    myPane.add(saveProgressBar);
    setSize(200, 100);
    center();
    setTitle("Save Progress");
    repaint();
  }

  public void center()
  {
    Dimension screenSize =
        Toolkit.getDefaultToolkit().getScreenSize();
    int screenWidth = screenSize.width;
    int screenHeight = screenSize.height;

    Dimension frameSize = this.getSize();
    int x = (screenWidth - frameSize.width)/2;
    int y = (screenHeight - frameSize.height)/2;

    if (x < 0)
    {
      x = 0;
      frameSize.width = screenWidth;
    }

    if (y < 0)
    {
      y = 0;
      frameSize.height = screenHeight;
    }
    this.setLocation(x, y);
  }
}
