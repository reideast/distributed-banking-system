#!/usr/bin/env bash
# The server should have its own files, shared interfaces, and finally those interfaces which it has implemented
# The codebase feature will be used to serve up those implemented interfaces
java -cp "target/server.jar:target/interfaces.jar:target/shared-from-server.jar" -Djava.rmi.server.codebase=file:target/shared-from-server.jar -Djava.security.policy=./target/classes/server.policy net.teamtrycatch.server.Bank 7777 &

