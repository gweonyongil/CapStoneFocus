from random import *
from PIL import Image
import time
import base64

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

            # Image Zip, Resize
            img = Image.open('home.jpg')
            img = img.resize((350, 250))
            img.save('test.jpg')

            with open("test.jpg", "rb") as imageFile:
                temp_str = base64.b64encode(imageFile.read())
            n = 2048

            li_da = [temp_str[i:i+n] for i in range(0, len(temp_str), n)]
            print(len(temp_str))
            for p in li_da:
                print(str(p).replace("b'", "").replace("'", "").replace("=", ""))
                self.andRaspTCP.sendAll(str(p).replace("b'", "").replace("'", "").replace("=", ""))
            self.andRaspTCP.sendAll(":" + t + ":" + str(temp) + ":" + str(slope) + ":" + str(fire) + "\n")
