#!/bin/sh
buildVersion=$1
echo -----------------------------------------------------------------------
echo to be compile adminUI ${buildVersion}
echo -----------------------------------------------------------------------
BUILD_PATH=dist
OUTPUT_PATH=output
rm -rf ${BUILD_PATH}
rm -rf ${OUTPUT_PATH}
mkdir -p ${OUTPUT_PATH}

echo install dependence!
yarn install

var=$(svn info  | awk '/^Revision:/{print $2}')
dt=$(date +%Y%m%d)
tarname="iMeeting_Console-"$dt"-r"$var".tar.gz"
echo start build!
npm run build

#create version.txt
if [ -n "${buildVersion}" ] ; then
echo version:${buildVersion} > ${BUILD_PATH}/version.txt
echo PackageTime:${dt} >> ${BUILD_PATH}/version.txt
echo svn:${var} >> ${BUILD_PATH}/version.txt
fi

echo tar files!
tar zcf ${OUTPUT_PATH}/$tarname -C ${BUILD_PATH} . --exclude=.svn

echo -----------------------------------------------------------------------
echo adminUI compile completed!
echo -----------------------------------------------------------------------