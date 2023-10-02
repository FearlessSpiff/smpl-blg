#!/bin/bash
echo "stopping smpl-blog"
sudo systemctl stop smpl-blog.service
cd /media/Data/Blog/dev/smpl-blg || exit
git pull
mvn clean install
echo "cp to prod target"
cp backend/target/backend-0.0.1-SNAPSHOT.jar ../../prod/app/
echo "starting smpl-blog"
sudo systemctl start smpl-blog.service