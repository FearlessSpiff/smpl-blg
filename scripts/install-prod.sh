#!/bin/bash
sudo systemctl stop smpl-blog.service
cd /media/Data/Blog/dev/smpl-blg || exit
git pull
mvn clean install
cp backend/target/backend-0.0.1-SNAPSHOT.jar ../../prod/app/
sudo systemctl start smpl-blog.service