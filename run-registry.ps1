# The registry should have the shared remote interfaces in its CLASSPATH, but no other compiled files
#     How to Define CLASSPATH for rmiregistry: https://stackoverflow.com/a/23643744
Write-Host "Starting RMI Registry"
rmiregistry.exe 7777 "-J-Djava.class.path=target\interfaces.jar"
