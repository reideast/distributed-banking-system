#!/usr/bin/env bash
rmiregistry.exe -J-Djava.class.path=./target/interfaces.jar &
echo RMI Registry has been started
java -cp "target/server.jar:target/interfaces.jar:target/shared-from-server.jar" -Djava.rmi.server.codebase=file:target/shared-from-server.jar -Djava.security.policy=./target/classes/server.policy net.teamtrycatch.server.Bank 7777 &
echo Bank server has been started

echo
echo "Please press enter after the server has started up: "
echo
read -p "Press enter:"

function client {
    java -cp "./target/client.jar:./target/interfaces.jar:./target/shared-from-client.jar" -Djava.rmi.server.codebase=file:target/shared-from-server.jar -Djava.security.policy=./target/classes/client.policy net.teamtrycatch.client.ATM localhost 7777 $@
}

echo
echo "Attempting operation before logging in: should fail"
client inquiry 100

echo
echo "Good sequence: login, get balance, deposit, withdraw, and get a statement"
client login username3 password3
client inquiry 300
client deposit 300 1000
client withdraw 300 30
client statement 300 01/01/2019 $(date --date="tomorrow" +%d/%m/%Y)  # Create a date range from start of 2019 to tomorrow

echo
echo "Good sequence, switching accounts: login user 1, make deposit, login user 2, inquiry"
client login username1 password1
client deposit 100 1500
client login username2 password2
client inquiry 200

echo
echo "Incorrect username/password, then attempt an operation"
client login hacker letmein123
client inquiry 100

echo
echo "Correct login, then do an operation with someone else's account number, and show that session is now invalidated"
client login username3 password3
client inquiry 100  # Incorrect account number for user3
client inquiry 300  # Correct account number, but session is now invalid

echo
read -p "The next command will sleep for 5 minutes to simulate a timeout. Press Ctrl+C now skip this, or Enter to continue:"

echo
echo "Show session timeout: Login again, do an operation. Wait five minutes (!) and show another operation fails"
client login username2 password2
client inquiry 200
echo Sleeping for $((60 * 5 + 10)) seconds
sleep $((60 * 5 + 10))
#echo Sleeping for 5 seconds
#sleep 5
client inquiry 200  # Session expired error

