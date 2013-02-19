#SEG TODO list

###GUI

+ Draw robots - separate buffered image, easier update[br]
+ Draw paths - separate buffered image, easier update
+ Draw garbage objects - separate buffered image, easier update
+ Update object locations - see above
+ Save floor plan to an image - Decide between PNG (transparency), or JPEG

###Robot

+ Blocking move() methods (do not return from method until target reached, or move cancelled), without freezing the GUI (separate thread?)
+ Extend the Robot class to be Observable
+ Subscribe the GUI to listen to Robot(s) for updates of robot position

###Exploration

+ Explore garbage objects, not just the floor plan
+ Do not map other robots as occupied space

###Path-finding

+ Remove unneeded nodes from path, to speed up robot navigation

###Garbage

+ Align robot with garbage
+ Pick/drop garbage
+ Create a garbage class to keep track of garbage objects
+ Extend class with Observable, to update observers on garbage move/collection
+ Subscribe GUI to Garbage objects, to redraw the GUI on move/collection

###Multi-robot control

+ Assign tasks to robots
+ Collision detection and avoidance between robots


Note: Syntax used is markdown. For more info: http://daringfireball.net/projects/markdown/syntax
