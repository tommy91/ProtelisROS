#!/bin/bash

## For usage see the help option
## Note: the upper case variables are the ones you can edit

## Default variables
SYSTEM_ID="1"
NAMESPACE_PREFIX="prj_device_"
VEHICLE_TYPE="copter"
HOW_MANY_DEVICES="1"
ROS_IP="127.0.0.1"
MASTER_PORT="11311"

LAUNCHFILE_MAVROS="mavros.launch"
LAUNCHFILE_PRJ="prj.launch"
LAUNCHFILE_ADHOC_COMMUNICATION="adhoc_communication.launch"

LOGS_DIRECTORY="logs"
LOG_MAVROS="mavros.log"
LOG_PRJ="prj.log"
LOG_MASTER="master.log"
LOG_ADHOC_COMMUNICATION="adhoc_communication.log"

PIDS_DIRECTORY="pids"

ROBOT_NAME_PREFIX="robot_"
INTERFACE="lo"

## Edit this variables to choose what to remove when using 'clean'
FILES_TO_REMOVE=()
DIRECTORIES_TO_REMOVE=($LOGS_DIRECTORY $PIDS_DIRECTORY)


getMacFromID() {
  local curr_id=$1
  local len_curr_id=$((12-${#curr_id}))
  for (( i=1; i<=$len_curr_id; i++ )); do
    curr_id="${curr_id}0"
  done
  curr_id=$(echo $curr_id | sed -r 's/.{2}/&:/g; s/.$//')
  echo "$curr_id"
}


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
    for f in $PIDS_DIRECTORY/*.pid; do
      echo "Killing nodes for $f:"
      while read LINE; do
        echo -n "  Killing PID $LINE.. "
        kill $LINE
        if [ "$?" -eq "0" ] ; then
          echo "ok"
        fi
      done < $f
    done
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


## Checking the logs directory
if [ ! -d $LOGS_DIRECTORY ]; then
  echo -n "Creating logs directory '$LOGS_DIRECTORY'.. "
  mkdir -p $LOGS_DIRECTORY
  echo -e "ok\n"
fi
## Checking the pids directory
if [ ! -d $PIDS_DIRECTORY ]; then
  echo -n "Creating pids directory '$PIDS_DIRECTORY'.. "
  mkdir -p $PIDS_DIRECTORY
  echo -e "ok\n"
fi


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


last_device_num=$(($SYSTEM_ID + $HOW_MANY_DEVICES - 1))


## If multiple launch setup simulated macs
sim_macs=""
if [ "$HOW_MANY_DEVICES" -gt "1" ]; then
  sim_macs="sim_robot_macs:="
  first_time=true
  for i in $(seq $SYSTEM_ID $last_device_num) 
  do
    curr_mac=$( getMacFromID $i )
  	if [ "$first_time" = true ]; then
  	  first_time=false
  	  sim_macs="$sim_macs$ROBOT_NAME_PREFIX$i,$curr_mac"
  	else
  	  sim_macs="$sim_macs!$ROBOT_NAME_PREFIX$i,$curr_mac"
  	fi
  done
fi  


if [ "$HOW_MANY_DEVICES" -eq "1" ] ; then
  echo -e "Launching 1 $VEHICLE_TYPE device with system ID $SYSTEM_ID\n"
else
  echo -e "Launching $HOW_MANY_DEVICES $VEHICLE_TYPE devices with system IDs $SYSTEM_ID..$last_device_num\n"
fi

## Running the needed devices
for current_device_num in $(seq $SYSTEM_ID $last_device_num) 
do
  echo -e "Launching the nodes for device ID $current_device_num\n"
  namespace="$NAMESPACE_PREFIX$current_device_num"
  current_log_directory="$LOGS_DIRECTORY/$namespace"
  pids_filename="$PIDS_DIRECTORY/$namespace.pid"
  
  ## Checking the log directory for this namespace
  if [ ! -d $current_log_directory ]; then
    echo -n "Creating log directory '$current_log_directory' for '$namespace'.. "
    mkdir -p $current_log_directory
    echo -e "ok\n"
  fi
  
  ## NOTE: for roslaunch
  ## use --wait to delay the launch until a roscore is detected
  ## use -p if you launched roscore on a different port using the -p option
  
  mavros_log_file="$current_log_directory/$LOG_MAVROS"
  mavros_cmd="roslaunch --wait prj_pkg mavros.launch system_id:=$current_device_num namespace:=$namespace"
  echo -e "Running mavros: $mavros_cmd"
  $mavros_cmd &>> $mavros_log_file 2>&1 &
  echo $! >> $pids_filename
  echo "PID: $!"
  echo -e "Log mavros to: $mavros_log_file\n"
  
  prj_log_file="$current_log_directory/$LOG_PRJ"
  prj_cmd="roslaunch --wait prj_pkg prj.launch system_id:=$current_device_num vehicle_type:=$VEHICLE_TYPE namespace:=$namespace"
  echo -e "Running prj node: $prj_cmd"
  $prj_cmd &>> $prj_log_file 2>&1 &
  echo $! >> $pids_filename
  echo -e "PID: $!"
  echo -e "Appending log to: $prj_log_file\n"
  
  robot_name="$ROBOT_NAME_PREFIX$current_device_num"
  mac=$( getMacFromID $current_device_num )
  adhoc_communication_log_file="$current_log_directory/$LOG_ADHOC_COMMUNICATION"
  adhoc_communication_cmd="roslaunch --wait prj_pkg adhoc_communication.launch interface:=$INTERFACE namespace:=$namespace robot_name:=$robot_name mac:=$mac robots_in_simulation:=$HOW_MANY_DEVICES $sim_macs"
  echo -e "Running adhoc_communication node: $adhoc_communication_cmd"
  $adhoc_communication_cmd &>> $adhoc_communication_log_file 2>&1 &
  echo $! >> $pids_filename
  echo -e "PID: $!"
  echo -e "Appending log to: $adhoc_communication_log_file\n"
done