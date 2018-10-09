import tcpServer
import executer
from multiprocessing import Queue
import time

# make public queue
commandQueue = Queue()

# init module
andRaspTCP = tcpServer.TCPServer(commandQueue, "", 12345)
andRaspTCP.start()

# set module to executer
commandExecuter = executer.Executer(andRaspTCP)

while True:
    try:
        command = commandQueue.get()
        commandExecuter.startCommand(command)
    except:
        pass