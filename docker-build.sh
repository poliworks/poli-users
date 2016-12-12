#!/usr/bin/env bash

echo "Starting to build application to prod..."
lein ring uberjar
echo "Building docker image..."
docker build . -t leoiacovini/poli-users
echo "Done building docker image!"
echo "Now pushing to remote..."
docker push leoiacovini/poli-users
echo "Done, image pushed to leoiacovini/poli-users:latest"
