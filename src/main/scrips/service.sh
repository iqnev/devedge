#!/bin/bash
#
# init script for a Java application
#

#Colors
RED='\033[1;31m'
GREEN='\033[1;32m'
YELLOW='\033[1;33m'
BLUE='\033[1;34m'
NC='\033[0m' #No Color

# Check the application status
#
# This function checks if the application is running
check_status() {
 check_java
 
  # Running ps with some arguments to check if the PID exists
  # -C : specifies the command name
  # -o : determines how columns must be displayed
  # h : hides the data header
  s=`ps -C 'java -jar c8y-4-devteams-agent-0.0.1-SNAPSHOT-jar-with-dependencies.jar start' -o pid h`

  # If somethig was returned by the ps command, this function returns the PID
  if [ $s ] ; then
    return $s
  fi

  # In any another case, return 0
  return 0

}

# Starts the application
start() {

  # At first checks if the application is already started calling the check_status
  # function
  pid="$(check_status)"

  if [ "$pid" != 0 ] ; then
    echo "The application is already started"
    exit 1
  fi

  # If the application isn't running, starts it
  echo -n "Starting application: "

  # Redirects default and error output to a log file
  nohup java -jar c8y-4-devteams-agent-0.0.1-SNAPSHOT-jar-with-dependencies.jar start >> logfile.log 2>&1 &
  echo "OK"
}

# Stops the application
stop() {

  # Like as the start function, checks the application status
  pid="$(check_status)"

  pid=$?
echo -e"PIIII"
  if [ "$pid" == 0 ] ; then
    echo "Application is already stopped"
    exit 1
  fi

  # Kills the application process
  echo -n "Stopping application with PID: $pid"
  kill -9 $pid &
  echo "OK"
}

# Show the application status
status() {
  # The check_status function, again...
  pid="$(check_status)"
  
  # If the PID was returned means the application is running
  if [ "$pid" != 0 ] ; then
    echo "Application is started with PID: ${pid}"
  else
    echo "Application is stopped"
  fi

}


#verify that java is available
check_java() {
	JAVA_VERSION=`echo "$(java -version 2>&1 >/dev/null)" | grep 'java version' | awk '{print $3}'`

	if [ $JAVA_VERSION ] ; then
	     echo -e "${GREEN} Java version($JAVA_VERSION) is installed"
	else
		echo -e "${RED} Java is not availible"
   		exit 1
	fi
}

# Main logic, a simple case to call functions
case "$1" in
  start)
    start
    ;;
  stop)
    stop
    ;;
  status)
    status
    ;;
  restart|reload)
    stop
    start
    ;;
  *)
    echo -e "${RED}Invalid arguments${NC}"
    echo -e "Usages: $0 ${BLUE}{ start | stop | restart | status }${NC}"
    exit 1
esac

exit 0