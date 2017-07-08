#!/bin/bash

trap `pkill -SIGTERM java` SIGTERM

if [ "$3" = "/opt/jira/install/bin/start-jira.sh -fg" ]
  then
    if [ -d "/opt/jira/config" ]
      then
        /opt/jira/config/ssl/install.sh
        cd /opt/jira/config 
        ./run.groovy || exit 1
        cd -
    fi
    chown -R jira:jira /opt/jira
    set -- gosu jira "$3"
fi
# Starting application now

exec $@
