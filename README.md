# smpl-blg

## How to run
* Checkout
* `mvn clean install`
* `java -Dspring.profiles.active=default -jar backend/target/backend-0.0.1-SNAPSHOT.jar`
* open `localhost:9666`

## How to dev
* Checkout
* run `BackendApplication`
* `cd frontend`
* `npm install`
* `npm run dev`

## How to nginx
Use something like this to proxypass to backend and to (optionally) serve the images from nginx directly:
```
location ~* ^/api/v1/images/.*\.(jpeg|jpg) {
    access_log off;
    expires 365d;

    rewrite ^/api/v1/images/(.*)$ /$1 break;
    root <<path/to/images>>;
} 

location / {
    proxy_pass http://<<backend-ip>>:<<port>>;
}
```
    
## TODO
* Fix exif data and show on web
* Add a download button for original image
* Show DownloadablePolaroid also on Mobile and set Size correctly
* Add paging and endless scroll