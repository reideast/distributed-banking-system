# Distributed Banking System

A project to learn Java RMI by James Quaife @SecretAgentx and Andrew Reid East @reideast

Simulates ATM clients which talk to a Bank server over RMI, with login/password and session tokens.

## Prerequisites

* Java 1.8
* Maven

## How to Run

### Windows

1. Start rmiregistry
   * `./run-registry.ps1`
   * This script includes the correct JAR in its classpath, as well as the port to run the registry on
   * Leave the registry running for all operations
   * Registry only needs to be restarted if any of the interfaces in the `net.teamtrycatch.shared` package are modified

1. Start Bank server
   * `./run-server.ps1`
   * The server will remain running, and only needs to be restarted if server is modified.

1. Start ATM client
   * `./run-client.ps1`
   * This batch file needs additional arguments to actually do any ATM operations. Add them as arguments when you run the script, such as:
     * `.\run-client.ps1 login user pass`
     * `.\run-client.ps1 inquiry 100`
     * ...and so on
   * The client will run once and then quit.

### Linux/MacOS

Bash scripts (`*.sh`) with the same functionality as above have been created.
However,these have only been tested in the Cygwin bash shell (Win-64).

### Test Script

There exists a `test-script` file (both Windows and Linux), which will start the registry, server,
and then run a series of client ATM operations, which we believe cover most normal operations and edge cases.

## How to Test

The server is built with several mock accounts to test with:

1. Account 100, `username1`, `password1`, three transactions in 2018
1. Account 200, `username2`, `password2`, four transactions in 2016, two in 2018
1. Account 300, `usernmae3`, `password3`, initial transaction of €0 at current date/time

## How to Build

Run the Maven phase package:

```mvn package```

Whenever any changes to the client or server are made,
run this Maven package command to compile, run tests,
and then rebuild those JARs, which the below scripts will utilise to run.

## Developer Workflow Summary

1. Modify code
1. `mvn package`
1. [Optional] If "shared" classes were modified: `./run-registry`
1. [Optional] If "server" classes were modified: `./run-server`
1. `./run-client` [desired operation] [args]

