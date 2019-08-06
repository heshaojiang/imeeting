#!/bin/sh
buildVersion=$1
echo -----------------------------------------------------------------------
echo to be compile CMAppServer ${buildVersion}
echo -----------------------------------------------------------------------
BUILD_PATH=build
OUTPUT_PATH=output
rm -rf ${BUILD_PATH}
rm -rf ${OUTPUT_PATH}
mkdir -p ${BUILD_PATH}
mkdir -p ${OUTPUT_PATH}

var=$(svn info  | awk '/^Revision:/{print $2}')
dt=$(date +%Y%m%d)
tarname="iMeeting_AppServer-"$dt"-r"$var".tar.gz"

#删除license.CheckLicense的参数 仅在调试阶段使用此参数，发布编译时需要删除此行，不能通过传参修改此值
MeetingController=pig-modules/grg-meeting/src/main/java/com/github/pig/admin/controller/MeetingController.java
sed -i "/@Value(\"\${license.CheckLicense}\")/d" ${MeetingController}

echo start build!

mvn clean compile package -DskipTests -Dmaven.test.skip=true

#还原CheckLicense修改
svn revert ${MeetingController}


echo copy files!
cp -f grg-gateway/target/grg-gateway.jar ${BUILD_PATH}
cp -f pig-config/target/pig-config.jar ${BUILD_PATH}
cp -f pig-eureka/target/pig-eureka.jar ${BUILD_PATH}
cp -f pig-auth/target/pig-auth.jar ${BUILD_PATH}
cp -f pig-modules/grg-meeting/target/grg-meeting.jar ${BUILD_PATH}
cp -f pig-modules/grg-acd/target/grg-acd.jar ${BUILD_PATH}
cp -rf config ${BUILD_PATH}/

#create version.txt
if [ -n "${buildVersion}" ] ; then
echo version:${buildVersion} > ${BUILD_PATH}/version.txt
echo PackageTime:${dt} >> ${BUILD_PATH}/version.txt
echo svn:${var} >> ${BUILD_PATH}/version.txt
fi 

echo tar files!
tar zcf ${OUTPUT_PATH}/$tarname -C ${BUILD_PATH} . --exclude=.svn


echo -----------------------------------------------------------------------
echo CMAppServer compile completed!
echo -----------------------------------------------------------------------