# Distributed Banking System

A project to learn Java RMI

Simulates ATM clients which talk to a Bank server over RMI, with login/password and session tokens.

## Prerequisites

* Java 1.8
* Maven

## How to build

Run Maven phase package:

`mvn package`

Whenever any changes to the client or server are made, the Maven package command will compile, run tests, and then rebuild those JARs, which the below scripts will utilise to run.

## How to Run

### Windows

1. Start rmiregistry
   * `./run-registry.ps1`
   * Leave the registry running for all operations
   * Registry only needs to be restarted if any of the interfaces in the `net.teamtrycatch.shared` package are modified

1. Start Bank server
   * `./run-server.ps1`
   * The server will remain running, and only needs to be restarted if server is modified.

1. Start ATM client
   * `./run-client.ps1`
   * This batch file has no additional arguments to actually do any ATM operations. They could be added at the end of the line to test various operations
   * The client will run once and then quit.

### Linux/MacOS

TODO

### Developer Workflow Summary

1. Modify code
1. `mvn package`
1. [Optional] If "shared" classes were modified: `./run-registry`
1. [Optional] If "server" classes were modified: `./run-server`
1. `./run-client`

## Test Script

There exists a `test-script` file, which will start the registry, server, and then run a series of client ATM operations, which we believe cover most normal operations and edge cases
