#SEG TODO list

###GUI

+ ~~Draw robots~~
+ Draw robot paths
+ ~~Draw garbage objects~~
+ ~~Update object locations - see above~~ No longer required, due to changes in drawing logic
+ ~~Save floor plan to an image - PNG, due to lossy nature of JPG~~
+ ~~Center scrollpane viewport automatically~~
+ ~~Integrate all needed GUI forms into 1, for easier usage~~
+ Differentiate between fiducially and non-fiducially explored cells (debugging)

###Robot

+ ~~Blocking move() methods (do not return from method until target reached, or move cancelled), without freezing the GUI (separate thread?)~~
+ ~~Extend the Robot class to be Observable~~ No longer needed, GUI polls robot position as needed
+ ~~Subscribe the GUI to listen to Robot(s) for updates of robot position~~ No longer needed, GUI polls robot position as needed

###Exploration

+ Explore garbage objects, not just the floor plan
+ Do not map other robots as occupied space
+ Blocking Control.explore() method, to meet CLI requirements

###Path-finding

+ Remove unneeded nodes from path, to speed up robot navigation

###Garbage

+ Align robot with garbage
+ Pick/drop garbage
+ ~~Create a garbage class to keep track of garbage objects~~
+ ~~Extend class with Observable, to update observers on garbage move/collection~~
+ ~~Subscribe GUI to Garbage objects, to redraw the GUI on move/collection~~ No longer needed, GUI polls garbage position as needed

###Multi-robot control

+ Assign tasks to robots
+ Collision detection and avoidance between robots


Note: Syntax used is markdown. For more info: http://daringfireball.net/projects/markdown/syntax
