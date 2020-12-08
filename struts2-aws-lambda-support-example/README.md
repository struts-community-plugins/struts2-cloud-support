# Struts2 Cloud Support Plugin

This example AWS Lambda function demonstrate how to create a simple Lambda function to manage orders.

The application can be deployed in an AWS account using the [Serverless Application Model](https://github.com/awslabs/serverless-application-model). 
The `sam.yaml` file in the root folder contains the application definition.

## Installation
To build and install the sample application you will need [Maven](https://maven.apache.org/) and the [AWS CLI](https://aws.amazon.com/cli/) installed on your computer.

In a shell, navigate to the sample's folder and use maven to build a deployable jar.
```
$ mvn package
```

This command should generate a `struts2-aws-lambda-support-example-1.3.0-lambda.zip` in the `target` folder. 
Now that we have generated the zip file, we can use the AWS CLI to package the template for deployment. 

You will need an S3 bucket to store the artifacts for deployment. Once you have created the S3 bucket, run the following command from the sample's folder:

```
$ aws cloudformation package --template-file sam.yaml --output-template-file output-sam.yaml --s3-bucket <YOUR S3 BUCKET NAME>
Uploading to xxxxxxxxxxx  10232401 / 10232401.0  (100.00%)
Successfully packaged artifacts and wrote output template to file output-sam.yaml.
Execute the following command to deploy the packaged template
aws cloudformation deploy --template-file /path/to/saml/struts2-aws-lambda-support-example/output-sam.yaml --stack-name <YOUR STACK NAME>
```

As the command output suggests, you can now use the cli to deploy the application. Choose a stack name and run the `aws cloudformation deploy` command from the output of the package command.
 
```
$ aws cloudformation deploy --template-file output-sam.yaml --stack-name <YOUR STACK NAME> --capabilities CAPABILITY_IAM
```

Once the application is deployed, you can describe the stack to show the API endpoint that was created. The endpoint should be the `Struts2LambdaSupportExampleApi` key of the `Outputs` property:

```
$ aws cloudformation describe-stacks --stack-name <YOUR STACK NAME>
{
    "Stacks": [
        {
            "StackId": "arn:aws:cloudformation:<YOUR REGION>:1234567879:stack/<YOUR STACK NAME>/1234-abcde-46788,
            "StackName": "<YOUR STACK NAME>",
            "ChangeSetId": "arn:aws:cloudformation:<YOUR REGION>:1234567879:changeSet/awscli-cloudformation-package-deploy-1542130046/1234567-0008-4d10-b925-890",
            "Description": "Struts2 AWS Lambda Support Plugin Example - 1.3.0",
            "CreationTime": "2018-10-31T12:41:30.316Z",
            "LastUpdatedTime": "2018-11-13T17:27:32.209Z",
            "RollbackConfiguration": {},
            "StackStatus": "UPDATE_COMPLETE",
            "DisableRollback": false,
            "NotificationARNs": [],
            "Capabilities": [
                "CAPABILITY_IAM"
            ],
            "Outputs": [
                {
                    "OutputKey": "Struts2LambdaSupportExampleApi",
                    "OutputValue": "https://xxxx.execute-api.<YOUR REGION>.amazonaws.com/Prod/data/order.json",
                    "Description": "URL for application",
                    "ExportName": "Struts2LambdaSupportExample"
                }
            ],
            "Tags": [],
            "EnableTerminationProtection": false
        }
    ]
}

```

Copy the `OutputValue` into a browser to test a first request.