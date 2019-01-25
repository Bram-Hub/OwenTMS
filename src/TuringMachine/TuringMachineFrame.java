package TuringMachine;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;

/**
 * Main visual holder component
 * @author Owen F. Kellett
 * @version 1.0
 */
public class TuringMachineFrame extends JFrame {
  /**
   * Helper variable for the contentPane of this
   */
  private JPanel contentPane;
  /**
   * Main menu bar along the top of the frame
   */
  private JMenuBar jMenuBar1 = new JMenuBar();
  /**
   * File menu for the main menu bar
   */
  private JMenu jMenuFile = new JMenu();
  /**
   * Exit option for the File menu
   */
  private JMenuItem jMenuFileExit = new JMenuItem();
  /**
   * Open option for the File menu
   */
  private JMenuItem jMenuFileOpen = new JMenuItem();
  /**
   * Save option for the File menu
   */
  private JMenuItem jMenuFileSave = new JMenuItem();
  /**
   * Save Graph option for the File menu
   */
  private JMenuItem jMenuFileSaveGraph = new JMenuItem();

  private JMenuItem jMenuFileSaveXML = new JMenuItem();

  private JMenuItem jMenuFileSaveTape = new JMenuItem();

  private JMenuItem jMenuFileSaveMultiple = new JMenuItem();
  /**
   * Save Execution option for the File menu
   */
  private JMenuItem jMenuFileSaveExecution = new JMenuItem();
  /**
   * Help menu for the main menu bar
   */
  private JMenu jMenuHelp = new JMenu();
  /**
   * About option for the Help menu
   */
  private JMenuItem jMenuHelpAbout = new JMenuItem();

  /**
   * Status bar for the bottom of the Frame (currently not used)
   */
  private JLabel statusBar = new JLabel();
  /**
   * Layout object for placing items into the Frame
   */
  private BorderLayout borderLayout1 = new BorderLayout();

  /**
   * The guts of the program - the main TM simulator
   */
  TuringMachineSimulator machine = new TuringMachineSimulator();

