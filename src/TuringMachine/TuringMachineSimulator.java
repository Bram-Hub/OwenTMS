package TuringMachine;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSlider;

/**
 * Holding component which houses all visual components of the actual simulator
 * 
 * @author Owen F. Kellett
 * @version 1.0
 */

public class TuringMachineSimulator extends JPanel {
  /**
	 * 
	 */
  private static final long serialVersionUID = -1769749247238343930L;
  /**
   * Toolbar seen along the left side of the window
   */
  public GraphToolBar graphtoolbar = new GraphToolBar();
  /**
   * Main Turing Machine "Drawing Area"
   */
  public GraphPanel graphpanel = new GraphPanel( graphtoolbar );
  public JSlider zoomSlider = new JSlider( 1, -10, 10, 0 );
  /**
   * Component which simulates the actual execution of the machine
   */
  public TM machine = new TM();
  /**
   * Panel which contains main controls and status information
   */
  public MessagePanel messagepanel = new MessagePanel();

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
  public TuringMachineSimulator() {
    execution = new Thread( machine );
    machine.setStates( graphpanel.states );
    machine.setTransitions( graphpanel.transitions );
    machine.setMessagePanel( messagepanel );
    machine.setTransitionPanel( transitionspanel );
    messagepanel.setMachine( machine );
    messagepanel.setExecution( execution );
    graphpanel.setMessagePanel( messagepanel );
    graphpanel.setTransitionPanel( transitionspanel );
    graphpanel.setMachine( machine );
    graphpanel.setSlider( zoomSlider );
    zoomSlider.setMajorTickSpacing( 5 );
    zoomSlider.setMinorTickSpacing( 1 );
    zoomSlider.setPaintTicks( true );
    zoomSlider.setSnapToTicks( true );
    zoomSlider.addChangeListener( graphpanel );

    JList<Edge> transitions = new JList<Edge>( machine.transitions );
    transitions.setCellRenderer( new TransitionCellRenderer() );
    transitionspanel.setBorder( BorderFactory
        .createTitledBorder( "Transitions" ) );
    transitionspanel.getViewport().setView( transitions );

    tapepanel.setBorder( BorderFactory.createTitledBorder( "Tape" ) );
    tapepanel.getViewport().setView( machine.tape );

    BorderLayout bl = new BorderLayout();
    setLayout( bl );
    JPanel drawingpanel = new JPanel();
    JPanel zoompanel = new JPanel();
    drawingpanel.setBorder( BorderFactory.createTitledBorder( "Drawing Area" ) );
    drawingpanel.setLayout( new BorderLayout() );
    drawingpanel.add( graphpanel, BorderLayout.CENTER );
    
    zoompanel.setLayout( new BorderLayout() );
    zoompanel.add( drawingpanel, BorderLayout.CENTER );
    zoompanel.add( zoomSlider, BorderLayout.EAST );
    
    add( zoompanel, BorderLayout.CENTER );
    add( transitionspanel, BorderLayout.EAST );
    add( graphtoolbar, BorderLayout.WEST );
    add( tapepanel, BorderLayout.NORTH );
    add( messagepanel, BorderLayout.SOUTH );

    graphpanel.start();
    tapepanel.getHorizontalScrollBar().setMaximum( TM.TAPESIZE * 14 );
    tapepanel.getHorizontalScrollBar().setValue( TM.TAPESIZE * 7 );
  }
}
