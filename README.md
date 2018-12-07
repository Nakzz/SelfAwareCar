# SelfAwareCar

**SelfAwareCar** navigates a mechanum directional drive model robot using vision recognition with the OpenCV Java library. The program runs on a Raspberry pi connected to an Arduino with a motor shield. You can find more about the project (here.)http://bluestampengineering.com/student-projects/portfolioajmain-n/]

# Pre-requisites
```sh
OpenCV-2.4.13
jSerialComm
```

# Project Structure
## ./src/identifytraffic.java
Uses usb camera to capture and identify traffic lights.


## ./src/ArduinoComm.java
Establishes connection between Raspberry and Arduino, through serial communication, controls the motors. 
