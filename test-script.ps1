Start-Process powershell.exe -Args .\run-registry.ps1
# rmiregistry.exe "-J-Djava.class.path=target\interfaces.jar"
Start-Process powershell.exe -Args .\run-server.ps1
# java -cp "target\server.jar;target\interfaces.jar" "-Djava.security.policy=.\target\classes\server.policy" net.teamtrycatch.server.Bank 7777

Write-Host "Please press enter after the server has started up: "
Pause

function client {
    java -cp "target\client.jar;target\interfaces.jar;target\shared-from-client.jar" "-Djava.rmi.server.codebase=file:target\shared-from-server.jar" "-Djava.security.policy=.\target\classes\client.policy" net.teamtrycatch.client.ATM localhost 7777 $args
}

Write-Host "`nAttempting operation before logging in: should fail"
client inquiry 100

Write-Host "`nGood sequence: login, get balance, deposit, withdraw, and get a statement"
client login username3 password3
client inquiry 300
client deposit 300 1000
client withdraw 300 30
#client statement 300 01/01/2019 (Get-Date -UFormat "%d/%m/%Y")  # Create a date range from start of 2019 to today
client statement 300 01/01/2019 ((Get-Date).AddDays(1).ToString("dd/MM/yyyy"))  # Create a date range from start of 2019 to tomorrow

Write-Host "`nGood sequence, switching accounts: login user 1, make deposit, login user 2, inquiry"
client login username1 password1
client deposit 100 1500
client login username2 password2
client inquiry 200

Write-Host "`nIncorrect username/password, then attempt an operation"
client login hacker letmein123
client inquiry 100

Write-Host "`nCorrect login, then do an operation with someone else's account number, and show that session is now invalidated"
client login username3 password3
client inquiry 100  # Incorrect account number for user3
client inquiry 300  # Correct account number, but session is now invalid

Write-host "`nThe next command will sleep for 5 minutes to simulate a timeout. Press Ctrl+C now skip this, or Enter to continue:"
Pause
Write-Host "`nShow session timeout: Login again, do an operation. Wait five minutes (!) and show another operation fails"
client login username2 password2
client inquiry 200
Write-Host "Sleeping for" (60 * 5 + 10) "seconds"
Start-Sleep -Seconds (60 * 5 + 10)
client inquiry 200  # Session expired error

