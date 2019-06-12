#!/bin/bash

## Accepted options:
## -h|--help		to print help and exit
## -c|--clear		to clear the folder removing NON-default files
## -i|--id			followed by the system ID of the device (1:255) 
##					[or system ID of the first device if multiple launch]
## -d|--devices		followed by the number of devices to launch
##					[they will have a progressive system ID]
## -k|--kill		to kill all the backgroud processes 
##					from the previous execution
## -v|--vehicle	to specify the vehicle type [default: copter]


## Note: the upper case variables are the ones you can edit

## Default variables
SYSTEM_ID="1"
VEHICLE_TYPE="copter"
HOW_MANY_DEVICES="1"
ROS_IP=127.0.0.1
MASTER_PORT="11311"
MAVROS_LAUNCHFILE=mavros.launch
LOG_DIRECTORY=logs/
LOG_PREFIX_MAVROS="mavros_"
LOG_FILENAME_PRJ="prj.log"
LOG_FILENAME_MASTER="master.log"
log_file_prj="$LOG_DIRECTORY$LOG_FILENAME_PRJ"	## All the prj nodes log to one single file
log_file_master="$LOG_DIRECTORY$LOG_FILENAME_MASTER"
DUMP_PID_NODES=dump_pid_nodes.pid
DUMP_PID_MAVROS=dump_pid_mavros.pid
DUMP_PID_MASTERS=dump_pid_masters.pid


## Edit this variables to choose what to remove when using 'clean'
FILES_TO_REMOVE=()
DIRECTORIES_TO_REMOVE=("logs")


## Parsing the options
while [[ $# -gt 0 ]]
do
  key="$1"  
  case $key in
    -h|--help)
    echo "Options: (except '-h', all the others are needed!)"
    echo "  -h|--help	    to print help and exit"
    echo "  -c|--clear		to clear the folder removing NON-default files"
    echo "  -i|--id  		followed by the system ID of the device (1:255)"
    echo "				    [or system ID of the first device if multiple launch]"
    echo "  -d|--devices	followed by the number of devices to launch"
    echo "					[they will have a progressive system ID]"
    echo "  -k|--kill		to kill all the backgroud processes"
    echo "					from the previous execution"
    echo "  -v|--vehicle	to specify the vehicle type [default: copter]"
    exit 0
    ;;
    -c|--clear)
    echo "Cleaning the folder:"
    cleaned_something=false
    for file_to_remove in ${FILES_TO_REMOVE[*]}
    do 
      if [ -e "$file_to_remove" ] ; then 
        echo -n "  - removing '$file_to_remove'.. "
        rm $file_to_remove
        echo "ok" 
        cleaned_something=true
      fi
    done
    for directory_to_remove in ${DIRECTORIES_TO_REMOVE[*]}
    do 
      if [ -e "$directory_to_remove" ] ; then 
        echo -n "  - removing '$directory_to_remove/'.. "
        rm -r $directory_to_remove
        echo "ok"
        cleaned_something=true
      fi
    done
    if [ "$cleaned_something" = false ] ; then
      echo "  Nothing to remove, the folder is already clean!"
    fi
    exit 0
    ;;
    -i|--id)
    SYSTEM_ID="$2"
    shift # pass the key 
    shift # pass the value
    ;;
    -d|--devices)
    HOW_MANY_DEVICES="$2"
    if [ "$HOW_MANY_DEVICES" -lt "1" ] ; then
      echo "Needed at least 1 device to run"
      exit 1
    fi
    shift
    shift
    ;;
    -k|--kill)
    echo "Killing Nodes:"
    while read LINE; do
      echo -n "Killing PID $LINE.. "
      kill $LINE
      if [ "$?" -eq "0" ] ; then
        echo "ok"
      fi
    done < $DUMP_PID_NODES
    echo "Killing Mavros:"
    while read LINE; do
      echo -n "Killing PID $LINE.. "
      kill $LINE
      if [ "$?" -eq "0" ] ; then
        echo "ok"
      fi
    done < $DUMP_PID_MAVROS
    ##echo "Killing Masters:"
    ##while read LINE; do
    ##  echo -n "Killing PID $LINE.. "
    ##  kill $LINE
    ##  if [ "$?" -eq "0" ] ; then
    ##    echo "ok"
    ##  fi
    ##done < $DUMP_PID_MASTER
    exit 0
    ;;
    -v|--vehicle)
    VEHICLE_TYPE="$2"
    shift
    shift
    ;;
    *)
    echo "Invalid argument '$1'.. exit.."
    exit 1
  esac
done


## Checking the log directory
if [ ! -d $LOG_DIRECTORY ]; then
  echo -n "Creating log directory '$LOG_DIRECTORY'.. "
  mkdir -p $LOG_DIRECTORY
  echo -e "ok\n"
fi


## Clear the pid dump files
##:> $DUMP_PID_MASTER
##echo "Master PID dump in $DUMP_PID_MASTER"
:> $DUMP_PID_MAVROS
echo "Mavros PID dump in $DUMP_PID_MAVROS"
:> $DUMP_PID_NODES
echo -e "Nodes PID dump in $DUMP_PID_NODES\n"


## Exporting the ROS_IP
##export ROS_IP=$ROS_IP


## Running the master
##echo -e "Launching the master: (on port $MASTER_PORT)\n"
##master_cmd="roscore -p $MASTER_PORT" 
##echo -e "Running master: $master_cmd"
##$master_cmd &>> $log_file_master 2>&1 &
##echo $! >> $DUMP_PID_MASTER
##echo -e "PID: $!"
##echo -e "Appending log to: $log_file_master\n"


## Running the needed devices
last_device_num=$(($SYSTEM_ID + $HOW_MANY_DEVICES - 1))
if [ "$HOW_MANY_DEVICES" -eq "1" ] ; then
  echo -e "Launching 1 $VEHICLE_TYPE device with system ID $SYSTEM_ID\n"
else
  echo -e "Launching $HOW_MANY_DEVICES $VEHICLE_TYPE devices with system IDs $SYSTEM_ID..$last_device_num\n"
fi

for current_device_num in $(seq $SYSTEM_ID $last_device_num) 
do
  echo -e "Launching the prj node: (device ID $current_device_num)\n"
  
  ## NOTE: for roslaunch
  ## use --wait to delay the launch until a roscore is detected
  ## use -p if you launched roscore on a different port using the -p option
  
  mavros_log_file=$LOG_DIRECTORY$LOG_PREFIX_MAVROS$current_device_num.log
  mavros_cmd="roslaunch --wait prj_pkg mavros.launch system_id:=$current_device_num"
  echo -e "Running mavros: $mavros_cmd"
  $mavros_cmd &>> $mavros_log_file 2>&1 &
  echo $! >> $DUMP_PID_MAVROS
  echo "PID: $!"
  echo -e "Log mavros to: $mavros_log_file\n"

  prj_cmd="roslaunch --wait prj_pkg prj.launch system_id:=$current_device_num vehicle_type:=$VEHICLE_TYPE"
  echo -e "Running prj node: $prj_cmd"
  $prj_cmd &>> $log_file_prj 2>&1 &
  echo $! >> $DUMP_PID_NODES
  echo -e "PID: $!"
  echo -e "Appending log to: $log_file_prj\n"
done