include "editable.inc"

define garbagegripper gripper
(
    paddle_size [0.66 0.3 0.4]
    paddle_state ["open" "close"]
    autosnatch 0
    size [0.4 0.45 0.3]
)

define fiducialsensor fiducial
(
    range_min 0.1
    range_max 5.0
    range_max_id 5.0
    fov 60
    ignore_zloc 0
    size [0.1 0.1 0.1]
)



# definition of the robot
define robot_base position (

   # actual size of robot
   size [0.5 0.5 0.4]
   
   # other parameters
   origin [0 0 0 0]
   gui_nose 1
   obstacle_return 1
   ranger_return 1
   blob_return 0
   fiducial_return 1
   localization "gps"
   localization_origin [0 0 0 0]

   # choose a differential drive for the robot
   drive "diff"    
)

define robot_with_sensors robot_base 
(
   robot_ranger( pose [0 0 -0.25 0] )
   garbagegripper( pose [0.4 0 -0.25 0] )
   fiducialsensor( pose [0.20 0 -0.25 0])
)

define garbagecollector robot_with_sensors
(
   # define the shape of the robot
   block
   (
      points 6
      point[0] [0.125 0.000]
      point[1] [0.500 0.000]
      point[2] [0.500 0.500]
      point[3] [0.125 0.500]
      point[4] [0.000 0.375]
      point[5] [0.000 0.125]
      z [0 0.4]
   )
)
