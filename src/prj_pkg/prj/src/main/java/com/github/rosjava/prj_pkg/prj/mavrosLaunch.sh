#!/bin/bash
## expecting 2 arguments like:
## dev1 udp://127.0.0.1:14551@14555
if [ $# -eq 3 ]; then
  echo "Launching mavros:"
  #echo "  Sourcing ros.. "
  #source /opt/ros/kinetic/setup.bash
  #echo "  Sourcing rosjava.. "
  #source /home/tommy/prj_ws/devel/setup.bash
  #echo "  ROS_PACKAGE_PATH: $ROS_PACKAGE_PATH"
  export ROS_IP=$3
  #echo "  ROS_IP=$ROS_IP"
  echo "  Running mavros: rosrun mavros mavros_node $1 $2"
  rosrun mavros mavros_node __ns:=$1 _fcu_url:=$2 &> /home/tommy/prj_ws/src/prj_pkg/prj/logs/$1_mavros.log
else
  echo "Your command contains $# arguments, needed 3.. exit"
fi

