#!/bin/bash

trap `pkill -SIGTERM java` SIGTERM

if [ "$3" = "/opt/fecru/install/bin/fisheyectl.sh run" ]
  then
    chown -R fecru:fecru /opt/fecru
    set -- gosu fecru "$3"
fi
# Starting application now

exec $@
