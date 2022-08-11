# Midi Fighter Twister XT for Bitwig Studio

## Features

* For now there are 2 controller modes: Volume and Pan
  * The 3 upper knob rows control the individual track volume/pan values
  * The bottom right knob controls the master track volume/pan
  * The bottom left knob scrolls the track bank (only available if there are enough tracks available)
  * Volume/pan knobs can be pressed to reset the value
* The current mode can be switched via extension document setting (found in Bitwig Studio's I/O Panel)
* The side buttons can be assigned to specific controller modes via extension preferences (found in Bitwig Studio's controller preferences)
* Advanced visual feedback:
  * RGB led colors are updated according to current track colors
  * Knob indicators show current values
  * Knob indicator appearance represent the current parameter type (bipolar/unipolar values)

## Installation
1. Connect the Midi Fighter Twister to the computer
2. (ensure to have the custom firmware installed correctly!)
3. Copy the .bwextension file into the Bitwig extensions directory
4. In the Bitwig Studio controller preferences, add the extension:
    * Hardware vendor: DJ TechTools
    * Name: Midi Fighter Twister XT
5. Select the Midi Fighter Twister MIDI input and output
