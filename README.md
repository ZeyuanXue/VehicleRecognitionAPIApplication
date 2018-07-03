# Vehicle Recognition API Application
## Overview
This is a application that using Sighthound Vehicle Recognition API to achieve massive file process.
The application read files from image folder and transfer each image file to binary stream one at a time.
Then the binary stream with headers will be send to API server using POST method.
Finally, the response will be parsed and stored into output.csv file and the application will go to next iteration, next image.

## How to compile:
For Mac, in the root directory of the project, use command:
```
javac -cp .:libs/javax.json-1.0.jar RecCar.java
```

## How to run:
For mac in the root directory of the project, after copied the image(s) into image folder, use command:
```
java -cp .:libs/javax.json-1.0.jar RecCar
```
## Other
Note: For windows users, the relative path of image would be different and the compile command also may differ.

This application is only for development and testings. No commercial use.
API : Sighthound Could API Development License
