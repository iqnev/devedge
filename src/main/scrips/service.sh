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
APP="c8y-4-devteams-agent-0.0.1-SNAPSHOT-jar-with-dependencies"
PGREP=/usr/bin/pgrep
ZERO=0



# Starts the application
start() {
	echo ""		
    echo "Starting $APP..."
    check_java
    #Verify if the service is running
    $PGREP -f $APP > /dev/null
    VERIFIER=$?
    if [ $ZERO -eq $VERIFIER ]
    then
        echo -e "The application is ${YELLOW}already${NC} running"
    else
        # Redirects default and error output to a log file
  		nohup java -jar "${APP}.jar" start >> logfile.log 2>&1 &
        #sleep time before the service verification
        sleep 3
        #Verify if the service is running
        $PGREP -f $APP  > /dev/null
        VERIFIER=$?
        if [ $ZERO -eq $VERIFIER ]
        then
            echo -e "Apllication was ${GREEN}successfully${NC} started"
        else
            echo -e "${RED}Failed${NC} to start application"
        fi
    fi
    echo
}

# Stops the application
stop() {
	echo ""		
    echo "Stopping $APP..."
    #Verify if the service is running
    $PGREP -f $APP > /dev/null
    VERIFIER=$?
    if [ $ZERO -eq $VERIFIER ]
    then        
		#Kill the pid of java with the application name
		kill -9 $($PGREP -f $APP)
		#Sleep time before the application verification
		sleep 3
		#Verify if the service is running
		$PGREP -f $APP  > /dev/null
		VERIFIER=$?
		if [ $ZERO -eq $VERIFIER ]
		then
			echo -e "${RED}Failed${NC} to stop application"
		else
			echo -e "Application was ${GREEN}successfully${NC} stopped"
		fi        
    else
        echo -e "The application is ${YELLOW}already${NC} stopped"
    fi
    echo
}

# Show the application status
status() {
	echo ""		
    echo "Checking status of $APP ..."
    #Verify if the application is running
    $PGREP -f $APP > /dev/null
    VERIFIER=$?
    if [ $ZERO -eq $VERIFIER ]
    then
        echo -e "Application is ${GREEN}running${NC}"        
    else
        echo -e "Application is ${RED}stopped${NC}"
    fi
    echo
}


#verify that java is available
check_java() {
        JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')
        #echo $JAVA_VERSION
        if [ $JAVA_VERSION ] ; then
             echo -e "${GREEN} Java version($JAVA_VERSION) is installed!!"
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