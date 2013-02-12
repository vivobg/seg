define garbage-lowvalue position
(
    size [0.2 0.2 0.2]
    fiducial_return 5
    color "green"
    boundary 1
    obstacle_return 0
    gripper_return 1
    ranger_return 1
  
    block
    (
        points 4
        point[0] [0 0]
        point[1] [0 0.2]
        point[2] [0.2 0.2]
        point[3] [0.2 0]
        z [0 0.2]
    )
)

define garbage-medvalue position
(
    size [0.2 0.2 0.2]
    fiducial_return 6
    color "brown"
    boundary 1
    obstacle_return 0
    gripper_return 1
    ranger_return 1
  
    block
    (
        points 4
        point[0] [0 0]
        point[1] [0 0.2]
        point[2] [0.2 0.2]
        point[3] [0.2 0]
        z [0 0.2]
    )
)

define garbage-highvalue position
(
    size [0.2 0.2 0.2]
    fiducial_return 7
    color "orange"
    boundary 1
    obstacle_return 0
    gripper_return 1
    ranger_return 1
  
    block
    (
        points 4
        point[0] [0 0]
        point[1] [0 0.2]
        point[2] [0.2 0.2]
        point[3] [0.2 0]
        z [0 0.2]
    )
)
