#!/bin/bash

## Accepted options:
## -h|--help		to print help and exit
## -c|--clear		to clear the folder removing NON-default files
## -d|--device		followed by the system ID of the device (1:255) 
##					or system ID of the first device if multiple launch]
## -m|--multiple	followed by the number of devices to launch
##					[they will have a progressive system ID]
## -k|--kill		to kill all the backgroud processes 
##					from the previous execution


## Note: the upper case variables are the ones you can edit

## Default variables
SYSTEM_ID="1"
HOW_MANY_DEVICES="1"
MULTIPLE_LAUNCH=false
MAVROS_LAUNCHFILE=mavros.launch
LOG_DIRECTORY=logs/
MAVROS_LOG_PREFIX="mavros_"
PRJ_LOG_FILENAME="prj.log"
prj_log_file="$LOG_DIRECTORY$PRJ_LOG_FILENAME"
PID_BACKGROUND_PROCESSES_DUMP=pid_dump.pid


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
    echo "  -d|--device		followed by the system ID of the device (1:255)"
    echo "				    [or system ID of the first device if multiple launch]"
    echo "  -m|--multiple	followed by the number of devices to launch"
    echo "					[they will have a progressive system ID]"
    echo "  -k|--kill		to kill all the backgroud processes"
    echo "					from the previous execution"
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
    -d|--device)
    SYSTEM_ID="$2"
    shift # pass the key 
    shift # pass the value
    ;;
    -m|--multiple)
    HOW_MANY_DEVICES="$2"
    if [ "$HOW_MANY_DEVICES" -lt "1" ] ; then
      echo "Needed at least 1 device to run"
      exit 1
    fi
    shift
    shift
    ;;
    -k|--kill)
    while read LINE; do
      echo -n "Killing PID $LINE.. "
      kill $LINE
      if [ "$?" -eq "0" ] ; then
        echo "ok"
      fi
    done < $PID_BACKGROUND_PROCESSES_DUMP
    exit 0
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


## Clear the pid dump file
:> $PID_BACKGROUND_PROCESSES_DUMP
echo -e "PIDs dump in $PID_BACKGROUND_PROCESSES_DUMP\n"


## Clear the prj log file
:> $prj_log_file


## Running the needed devices
last_device_num=$(($SYSTEM_ID + $HOW_MANY_DEVICES - 1))

if [ "$HOW_MANY_DEVICES" -eq "1" ] ; then
  echo -e "Launching 1 device with system ID $SYSTEM_ID\n"
else
  echo -e "Launching $HOW_MANY_DEVICES with system IDs $SYSTEM_ID..$last_device_num\n"
fi

for current_device_num in $(seq $SYSTEM_ID $last_device_num) 
do
  echo -e "Launching the prj node: (device number $current_device_num)\n"

  #mavros_log_file=$LOG_DIRECTORY$MAVROS_LOG_PREFIX$current_device_num.log

  #mavros_cmd="roslaunch mavros.launch system_id:=$current_device_num"
  #echo -e "Running mavros: $mavros_cmd"
  #$mavros_cmd &> $mavros_log_file &
  #echo $! >> $PID_BACKGROUND_PROCESSES_DUMP
  #echo "PID: $!"
  #echo -e "Log mavros to: $mavros_log_file\n"

  prj_cmd="roslaunch prj.launch system_id:=$current_device_num"
  echo -e "Running prj node: $prj_cmd"
  $prj_cmd &>> $prj_log_file 2>&1 &
  echo $! >> $PID_BACKGROUND_PROCESSES_DUMP
  echo -e "PID: $!"
  echo -e "Appending log to: $prj_log_file\n"
done