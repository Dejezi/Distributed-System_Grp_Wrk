Simple message sending [done]
Completed by having each user send their message to the sequencer using RMI and recieve other people's messages through multicast socket.


Stress-testing [done]

Run StressTest.java


Heartbeat messages [done]

The client sends to the sequencer every 10 seconds.


Marshalling [done]

This is done when sending the data through the socket.


History truncation [done]

Done by storing messages in created text file called backup.txt that is created everytime a server is started.


Recovery from simulated multicast datagram loss
