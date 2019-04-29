#!/bin/bash

## Accepted options:
## -h|--help       to print help and exit
## -c|--clean      to clean the folder removing NON-default files
## -I|--instance   followed by the instance number (0:255)
## -a|--apmp       followed by the IP address of APM Planner
## -g|--gazebo     followed by the IP address of Gazebo
## -p|--position   followed by the home position of the uav


## Note: the upper case variables are the ones you can edit

## Default variables
DEVICE_NUM="0"
MODEL="+"
NEED_APMP=false
NEED_GAZEBO=false
NEED_POSITION=false


## Edit this variables to choose what to remove when using 'clean'
FILES_TO_REMOVE=("eeprom.bin"  "mav.parm"  "mav.tlog"  "mav.tlog.raw")
DIRECTORIES_TO_REMOVE=("logs"  "terrain")


## Parsing the options
while [[ $# -gt 0 ]]
do
  key="$1"  
  case $key in
    -h|--help)
    echo "Options: (except '-h', all the others are needed!)"
    echo "  -h|--help       to print help and exit"
    echo "  -I|--instance   followed by the instance number (0:255)"
    echo "  -a|--apmp       followed by the IP address of APM Planner"
    echo "  -g|--gazebo     followed by the IP address of Gazebo"
    echo "  -p|--position   followed by the home position of the uav"
    exit 0
    ;;
    -c|--clean)
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
    -I|--instance)
    DEVICE_NUM="$2"
    shift # pass the key 
    shift # pass the value
    ;;
    -a|--apmp)
    APMP_ADDR="$2"
    NEED_APMP=true
    shift
    shift
    ;;
    -g|--gazebo)
    GAZEBO_ADDR="$2"
    NEED_GAZEBO=true
    MODEL="gazebo-iris"
    shift
    shift
    ;;
    -p|--position)
    POSITION="$2"
    NEED_POSITION=true
    shift
    shift
    ;;
    *)
    echo "Invalid argument '$1'.. exit.."
    exit 1
  esac
done

    
echo -e "Launching SITL: (I$DEVICE_NUM)\n"
echo "use APM PLANNER = $NEED_APMP"
echo "use GAZEBO = $NEED_GAZEBO"
echo -e "custom POSITION = $NEED_POSITION\n"


shift=$(($DEVICE_NUM*10))

MASTER_MODE=tcp
MASTER_ADDR=127.0.0.1
MASTER_PORT=5760
master_shifted_port=$(($MASTER_PORT+$shift))
master=$MASTER_MODE:$MASTER_ADDR:$master_shifted_port

SITL_ADDR=127.0.0.1
SITL_PORT=5501
sitl_shifted_port=$(($SITL_PORT+$shift))
sitl=$SITL_ADDR:$sitl_shifted_port

OUT_ADDR=127.0.0.1
OUT_PORT1=14550
OUT_PORT2=14551
out_shifted_port1=$(($OUT_PORT1+$shift))
out_shifted_port2=$(($OUT_PORT2+$shift))
out1=$OUT_ADDR:$out_shifted_port1
out2=$OUT_ADDR:$out_shifted_port2

APMP_PORT1=14550
APMP_PORT2=14551
apmp_out1=$APMP_ADDR:$APMP_PORT1
apmp_out2=$APMP_ADDR:$APMP_PORT2

LOG_DIRECTORY=../logs/
if [ ! -d $LOG_DIRECTORY ]; then
  echo -n "Creating log directory '$LOG_DIRECTORY'.. "
  mkdir -p $LOG_DIRECTORY
  echo -e "ok\n"
fi
log_file=$LOG_DIRECTORY$DEVICE_NUM.log
  
arducopter_path=~/ardupilot/build/sitl/bin/arducopter
  
default_params=~/ardupilot/Tools/autotest/default_params/copter.parm
gazebo_params=~/ardupilot/Tools/autotest/default_params/gazebo-iris.parm
PARAMS_DIRECTORY=params/
SYSID_PARAM_FILENAME=sysid.parm
sysid_param_filepath=$PARAMS_DIRECTORY$SYSID_PARAM_FILENAME

params_to_use=$default_params
## When using gazebo add its params
if [ "$NEED_GAZEBO" = true ] ; then 
  params_to_use=$params_to_use,$gazebo_params
fi
params_to_use=$params_to_use,$sysid_param_filepath


## Set the SYSID_THISMAV in the param file
thismav=$(($DEVICE_NUM+1))
echo -n "Set 'SYSID_THISMAV=$thismav' in the param file.. "
echo "SYSID_THISMAV    $thismav.000000" > $sysid_param_filepath
echo -e "ok\n"

## Build and execute the arducopter command and log its output to file
## such that mavproxy can have the screen to itself
arducopter_cmd="$arducopter_path -S -I$DEVICE_NUM --model $MODEL --speedup 1 --defaults $params_to_use"
if [ "$NEED_POSITION" = true ] ; then 
  arducopter_cmd="$arducopter_cmd --home $POSITION"
fi
if [ "$NEED_GAZEBO" = true ] ; then
  arducopter_cmd="$arducopter_cmd --sim-address $GAZEBO_ADDR"
fi
echo -e "Running arducopter:\n$arducopter_cmd\n"
$arducopter_cmd &> $log_file &

echo -e "Log arducopter to:\n$log_file\n"


## Build and execute the mavproxy command and leave its output to screen
mavproxy_cmd="mavproxy.py --master $master --sitl $sitl --out $out1 --out $out2 --console"
if [ "$NEED_APMP" = true ] ; then
  mavproxy_cmd="$mavproxy_cmd --out $apmp_out1 --out $apmp_out2"
fi
echo -e "Running mavproxy:\n$mavproxy_cmd\n"
$mavproxy_cmd

