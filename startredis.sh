#!/bin/bash
docker run -d -p 6379:6379 --name redis-db redis:7
echo docker rm -f redis-db, to remove the container again
