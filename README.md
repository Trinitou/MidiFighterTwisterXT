# Midi Fighter Twister XT
Extended bi-directional communication for the DJ TechTools MidiFighter Twister

## Firmware
The Midi Fighter Twister firmware has been modified to support a more nuanced bi-directional communication. That means that the controller can be configured on the fly via MIDI cc/SysEx. E.g. individual RGB led colors can be addressed and changed directly during runtime.
* If you are interested in how things work internally, have a look at the [firmware source code](https://github.com/Trinitou/Midi_Fighter_Twister_Open_Source).

## Advanced integration
The extended bi-directional communication features can be used for advanced integration of the Midi Fighter Twister with other software:
* For now there is a [Bitwig Studio](https://www.bitwig.com/) extension to demonstrate the capabilities of this approach. If you are interested in adding new features to that, head over to the [Bitwig extension source code](integration/bitwig/).

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
