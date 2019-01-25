package TuringMachine;

import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Holding component which houses all visual components of the actual simulator
 * @author Owen F. Kellett
 * @version 1.0
 */

public class TuringMachineSimulator extends JPanel
{
  /**
   * Toolbar seen along the left side of the window
   */
  public GraphToolBar graphtoolbar = new GraphToolBar();
  /**
   * Main Turing Machine "Drawing Area"
   */
  public GraphPanel graphpanel = new GraphPanel(graphtoolbar);
  /**
   * Component which simulates the actual execution of the machine
   */
  public TM machine = new TM();
  /**
   * Panel which contains main controls and status information
   */
  public MessagePanel messagepanel = new MessagePanel();
  //public TapePanel tapepanel = new TapePanel();
  //public JScrollPane tapepanel = new JScrollPane();
  /**
   * Panel which lists transitions in a textual representation
   */
  public TransitionsPane transitionspanel = new TransitionsPane();
  /**
   * The tape
   */
  public TapePanel tapepanel = new TapePanel();
  /**
   * Main execution thread
   */
  Thread execution;

  /**
   * Main constructor - add all the pieces to the panel
   */
  public TuringMachineSimulator()
  {
    execution = new Thread(machine);
    machine.setStates(graphpanel.states);
    machine.setTransitions(graphpanel.transitions);
    machine.setMessagePanel(messagepanel);
    machine.setTransitionPanel(transitionspanel);
    //machine.setTape(tapepanel);
    messagepanel.setMachine(machine);
    messagepanel.setExecution(execution);
    graphpanel.setMessagePanel(messagepanel);
    graphpanel.setTransitionPanel(transitionspanel);
    graphpanel.setMachine(machine);
    //messagepanel.setTapeDisplay(tapepanel);

    //JList tape = new JList(machine.tape);
    //tapepanel.getViewport().setView(tape);

    JList transitions = new JList(machine.transitions);
    transitions.setCellRenderer(new TransitionCellRenderer());
    transitionspanel.setBorder(BorderFactory.createTitledBorder("Transitions"));
    transitionspanel.getViewport().setView(transitions);

    tapepanel.setBorder(BorderFactory.createTitledBorder("Tape"));
    tapepanel.getViewport().setView(machine.tape);

 //   tapepanel.setMachine(machine);
 //   tapepanel.setExecution(execution);

/*    //add(tapepanel);
    GridBagLayout gbl = new GridBagLayout();
    GridBagConstraints gbcon = new GridBagConstraints();
    setLayout(gbl);

    gbcon.gridx = 0;
    gbcon.gridy = 0;
    gbcon.gridwidth = 1;
    gbcon.fill = GridBagConstraints.BOTH;
    gbcon.insets = new Insets(0,0,0,0);
    gbcon.anchor = GridBagConstraints.NORTHWEST;
    //gbcon.weighty = 0.6;
    gbl.setConstraints(tapepanel, gbcon);
    add(tapepanel);
    tapepanel.validate();

    gbcon.gridy = 1;
    //gbcon.weighty = 0.2;
    gbl.setConstraints(graphtoolbar, gbcon);
    add(graphtoolbar);
    graphtoolbar.validate();

    gbcon.gridy = 2;
    gbcon.gridwidth = 1;
   // gbcon.anchor = GridBagConstraints.SOUTHEAST;
   // gbcon.weighty = 0.2;
   // gbcon.insets = new Insets(0,0,0,15);
    graphpanel.setSize(300,300);
    gbl.setConstraints(graphpanel, gbcon);
    add(graphpanel);
    graphpanel.validate();

    gbcon.gridy = 3;
   // gbcon.insets = new Insets(0,15,0,0);
    gbl.setConstraints(messagepanel, gbcon);
    add(messagepanel);
    messagepanel.validate();*/

    BorderLayout bl = new BorderLayout();
    setLayout(bl);
    JPanel drawingpanel = new JPanel();
    drawingpanel.setBorder(BorderFactory.createTitledBorder("Drawing Area"));
    drawingpanel.setLayout(new BorderLayout());
    drawingpanel.add(graphpanel, BorderLayout.CENTER);
    add(drawingpanel, BorderLayout.CENTER);
    add(transitionspanel, BorderLayout.EAST);
    add(graphtoolbar, BorderLayout.WEST);
    add(tapepanel, BorderLayout.NORTH);
    add(messagepanel, BorderLayout.SOUTH);


    //tapepanel.initGraphics();
    graphpanel.start();
    //tapepanel.start();
    tapepanel.getHorizontalScrollBar().setMaximum(machine.TAPESIZE*10);
    tapepanel.getHorizontalScrollBar().setValue(machine.TAPESIZE*5 + 115);
  }
}
