#!/bin/bash
echo "stopping smpl-blog test"
sudo systemctl stop smpl-blog-test.service
cd /media/Data/Blog/dev/smpl-blg || exit
git pull
mvn clean install
echo "cp to test target"
cp backend/target/backend-0.0.1-SNAPSHOT.jar ../../test/app/
echo "starting smpl-blog test"
sudo systemctl start smpl-blog-test.service