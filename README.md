# imgur-image-uploader
Imgur image uploading service

#### Steps to create docker image
Run the following command from the root of the project where `build.sbt` is present
```bash
sbt docker:publishLocal
``` 
This will create a docker image with Repository `imgur-image-uploader` and TAG `1.0-SNAPSHOT`

#### Starting the docker image
To start the docker image run the following
```bash
docker run --rm -p9000:9000 <IMAGE-ID>
```

#### Starting without the docker
To start the application server without docker, run the following:
```bash
sbt run <PORT>
```