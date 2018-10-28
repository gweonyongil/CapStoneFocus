from random import *
import time
class Executer:
    def __init__(self, tcpServer):
        self.andRaspTCP = tcpServer

    def startCommand(self, command):
        print(command)
        if command == "123\n":
            now = time.localtime()
            t = "%04d-%02d-%02d %02d %02d %02d" % (now.tm_year, now.tm_mon, now.tm_mday, now.tm_hour, now.tm_min, now.tm_sec)
            temp = randint(16, 20)
            slope = randint(1, 8)
            if slope <= 2:
                slope = 1
            else:
                slope = 0
            fire = randint(1, 8)
            if slope == 0 and fire <= 2:
                fire = 1
                temp += 20
            else:
                fire = 0
            self.andRaspTCP.sendAll(t + ":" + str(temp) + ":" + str(slope) + ":" + str(fire) + "\n")
