# Define CLASSPATH for rmiregistry: https://stackoverflow.com/a/23643744
Start-Process rmiregistry.exe "-J-Djava.class.path=compute.jar"

#java -cp ".;compute.jar" "-Djava.security.policy=.\server.policy" engine.ComputeEngine
Start-Process -FilePath java -ArgumentList '-cp ".;compute.jar" "-Djava.security.policy=.\server.policy" engine.ComputeEngine'

java -cp ".;compute.jar" "-Djava.security.policy=.\client.policy" client.ComputePi localhost 50
