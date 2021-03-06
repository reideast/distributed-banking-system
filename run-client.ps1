# The client should have its own files and shared interfaces.
# It gets those interfaces which are implemented on the server from the codebase feature
# This powershell script supports adding in arguments from the prompt
#     So, run this script as: `.\run-client.ps1 login user pass`, `.\run-client.ps1 inquiry 100`, etc.
java -cp "target\client.jar;target\interfaces.jar;target\shared-from-client.jar" "-Djava.rmi.server.codebase=file:target\shared-from-server.jar" "-Djava.security.policy=.\target\classes\client.policy" net.teamtrycatch.client.ATM localhost 7777 $args
