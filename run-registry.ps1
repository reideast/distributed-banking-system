# The registry should have the shared remote interfaces in its CLASSPATH, but no other compiled files
#     How to Define CLASSPATH for rmiregistry: https://stackoverflow.com/a/23643744
rmiregistry.exe "-J-Djava.class.path=target\interfaces.jar"
Write-Host "RMI Registry has been started"
