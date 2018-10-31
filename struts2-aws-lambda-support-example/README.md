# Struts2 Cloud Support Plugin

This example AWS Lambda function demonstrate how to create a simple Lambda function to manage orders.

## Run Demo

### Deploy as AWS Lambda function 

1. Run **mvn install** to build ZIP file
2. In AWS Console or over AWS CLI deploy generated ZIP file with name **struts2-orders-demo**
3. Use _com.amazonaws.serverless.proxy.struts2.Struts2LambdaHandler::handleRequest_ as Handler

### API Gateway configuration

1. Select Service Amazon API Gateway
2. Click Create API and use following API definition swagger file and replace all <YOUR_REGION> with your Region (e.g. us-west-2) and all <YOUR_AWS_ACCOUNT_ID> with your account id

````json

{
  "swagger": "2.0",
  "info": {
    "title": "orders"
  },
  "basePath": "/demo",
  "schemes": [
    "https"
  ],
  "paths": {
    "/data/language": {
      "get": {
        "produces": [
          "application/json"
        ],
        "responses": {
          "200": {
            "description": "200 response",
            "schema": {
              "$ref": "#/definitions/Empty"
            },
            "headers": {
              "Access-Control-Allow-Origin": {
                "type": "string"
              }
            }
          }
        },
        "x-amazon-apigateway-integration": {
          "uri": "arn:aws:apigateway:<YOUR_REGION>:lambda:path/2015-03-31/functions/arn:aws:lambda:<YOUR_REGION>:<YOUR_AWS_ACCOUNT_ID>:function:struts2-orders-demo/invocations",
          "responses": {
            "default": {
              "statusCode": "200",
              "responseParameters": {
                "method.response.header.Access-Control-Allow-Origin": "'*'"
              }
            }
          },
          "passthroughBehavior": "when_no_match",
          "httpMethod": "POST",
          "contentHandling": "CONVERT_TO_TEXT",
          "type": "aws_proxy"
        }
      },
      "options": {
        "consumes": [
          "application/json"
        ],
        "produces": [
          "application/json"
        ],
        "responses": {
          "200": {
            "description": "200 response",
            "schema": {
              "$ref": "#/definitions/Empty"
            },
            "headers": {
              "Access-Control-Allow-Origin": {
                "type": "string"
              },
              "Access-Control-Allow-Methods": {
                "type": "string"
              },
              "Access-Control-Allow-Headers": {
                "type": "string"
              }
            }
          }
        },
        "x-amazon-apigateway-integration": {
          "responses": {
            "default": {
              "statusCode": "200",
              "responseParameters": {
                "method.response.header.Access-Control-Allow-Methods": "'GET,OPTIONS'",
                "method.response.header.Access-Control-Allow-Headers": "'Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token'",
                "method.response.header.Access-Control-Allow-Origin": "'*'"
              }
            }
          },
          "requestTemplates": {
            "application/json": "{\"statusCode\": 200}"
          },
          "passthroughBehavior": "when_no_match",
          "type": "mock"
        }
      }
    },
    "/data/order": {
      "get": {
        "consumes": [
          "application/json"
        ],
        "produces": [
          "application/json"
        ],
        "parameters": [
          {
            "name": "Accept",
            "in": "header",
            "required": false,
            "type": "string"
          },
          {
            "in": "body",
            "name": "Empty",
            "required": true,
            "schema": {
              "$ref": "#/definitions/Empty"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "200 response",
            "schema": {
              "$ref": "#/definitions/Empty"
            },
            "headers": {
              "Access-Control-Allow-Origin": {
                "type": "string"
              }
            }
          }
        },
        "x-amazon-apigateway-integration": {
          "uri": "arn:aws:apigateway:<YOUR_REGION>:lambda:path/2015-03-31/functions/arn:aws:lambda:<YOUR_REGION>:<YOUR_AWS_ACCOUNT_ID>:function:struts2-orders-demo/invocations",
          "responses": {
            "default": {
              "statusCode": "200",
              "responseParameters": {
                "method.response.header.Access-Control-Allow-Origin": "'*'"
              }
            }
          },
          "passthroughBehavior": "when_no_match",
          "httpMethod": "POST",
          "contentHandling": "CONVERT_TO_TEXT",
          "type": "aws_proxy"
        }
      },
      "post": {
        "consumes": [
          "application/json"
        ],
        "produces": [
          "application/json"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "Empty",
            "required": true,
            "schema": {
              "$ref": "#/definitions/Empty"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "200 response",
            "schema": {
              "$ref": "#/definitions/Empty"
            },
            "headers": {
              "Access-Control-Allow-Origin": {
                "type": "string"
              }
            }
          }
        },
        "x-amazon-apigateway-integration": {
          "uri": "arn:aws:apigateway:<YOUR_REGION>:lambda:path/2015-03-31/functions/arn:aws:lambda:<YOUR_REGION>:<YOUR_AWS_ACCOUNT_ID>:function:struts2-orders-demo/invocations",
          "responses": {
            "default": {
              "statusCode": "200",
              "responseParameters": {
                "method.response.header.Access-Control-Allow-Origin": "'*'"
              }
            }
          },
          "passthroughBehavior": "when_no_match",
          "httpMethod": "POST",
          "contentHandling": "CONVERT_TO_TEXT",
          "type": "aws_proxy"
        }
      },
      "options": {
        "consumes": [
          "application/json"
        ],
        "produces": [
          "application/json"
        ],
        "responses": {
          "200": {
            "description": "200 response",
            "schema": {
              "$ref": "#/definitions/Empty"
            },
            "headers": {
              "Access-Control-Allow-Origin": {
                "type": "string"
              },
              "Access-Control-Allow-Methods": {
                "type": "string"
              },
              "Access-Control-Allow-Headers": {
                "type": "string"
              }
            }
          }
        },
        "x-amazon-apigateway-integration": {
          "responses": {
            "default": {
              "statusCode": "200",
              "responseParameters": {
                "method.response.header.Access-Control-Allow-Methods": "'POST,GET,OPTIONS'",
                "method.response.header.Access-Control-Allow-Headers": "'Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token'",
                "method.response.header.Access-Control-Allow-Origin": "'*'"
              }
            }
          },
          "requestTemplates": {
            "application/json": "{\"statusCode\": 200}"
          },
          "passthroughBehavior": "when_no_match",
          "type": "mock"
        }
      }
    },
    "/data/order/{id}": {
      "get": {
        "produces": [
          "application/json"
        ],
        "parameters": [
          {
            "name": "id",
            "in": "path",
            "required": true,
            "type": "string"
          }
        ],
        "responses": {
          "200": {
            "description": "200 response",
            "schema": {
              "$ref": "#/definitions/Empty"
            },
            "headers": {
              "Access-Control-Allow-Origin": {
                "type": "string"
              }
            }
          }
        },
        "x-amazon-apigateway-integration": {
          "uri": "arn:aws:apigateway:<YOUR_REGION>:lambda:path/2015-03-31/functions/arn:aws:lambda:<YOUR_REGION>:<YOUR_AWS_ACCOUNT_ID>:function:struts2-orders-demo/invocations",
          "responses": {
            "default": {
              "statusCode": "200",
              "responseParameters": {
                "method.response.header.Access-Control-Allow-Origin": "'*'"
              }
            }
          },
          "passthroughBehavior": "when_no_match",
          "httpMethod": "POST",
          "contentHandling": "CONVERT_TO_TEXT",
          "type": "aws_proxy"
        }
      },
      "put": {
        "produces": [
          "application/json"
        ],
        "parameters": [
          {
            "name": "id",
            "in": "path",
            "required": true,
            "type": "string"
          }
        ],
        "responses": {
          "200": {
            "description": "200 response",
            "schema": {
              "$ref": "#/definitions/Empty"
            },
            "headers": {
              "Access-Control-Allow-Origin": {
                "type": "string"
              }
            }
          }
        },
        "x-amazon-apigateway-integration": {
          "uri": "arn:aws:apigateway:<YOUR_REGION>:lambda:path/2015-03-31/functions/arn:aws:lambda:<YOUR_REGION>:<YOUR_AWS_ACCOUNT_ID>:function:struts2-orders-demo/invocations",
          "responses": {
            "default": {
              "statusCode": "200",
              "responseParameters": {
                "method.response.header.Access-Control-Allow-Origin": "'*'"
              }
            }
          },
          "passthroughBehavior": "when_no_match",
          "httpMethod": "POST",
          "contentHandling": "CONVERT_TO_TEXT",
          "type": "aws_proxy"
        }
      },
      "delete": {
        "produces": [
          "application/json"
        ],
        "parameters": [
          {
            "name": "id",
            "in": "path",
            "required": true,
            "type": "string"
          }
        ],
        "responses": {
          "200": {
            "description": "200 response",
            "schema": {
              "$ref": "#/definitions/Empty"
            },
            "headers": {
              "Access-Control-Allow-Origin": {
                "type": "string"
              }
            }
          }
        },
        "x-amazon-apigateway-integration": {
          "uri": "arn:aws:apigateway:<YOUR_REGION>:lambda:path/2015-03-31/functions/arn:aws:lambda:<YOUR_REGION>:<YOUR_AWS_ACCOUNT_ID>:function:struts2-orders-demo/invocations",
          "responses": {
            "default": {
              "statusCode": "200",
              "responseParameters": {
                "method.response.header.Access-Control-Allow-Origin": "'*'"
              }
            }
          },
          "passthroughBehavior": "when_no_match",
          "httpMethod": "POST",
          "contentHandling": "CONVERT_TO_TEXT",
          "type": "aws_proxy"
        }
      },
      "options": {
        "consumes": [
          "application/json"
        ],
        "produces": [
          "application/json"
        ],
        "responses": {
          "200": {
            "description": "200 response",
            "schema": {
              "$ref": "#/definitions/Empty"
            },
            "headers": {
              "Access-Control-Allow-Origin": {
                "type": "string"
              },
              "Access-Control-Allow-Methods": {
                "type": "string"
              },
              "Access-Control-Allow-Headers": {
                "type": "string"
              }
            }
          }
        },
        "x-amazon-apigateway-integration": {
          "responses": {
            "default": {
              "statusCode": "200",
              "responseParameters": {
                "method.response.header.Access-Control-Allow-Methods": "'DELETE,GET,OPTIONS,PUT'",
                "method.response.header.Access-Control-Allow-Headers": "'Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token'",
                "method.response.header.Access-Control-Allow-Origin": "'*'"
              }
            }
          },
          "requestTemplates": {
            "application/json": "{\"statusCode\": 200}"
          },
          "passthroughBehavior": "when_no_match",
          "type": "mock"
        }
      }
    }
  },
  "definitions": {
    "Empty": {
      "type": "object",
      "title": "Empty Schema"
    }
  },
  "x-amazon-apigateway-minimum-compression-size": 0
}

````

2. Go to resources and select _orders_ and select path /data/order/GET
3. Click _Test_ in the Client section.
4. Enter following headers in the Headers text area and execute
5. In the API Actions dropdown select _Deploy API_ and deploy it to existent or new stage
   
```
Accept:application/json
Content-Type:application/json;charset=UTF-8
```

5. In the results you should see a json array of three orders
6. If an error message is shown please try to go to **Integration Request** and select the deployed lambda function again.


### Run demo webapp

1. Go to src/main/webapp
2. Edit js/app.js and configure your API endpoint with your region, account id and stage.
3. Deploy static website content on a webserver or S3 bucket with static website hosting.
