from datetime import datetime
from socket import *
from threading import Thread

serverPort = 31187
serverSocket = socket(AF_INET, SOCK_DGRAM)
serverSocket.bind(('', serverPort))
responseMessage = ''

# Show message when the server is ready
print("The server is ready to receive on port", serverPort)

try:
    while True:
        message, clientAddress = serverSocket.recvfrom(2048)
        # message Split example
        stringA = "1 2 3 4 5 6 7 8 9"
        listA = stringA.split(" ")
        print(listA)
        # Check Arudino or Android
        # 만약 아두이노라면 값 갱신(온도 등등)
        # 안드로이드라면 현재 값 전송(온도, 사진 등등)

        print('Connection requested from', clientAddress)
        # Processing message
        print('Command ' + message.decode() +  '\n')
        if message.decode() == '1':
            responseMessage, clientAddress = serverSocket.recvfrom(2048)
            responseMessage = responseMessage.decode().upper()
        elif message.decode() == '2':
            responseMessage, clientAddress = serverSocket.recvfrom(2048)
            responseMessage = responseMessage.decode().lower()
        elif message.decode() == '3':
            responseMessage = 'IP : ' + clientAddress[0] + ' Port : ' + str(clientAddress[1])
        elif message.decode() == '4':
            responseMessage = datetime.now().isoformat()
        # Send to Client
        serverSocket.sendto(responseMessage.encode(), clientAddress)
# Process Ctrl-C Interrupt
except KeyboardInterrupt:
    print('\n'
          'Bye bye~')
    serverSocket.close()
