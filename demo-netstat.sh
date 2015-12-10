#!/bin/sh
watch -d -n 1 'netstat -n -p tcp | grep 127 | grep 8080'