  /**
   * Construct the frame
   */
  public TuringMachineFrame() {
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Component intialization
   */
  public void jbInit() throws Exception  {

    //setIconImage(Toolkit.getDefaultToolkit().createImage(TuringMachineFrame.class.getResource("[Your Icon]")));
    contentPane = (JPanel) this.getContentPane();
    contentPane.setLayout(borderLayout1);
    this.setSize(new Dimension(800, 600));
    this.setTitle("Turing Machine Simulator");
    statusBar.setText(" ");

    //Initialize and declare menu structure
    jMenuFile.setText("File");
    jMenuFileExit.setText("Exit");
    jMenuFileExit.addActionListener(new ActionListener()  {
      public void actionPerformed(ActionEvent e) {
        jMenuFileExit_actionPerformed(e);
      }
    });
    jMenuFileOpen.setText("Open");
    jMenuFileOpen.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuFileOpen_actionPerformed(e);
      }
    });
    jMenuFileSave.setText("Save");
    jMenuFileSave.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuFileSave_actionPerformed(e);
      }
    });
    jMenuFileSaveGraph.setText("Save Graph");
    jMenuFileSaveGraph.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuFileSaveGraph_actionPerformed(e);
      }
    });
    jMenuFileSaveXML.setText("Save XML");
    jMenuFileSaveXML.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuFileSaveXML_actionPerformed(e);
      }
    });
    jMenuFileSaveTape.setText("Save Tape");
    jMenuFileSaveTape.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuFileSaveTape_actionPerformed(e);
      }
    });
    jMenuFileSaveMultiple.setText("Save Multiple");
    jMenuFileSaveMultiple.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuFileSaveMultiple_actionPerformed(e);
      }
    });
    jMenuFileSaveExecution.setText("Save Execution");
    jMenuFileSaveExecution.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuFileSaveExecution_actionPerformed(e);
      }
    });
    jMenuHelp.setText("Help");
    jMenuHelpAbout.setText("About");
    jMenuHelpAbout.addActionListener(new ActionListener()  {
      public void actionPerformed(ActionEvent e) {
        jMenuHelpAbout_actionPerformed(e);
      }
    });

    //add menu components to menu
    jMenuFile.add(jMenuFileOpen);
    jMenuFile.add(jMenuFileSave);
    jMenuFile.add(jMenuFileSaveGraph);
    jMenuFile.add(jMenuFileSaveXML);
    jMenuFile.add(jMenuFileSaveTape);
    jMenuFile.add(jMenuFileSaveMultiple);
    jMenuFile.add(jMenuFileSaveExecution);
    jMenuFile.add(jMenuFileExit);
    jMenuHelp.add(jMenuHelpAbout);
    jMenuBar1.add(jMenuFile);
    jMenuBar1.add(jMenuHelp);
    this.setJMenuBar(jMenuBar1);

    //contentPane.add(graphtoolbar, BorderLayout.NORTH);
    contentPane.add(statusBar, BorderLayout.SOUTH);
    contentPane.add(machine, BorderLayout.CENTER);
    //contentPane.add(graphpanel, BorderLayout.CENTER);
    //contentPane.add(tapepanel, BorderLayout.SOUTH);
    //graphpanel.start();
  }
  /**
   * File | Exit action - exits the program
   * @param e ActionEvent dummy variable
   */
  public void jMenuFileExit_actionPerformed(ActionEvent e) {
    System.exit(0);
  }

  /**
   * File | Open action - brings up a TMFileChooser dialog
   * @param e ActionEvent dummy variable
   */
  public void jMenuFileOpen_actionPerformed(ActionEvent e) {
    TMFileChooser filechooser = new TMFileChooser();
    filechooser.setGraphPanel(machine.graphpanel);
    int confirm = filechooser.showOpenDialog(this);
    if(confirm == TMFileChooser.APPROVE_OPTION)
    {
      filechooser.curdir = filechooser.getCurrentDirectory().toString();
      if(filechooser.getSelectedFile().getName().endsWith("tm"))
        filechooser.openFile(filechooser.getSelectedFile());
      else if(filechooser.getSelectedFile().getName().endsWith("tmo"))
        filechooser.openTMOFile(filechooser.getSelectedFile());
      else if(filechooser.getSelectedFile().getName().endsWith("txt"))
        filechooser.openTapeFile(filechooser.getSelectedFile());
      else
          filechooser.openXMLFile(filechooser.getSelectedFile());
    }
  }
  /**
   * File | Save action - brings up a TMFileChooser dialog
   * @param e ActionEvent dummy variable
   */
  public void jMenuFileSave_actionPerformed(ActionEvent e) {
    TMFileChooser filechooser = new TMFileChooser();
    filechooser.setGraphPanel(machine.graphpanel);
    int confirm = filechooser.showSaveDialog(this);
    if(confirm == TMFileChooser.APPROVE_OPTION)
    {
      filechooser.curdir = filechooser.getCurrentDirectory().toString();
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
  }

  /**
   * File | Save Graph action - brings up a TMFileChooser dialog
   * @param e ActionEvent dummy variable
   */
  public void jMenuFileSaveGraph_actionPerformed(ActionEvent e) {
    TMFileChooser filechooser = new TMFileChooser(2);
    filechooser.setGraphPanel(machine.graphpanel);
    int confirm = filechooser.showSaveDialog(this);
    if(confirm == TMFileChooser.APPROVE_OPTION)
    {
      filechooser.curdir = filechooser.getCurrentDirectory().toString();
      File newSelect;
      File select = filechooser.getSelectedFile();
      if(select.getName().endsWith(".tmo"))
        newSelect = select;
      else
        newSelect = new File(select.getParent() + File.separator + select.getName() + ".tmo");
      if(newSelect.exists())
      {
        int answer = JOptionPane.showConfirmDialog(this, newSelect.getName()+" already exists, overwrite?",
            "Error",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
        switch(answer)
        {
          case JOptionPane.YES_OPTION :
            filechooser.saveTMOFile(newSelect);
            break;
          case JOptionPane.NO_OPTION :
            return;
        }
      }
      else
        filechooser.saveTMOFile(newSelect);
    }
  }

  public void jMenuFileSaveXML_actionPerformed(ActionEvent e) {
    TMFileChooser filechooser = new TMFileChooser(3);
    filechooser.setGraphPanel(machine.graphpanel);
    int confirm = filechooser.showSaveDialog(this);
    if(confirm == TMFileChooser.APPROVE_OPTION)
    {
      filechooser.curdir = filechooser.getCurrentDirectory().toString();
      File newSelect;
      File select = filechooser.getSelectedFile();
      if(select.getName().endsWith(".xml"))
        newSelect = select;
      else
        newSelect = new File(select.getParent() + File.separator + select.getName() + ".xml");
      if(newSelect.exists())
      {
        int answer = JOptionPane.showConfirmDialog(this, newSelect.getName()+" already exists, overwrite?",
            "Error",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
        switch(answer)
        {
          case JOptionPane.YES_OPTION :
            filechooser.saveXMLFile(newSelect);
            break;
          case JOptionPane.NO_OPTION :
            return;
        }
      }
      else
        filechooser.saveXMLFile(newSelect);
    }
  }

  public void jMenuFileSaveTape_actionPerformed(ActionEvent e) {
    TMFileChooser filechooser = new TMFileChooser(4);
    filechooser.setGraphPanel(machine.graphpanel);
    int confirm = filechooser.showSaveDialog(this);
    if(confirm == TMFileChooser.APPROVE_OPTION)
    {
      filechooser.curdir = filechooser.getCurrentDirectory().toString();
      File newSelect;
      File select = filechooser.getSelectedFile();
      if(select.getName().endsWith(".txt"))
        newSelect = select;
      else
        newSelect = new File(select.getParent() + File.separator + select.getName() + ".txt");
      if(newSelect.exists())
      {
        int answer = JOptionPane.showConfirmDialog(this, newSelect.getName()+" already exists, overwrite?",
            "Error",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
        switch(answer)
        {
          case JOptionPane.YES_OPTION :
            filechooser.saveTapeFile(newSelect);
            break;
          case JOptionPane.NO_OPTION :
            return;
        }
      }
      else
        filechooser.saveTapeFile(newSelect);
    }
  }

  public void jMenuFileSaveMultiple_actionPerformed(ActionEvent e) {
    TMFileChooser filechooser = new TMFileChooser(4);
    filechooser.setGraphPanel(machine.graphpanel);
    int confirm = filechooser.showSaveDialog(this);
    if(confirm == TMFileChooser.APPROVE_OPTION)
    {
      filechooser.curdir = filechooser.getCurrentDirectory().toString();
      File newSelect;
      File select = filechooser.getSelectedFile();
      if(select.getName().endsWith(".txt"))
        newSelect = select;
      else
        newSelect = new File(select.getParent() + File.separator + select.getName() + ".txt");
      if(newSelect.exists())
      {
        int answer = JOptionPane.showConfirmDialog(this, newSelect.getName()+" already exists, overwrite?",
            "Error",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
        switch(answer)
        {
          case JOptionPane.YES_OPTION :
            filechooser.saveInputFile(newSelect);
            break;
          case JOptionPane.NO_OPTION :
            return;
        }
      }
      else
        filechooser.saveInputFile(newSelect);
    }
  }

  /**
   * File | Save Execution action - brings up a TMFileChooser dialog
   * @param e ActionEvent dummy variable
   */
  public void jMenuFileSaveExecution_actionPerformed(ActionEvent e) {
    TMFileChooser filechooser = new TMFileChooser(1);
    filechooser.setGraphPanel(machine.graphpanel);
    int confirm = filechooser.showSaveDialog(this);
    if(confirm == TMFileChooser.APPROVE_OPTION)
    {
      filechooser.curdir = filechooser.getCurrentDirectory().toString();
      File newSelect;
      File select = filechooser.getSelectedFile();
      if(select.getName().endsWith(".html"))
        newSelect = select;
      else
        newSelect = new File(select.getParent() + File.separator + select.getName() + ".html");
      if(newSelect.exists())
      {
        int answer = JOptionPane.showConfirmDialog(this, newSelect.getName()+" already exists, overwrite?",
            "Error",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
        switch(answer)
        {
          case JOptionPane.YES_OPTION :
            SaveProgressDialog progressIndicator = new SaveProgressDialog(Integer.valueOf(filechooser.maximum.getText()).intValue(), this);
            progressIndicator.validate();
            progressIndicator.show();
            machine.machine.setSpeed("Compute");
            ExecutionSaver saveNow = new ExecutionSaver(newSelect, Integer.valueOf(filechooser.maximum.getText()).intValue(), progressIndicator, machine.graphpanel);
            saveNow.start();
            break;
          case JOptionPane.NO_OPTION :
            return;
        }
      }
      else
      {
        SaveProgressDialog progressIndicator = new SaveProgressDialog(Integer.valueOf(filechooser.maximum.getText()).intValue(), this);
        progressIndicator.validate();
        progressIndicator.show();
        machine.machine.setSpeed("Compute");
        ExecutionSaver saveNow = new ExecutionSaver(newSelect, Integer.valueOf(filechooser.maximum.getText()).intValue(), progressIndicator, machine.graphpanel);
        saveNow.start();
      }
    }
  }

  /**
    * Help | About action - displays About dialog
    * @param e ActionEvent dummy variable
    */
  public void jMenuHelpAbout_actionPerformed(ActionEvent e) {
    TuringMachineFrame_AboutBox dlg = new TuringMachineFrame_AboutBox(this);
    Dimension dlgSize = dlg.getPreferredSize();
    Dimension frmSize = getSize();
    Point loc = getLocation();
    dlg.setLocation((frmSize.width - dlgSize.width) / 2 + loc.x, (frmSize.height - dlgSize.height) / 2 + loc.y);
    dlg.setModal(true);
    dlg.pack();
    dlg.show();
  }

  /**
    * Overridden so we can exit when window is closed
    */
  protected void processWindowEvent(WindowEvent e) {
    super.processWindowEvent(e);
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      jMenuFileExit_actionPerformed(null);
    }
  }
}