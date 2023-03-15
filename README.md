Module 03 Assignment 03: Programming Assignment 1
Erica Butts

1. Set up EC2 Instances "Willow" and "Linus"
-use SSH key file to connect to the Linux EC2 via terminal

2. Install Maven on EC2 and AWS CLI with PIP

3. Grant permission to Maven (with access key credentials) and SQS, S3, and Rekognition via IAM User Settings
-go to ~/.aws and credentials to apply the access key

4. Create a Maven project; edit pom.xml to call dependencies for Maven, SQS, S3 and Rekognition services

5. Create App.java and S3Service.java files in "Willow" (recognize cars) and "Linus" (recognize text)

6. Compile the Maven projects in their myapp directories

7. Create a FIFO queue in AWS named "paul.fifo"; click "Send and Receive Messages" to start the polling

8. Run Willow and Linus simultaneously

9. Check the console and output.txt for the results
