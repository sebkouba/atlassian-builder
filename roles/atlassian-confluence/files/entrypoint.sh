#!/bin/bash

trap `pkill -SIGTERM java` SIGTERM

if [ "$3" = "/opt/confluence/install/bin/start-confluence.sh -fg" ]
  then
    if [ -f "/etc/apache2/httpd.conf" ]
      then
        httpd -f /etc/apache2/httpd.conf -k start
    fi
    if [ -d "/opt/confluence/config" ]
      then
        /opt/confluence/config/ssl/install.sh
        cd /opt/confluence/config
        ./run.groovy || exit 1
        cd -
    fi
    chown -R confluence:confluence /opt/confluence
    set -- gosu confluence "$3"
fi
# Starting application now

exec $@
