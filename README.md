eras3r
==

[![eras3r native build](https://github.com/AdamBien/eras3r/actions/workflows/main.yml/badge.svg)](https://github.com/AdamBien/eras3r/actions/workflows/main.yml)

eras3r removes a S3 bucket with all its contents. Also works on buckets with versioned objects.

# usage

removes bucket contents:

`eras3r [bucketname]`


removes the bucket:

`eras3r [bucketname] [--remove-bucket]`

or

`java -jar target/eras3r.jar [bucketname] [--remove-bucket]`

the argument: `--remove-bucket` will also remove the bucket

# build

change to the `eras3r` directory and perform:

`mvn clean package`
