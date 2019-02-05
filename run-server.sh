#!/usr/bin/env bash
java -cp "target/server.jar:target/shared.jar" -Djava.security.policy=./target/classes/server.policy net.teamtrycatch.server.Bank 7777 &
