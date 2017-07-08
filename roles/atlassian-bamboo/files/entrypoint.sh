#!/bin/bash

trap `pkill -SIGTERM java` SIGTERM

if [ "$3" = "/opt/bamboo/install/bin/start-bamboo.sh -fg" ]
  then
    chown -R bamboo:bamboo /opt/bamboo
    set -- gosu bamboo "$3"
fi
# Starting application now

exec $@
