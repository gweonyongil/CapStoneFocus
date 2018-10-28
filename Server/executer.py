import time
class Executer:
    def __init__(self, tcpServer):
        self.andRaspTCP = tcpServer

    def startCommand(self, command):
        print(command)
        if command == "123\n":
            now = time.localtime()
            t = "%04d-%02d-%02d %02d %02d %02d" % (now.tm_year, now.tm_mon, now.tm_mday, now.tm_hour, now.tm_min, now.tm_sec)
            temp = "32"
            slope = "0"
            humid = "100"
            self.andRaspTCP.sendAll(t + ":" + temp + ":" + slope + ":" + humid + "%\n")
