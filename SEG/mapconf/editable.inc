include "exploresensor.inc"

# You may edit the definition of robot_ranger.  
# You can change the position and direction of the exploresensors,
# add additional exploresensors or remove exploresensors.  Your 
# robot may possess up to 16 exploresensors (not more).  Do not
# remove the include line.

define robot_ranger ranger 
(
   exploresensor ( pose [ 0.125  0.000 0.1   0] )
   exploresensor ( pose [ 0.125  0.125 0.1   45] )
   exploresensor ( pose [ 0.000  0.125 0.1  90] )
   exploresensor ( pose [ -0.125  0.125 0.1  135] )
   exploresensor ( pose [ -0.125  -0.000 0.1 180] )
   exploresensor ( pose [-0.125  -0.125 0.1 225] ) 
   exploresensor ( pose [-0.000  -0.125 0.1 270] )
   exploresensor ( pose [0.125  -0.125 0.1 310] )
   
)
