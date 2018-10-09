class Executer:
    def __init__(self, tcpServer):
        self.andRaspTCP = tcpServer

    def startCommand(self, command):
        print(command)
        if command == "123\n":
            self.andRaspTCP.sendAll("Sungmin11242342342341\n")