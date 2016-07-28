/* Author: Ajmain Naqib
   Bluestamp Engineering Poject
   SELF-AWARE CAR PROJECT
   Version: 0.1
*/

//all the motors
#include <NewPing.h>
#include <Wire.h>
#include <Adafruit_MotorShield.h>
#include "utility/Adafruit_MS_PWMServoDriver.h"
Adafruit_MotorShield AFMS = Adafruit_MotorShield();
Adafruit_DCMotor *motorFR = AFMS.getMotor(1);
Adafruit_DCMotor *motorFL = AFMS.getMotor(2);
Adafruit_DCMotor *motorBR = AFMS.getMotor(4);
Adafruit_DCMotor *motorBL = AFMS.getMotor(3);

//ultrasonic sensors 4 sides
  #define FrontTRIGGER_PIN  12  // Arduino pin tied to trigger pin on the ultrasonic sensor.
  #define FrontECHO_PIN     11  // Arduino pin tied to echo pin on the ultrasonic sensor.
/*  #define BackTRIGGER_PIN  12
  #define BackECHO_PIN     11
  #define LeftTRIGGER_PIN  12
  #define LeftECHO_PIN     11
  #define RightTRIGGER_PIN  12
  #define RightECHO_PIN     11 */
  #define MAX_DISTANCE 200 // Maximum distance we want to ping for (in centimeters). Maximum sensor distance is rated at 400-500cm.
  NewPing sonarFront(FrontTRIGGER_PIN, FrontECHO_PIN, MAX_DISTANCE);
//  NewPing sonarBack(BackTRIGGER_PIN, BackECHO_PIN, MAX_DISTANCE);
//  NewPing sonarLeft(LeftTRIGGER_PIN, LeftECHO_PIN, MAX_DISTANCE);
//  NewPing sonarRight(RightTRIGGER_PIN, RightECHO_PIN, MAX_DISTANCE);

int recv = 0;
int speed = 140; //speed of all the motors for easy access

void setup() {
  Serial.begin(9600);
  AFMS.begin();
  motorFR->setSpeed(speed);
  motorFL->setSpeed(speed);
  motorBR->setSpeed(speed);
  motorBL->setSpeed(speed);
}

void loop() {
 if (Serial.available() > 0) {
    recv = Serial.read();
 
    // if 'y' (decimal 121) is received, turn LED/Powertail on
    // anything other than 121 is received, turn LED/Powertail off
    if (recv == 121){
      moveRight();
    }else if(recv == 110) {
moveLeft();
    }else {
      stopRobot();
      }
     
    // confirm values received in serial monitor window
    Serial.print("--Arduino received: ");
    Serial.println(recv);
  }
}

int moveForwardAvoidObs() {
   int distance = sonarFront.ping_cm();
  delay(50);                     // Wait 50ms between pings (about 20 pings/sec). 29ms should be the shortest delay between pings.
  Serial.print("Ping: ");
  Serial.print(distance); // Send ping, get distance in cm and print result (0 = outside set distance range)
  Serial.println("cm");

  if (distance > 16 && distance !=0){
    moveBackward();

      Serial.println("Going forward");

    } if( distance< 15 && distance >0){
      moveForward();
          delay(300);
    rotateLeft();
    delay(300);
        Serial.println("Going Back");
    }
}

int moveForward() {
  motorFR->run(FORWARD);
  motorFL->run(FORWARD);
  motorBR->run(FORWARD);
  motorBL->run(FORWARD
  );
}

int moveBackward() {
  motorFR->run(BACKWARD);
  motorFL->run(BACKWARD);
  motorBR->run(BACKWARD);
  motorBL->run(BACKWARD);
}

int moveRight() {
  motorFR->run(FORWARD);
  motorFL->run(BACKWARD);
  motorBR->run(BACKWARD);
  motorBL->run(FORWARD);
}

int moveLeft() {
  motorFR->run(BACKWARD);
  motorFL->run(FORWARD);
  motorBR->run(FORWARD);
  motorBL->run(BACKWARD);
}

int moveDiagnalRightForward() {
  motorFR->setSpeed(0);
  motorBL->setSpeed(0);

  motorFR->run(FORWARD);
  motorFL->run(FORWARD);
  motorBR->run(FORWARD);
  motorBL->run(FORWARD);
}

int moveDiagnalLeftForward() {
  motorFR->setSpeed(speed);
  motorFL->setSpeed(0);
  motorBR->setSpeed(0);
  motorBL->setSpeed(speed);

  motorFR->run(FORWARD);
  motorFL->run(FORWARD);
  motorBR->run(FORWARD);
  motorBL->run(FORWARD);
}

int moveDiagnalRightBackward() {
  motorFL->setSpeed(0);
  motorBR->setSpeed(0);

  motorFR->run(BACKWARD);
  motorFL->run(FORWARD);
  motorBR->run(FORWARD);
  motorBL->run(BACKWARD);
}

int moveDiagnalLeftBackward() {
  motorFR->setSpeed(0);
  motorFL->setSpeed(speed);
  motorBR->setSpeed(speed);
  motorBL->setSpeed(0);

  motorFR->run(FORWARD);
  motorFL->run(BACKWARD);
  motorBR->run(BACKWARD);
  motorBL->run(FORWARD);
}


int rotateLeft() {
  motorFR->run(BACKWARD);
  motorFL->run(FORWARD);
  motorBR->run(BACKWARD);
  motorBL->run(FORWARD);
}

int stopRobot(){
    motorFR->run(RELEASE);
  motorFL->run(RELEASE);
  motorBR->run(RELEASE);
  motorBL->run(RELEASE);
  }
