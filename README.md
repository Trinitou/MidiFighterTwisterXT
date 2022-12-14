# Midi Fighter Twister XT
Extended bi-directional communication for the DJ TechTools MidiFighter Twister

## Firmware
The Midi Fighter Twister firmware has been modified to support a more nuanced bi-directional communication. That means that the controller can be configured on the fly via MIDI cc/SysEx. E.g. individual RGB led colors can be addressed and changed directly during runtime.
* If you are interested in how things work internally, have a look at the [firmware source code](https://github.com/Trinitou/Midi_Fighter_Twister_Open_Source).

## Advanced integration
The extended bi-directional communication features can be used for advanced integration of the Midi Fighter Twister with other software:
* For now there is a [Bitwig Studio](https://www.bitwig.com/) extension to demonstrate the capabilities of this approach. If you are interested in adding new features to that, head over to the [Bitwig extension source code](integration/bitwig/).
* If you want to know more details about the available MIDI/SysEx commands, have a look at the [native mode documentation](https://github.com/Trinitou/Midi_Fighter_Twister_Open_Source/blob/nativeMode/doc/NativeMode.md).

## Contribution
* If there is some bug or something you'd like to see in the future, feel free to [create an issue](https://github.com/Trinitou/MidiFighterTwisterXT/issues) for it.
* Also if you do some advanced integration yourself and are willing to share it with the community, you are very welcome to put it into the [integration](integration) sub-directory and  [create a pull request](https://github.com/Trinitou/MidiFighterTwisterXT/compare).

## License
Extended bi-directional communication for the DJ TechTools Midi Fighter Twister
Copyright (C) 2022  Ulrich Wappler

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
