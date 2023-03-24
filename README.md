eraS3r
==

[![eras3r native build](https://github.com/AdamBien/eras3r/actions/workflows/main.yml/badge.svg)](https://github.com/AdamBien/eras3r/actions/workflows/main.yml)

eras3r removes a S3 bucket with all its contents. Also works on buckets with versioned objects.

# usage

`eras3r [bucketname] [RB!!!]`

or

`java -jar target/eras3r.jar [bucketname] [RB!!!]`

the argument: `RB!!!` will also remove the bucket

# build

change to the `eras3r` directory and perform:

`mvn clean package`