package TuringMachine;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Vector;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class ExecutionSaver extends Thread
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
	  graphpanel.machine.tape.editCellAt(-1, -1);
      graphpanel.machine.tape.clearSelection();
    try
    {
      Vector<Vector<Object>> tapeStates = new Vector<Vector<Object>>();
      Vector<Integer> tapePositions = new Vector<Integer>();
      Vector<String> currentStates = new Vector<String>();
      int furthestLeft = graphpanel.machine.tape.getColumnCount() -1;
      int furthestRight = 0;
      int transitionsMade = 0;
      for(int i = 0; i < number; i++)
      {
        Vector<Object> tape = new Vector<Object>();
        for(int j = 0; j < graphpanel.machine.tape.getColumnCount(); j++)
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
        Vector<?> temp = (Vector<?>)tapeStates.elementAt(i);
        Integer tempLeft = tapePositions.elementAt(i*3);
        Integer tempRight = tapePositions.elementAt(i*3 + 1);
        Integer tempPos = tapePositions.elementAt(i*3 + 2);
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
          else if(((Character)temp.elementAt(j)).charValue() != TM.DEFAULTCHAR ||
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
        saver.print(currentStates.elementAt(i));
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

