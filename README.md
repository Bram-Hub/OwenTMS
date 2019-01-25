# OwenTMSimulator
## Authors
2003-2006:
Owen Kellett  
2010:
Scott Tritten  
2012:
Christopher French  
2015:
Chris Brenon

## About
This is a turing machine simulation software created by RPI students.

How to use:
Save:   Saves transitions, with X,Y positions  (automatically adds .tm extension)
Save Graph: Saves transitions, without X,Y positions (automatically adds .tmo extension)

## Planned Changes
- ~~Reset / rewrite machine button~~
- ~~disable quadruple machine field when one is filled~~
- ~~fix numbering system~~
    - ~~reorganize right-hand window~~
- ~~don't pop window when node is inserted~~
    - ~~add ability to double click to change node name~~
- ~~Create new Turing Machine~~
- ~~Show number of states and transitions~~
- ~~Show number transitions when doing multiple inputs~~
- ~~Only allow for user to select TM type when a new TM is created~~
    - ~~Automatically detect TM type when opening machine~~
- ~~Allow user to open Turing Machine file from command-line~~
- ~~Change "New TM" to clear, and give a "New TM" option that opens a new window~~
- ~~Fix behavior when user presses the close button on a pop-up~~
- ~~Allow user to select TM type on command-line~~
- shortcut buttons
- Allow for user to create comments
- Allow for user to rectangularly select nodes & transitions
- Allow for user to copy and paste nodes and transitions
- Disable modification of tape while TM is running
- Documentation
- Add JUnit testing

## Installation
Build instructions: 
this project uses Ant to create an executable .jar file

From command-line:
Run java -jar TuringMachine4.jar [input file] [-type=quad|quint]
From the command line, the user can select an input file, as well as the type of machine to open.
Also, you can run java -jar TuringMachine4.jar -h to get these usage instructions.

Otherwise, if you drag an input file onto the .jar or .bat file (on windows), the software will open with that TM file.


---VERSION 4.0 RELEASE NOTES--------------------------------------
New Features:
- Added a Reset All button to the menu to reset, clear tape, and load the input tape.
- When working with a quadruple machine, each field (new state and move direction) disables the other field when it changes from NULL, so the user knows that they can't enter both.
- When a node is inserted, no window pops up asking for the node name, allowing for quicker placement of nodes.
- If a node is double clicked in select mode, its name can be changed.
- The umber of states and transitions is shown on the bottom of the screen
- Program asks for quadruple or quintuple machine at the very start - removes dropdown inside
- Allow user to determine type at command-line
- Allow user to specify input file at command-line
- Add option to open new window with "New" in file menu
- Allow user to clear current TM with "Clear" option in file menu

Bugfixes:
- When nodes are created, they find the lowest unused number as their name
- Two nodes cannot both have the same name

Known bugs:
- If a file is saved when the zoom is not 0, the nodes will have their location saved as their apparent location, rather than absolute location.
- Count of states is not updated until next action is taken when a node is deleted

Additional feature ideas:
- Ability to add comments to the drawing area
- Allow for mass copy-pasting and deleting of nodes and transitions
- Add shortcut buttons (for saving, changing modes, etc)


---VERSION 3.0 RELEASE NOTES--------------------------------------
    Changed Save Tape to save more than 1000 characters if the tape has been extended.
            Tape files from Ver 2.0 and earlier no longer supported.
    Changed Save Execution to look at more than 1000 characters if the tape has been extended.
    Removed ability to resize tape cells.
    Increased size of tape cells to fit any valid character.
    Centered text in tape cells.
    Limited the number of characters in a tape cell to 1
    Added validation for manual tape input
        Sets cell values to the last character typed into the cell
        If the character is not valid input, the cell is set to blank (i.e. 0)
    Added an editor each tape cell
        When clicked, the previous contents are cleared.
        When the cell is typed in, the input is sent to the validator and the cell is set to the returned value.
        If the cell is deselected without entering anything, the previous contents are restored.
    Added functionality to deselected the currently selected tape cell when certain features are used 
        e.g. Load input string, Clear tape, Save tape, Start, etc.
    Removed ability to manually edit the tape while the machine is running. 
    When tape is cleared, the tape pointer is now centered on the tape to reduce the need to allocate more tape cells.
    Added the ability to set the tape pointer position by typing "^" into the cell.
    Added the ability to set the starting tape pointer position by surrounding the character in the input with [].
        Works for both single and multiple inputs.
        Also added relavent input validators.
    Changed straight transition lines to curved lines.
    Added the ability to drag trasition lables along the curved lines.

Bug Fixes:
    Fixed a bug where the entire tape panel would be highlighted when clicked.
    Fixed a bug where colums added to ends of the tape would all have the same value even when one was changed.
    Fixed a bug where transition labels would jump when the transition line was exactly vertical.
    Fixed a bug where the Multiple Inputs window was stopping machine execution when the machine wasn't running.
    Fixed a bug where it was possible to get the transition labels stuck beneath the state labels.
    

Known Issues:
    Rapidly scrolling througth the Tape Panel during execution can occationally cause the Tape Panel to display incorrectly
        Priority: High   This issue can interrupt execution and requires the program to be restarted to be resolved
        This can occur during normal execution of Turing Machines which force the Tape Panel to scroll.
        Has been obverved to occur in Ver 1.0 and later.  
    Message window will occasionally fail to scroll to bottom when new text is added.
        Priority: Low    Little effect on program usability
        Can occur when the Multiple Inputs window is closed
        Has been obverved to occur in Ver 2.0 and later.