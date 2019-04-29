#!/bin/bash
## expecting 2 arguments like:
## dev1 udp://127.0.0.1:14551@14555
if [ $# -eq 1 ]; then
  echo "Launching mavros:"
  #echo "  Sourcing ros.. "
  #source /opt/ros/kinetic/setup.bash
  #echo "  Sourcing rosjava.. "
  #source /home/tommy/prj_ws/devel/setup.bash
  #echo "  ROS_PACKAGE_PATH: $ROS_PACKAGE_PATH"
  #export ROS_IP=$3
  #echo "  ROS_IP=$ROS_IP"

  #TGT_SYSTEM=$(($4+1))
  #MAVROS_CMD="rosrun mavros mavros_node __ns:=$1 _fcu_url:=$2 _tgt_system:=$TGT_SYSTEM"
  LAUNCH_FILE_PATH="/home/tommy/vehicles/mavros/dev.launch"
  MAVROS_CMD="roslaunch $LAUNCH_FILE_PATH dev_num:=$1"
  echo "  Running mavros: $MAVROS_CMD"
  $MAVROS_CMD &> /home/tommy/prj_ws/src/prj_pkg/prj/logs/dev$1_mavros.log
else
  echo "Your command contains $# arguments, needed 1.. exit"
fi

