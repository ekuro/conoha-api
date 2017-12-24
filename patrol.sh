#!/bin/sh

/usr/bin/gradle -b /root/conoha-api/build.gradle -Dtenant=xxx -Dusername=xxx -Dpassword=xxx run
echo $(date "+%Y/%m/%d %H:%M:%S") " patrolled!"
