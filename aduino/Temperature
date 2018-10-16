float temperature;  
int reading;  
int lm35Pin = A0;

void setup()  
{
    analogReference(INTERNAL);
    Serial.begin(9600);
}

void loop()  
{
    reading = analogRead(lm35Pin);
    temperature = reading / 9.31;
    
    Serial.println(temperature);
    delay(1000);
}
