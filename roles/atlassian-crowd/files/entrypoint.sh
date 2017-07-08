#!/bin/bash

trap `pkill -SIGTERM java` SIGTERM

if [ "$3" = "/opt/crowd/install/start_crowd.sh" ]
  then
    chown -R crowd:crowd /opt/crowd
    set -- gosu crowd "$3"
fi
# Starting application now

exec $@
