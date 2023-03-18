#!/bin/sh
mvn clean package
native-image -jar target/eras3r.jar