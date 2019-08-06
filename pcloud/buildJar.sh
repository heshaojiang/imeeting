#!/bin/sh
mvn validate clean compile package -DskipTests

#scp ./pig-modules/grg-meeting/target/grg-meeting.jar xguo@10.1.42.107:/home/xguo/
