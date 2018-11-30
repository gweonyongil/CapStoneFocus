from firebase_admin import messaging
from firebase_admin import credentials
import datetime
import firebase_admin

cred = credentials.Certificate('capstone-focus-firebase-adminsdk-n8f6h-caefd0006d.json')
default_app = firebase_admin.initialize_app(cred)

# This registration token comes from the client FCM SDKs.
registration_token = 'cSqxC1wMpNE:APA91bGBSBbNdJpeRxRuQeYv43tDaJBRDLBghPajj69JQRMYU1bOJt-9i1l7KnYi4HEp-5AfmqOtwBtjGxAZRggbaXAt_Aa7typHdMMzyN9zPKEGjELqBhgFtH-HitswlZRqhedmKca1'

# See documentation on defining a message payload.
message = messaging.Message(
    android=messaging.AndroidConfig(
        ttl=datetime.timedelta(seconds=3600),
        priority='normal',
        notification=messaging.AndroidNotification(
            title='$GOOG up 1.43% on the day',
            body='test',
            icon='stock_ticker_update',
            color='#f45342'
        ),
    ),
    token=registration_token,
)

# Send a message to the device corresponding to the provided
# registration token.
response = messaging.send(message)
# Response is a message ID string.
print('Successfully sent message:', response)
