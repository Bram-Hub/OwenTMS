# OwenTMSimulator
## Authors
2003-2006:
Owen Kellett  
2010:
Scott Tritten  
2012:
Christopher French

## About
This is a turing machine simulation software created by RPI students.

How to use:
Save:   Saves transitions, with X,Y positions  (automatically adds .tm extension)
Save Graph: Saves transitions, without X,Y positions (automatically adds .tmo extension)

## Installation
Build instructions:
The program can be compiled and run using ant.  
In this directory, simply execute:

ant run

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