import base64

with open("untitled.png", "rb") as imageFile:
    temp_str = base64.b64encode(imageFile.read())
    print(temp_str)
