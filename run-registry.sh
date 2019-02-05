#!/usr/bin/env bash
# Define CLASSPATH for rmiregistry: https://stackoverflow.com/a/23643744
rmiregistry.exe -J-Djava.class.path=./target/shared.jar &
echo RMI Registry has been started
