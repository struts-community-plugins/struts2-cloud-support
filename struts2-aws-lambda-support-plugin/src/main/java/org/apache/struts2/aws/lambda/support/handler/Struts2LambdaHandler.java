package org.apache.struts2.aws.lambda.support.handler;

import com.amazonaws.serverless.proxy.internal.LambdaContainerHandler;
import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * The lambda handler to handle the requests.
 * <p>
 * <code>
 * org.apache.struts2.aws.lambda.support.handler.Struts2LambdaHandler::handleRequest
 * </code>
 */
@Log4j2
public class Struts2LambdaHandler implements RequestStreamHandler {

    private final Struts2LambdaContainerHandler<AwsProxyRequest, AwsProxyResponse> handler = Struts2LambdaContainerHandler
            .getAwsProxyHandler();

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) {

        try {
            AwsProxyRequest request = LambdaContainerHandler.getObjectMapper()
                    .readValue(inputStream, AwsProxyRequest.class);

            log.debug("Handle request path: {}", request.getPath());

            AwsProxyResponse response = handler.proxy(request, context);
            LambdaContainerHandler.getObjectMapper().writeValue(outputStream, response);

            // just in case it wasn't closed by the mapper
            outputStream.close();
        } catch (IOException e) {
            log.error("An unexpected exception happened while handling request: {}", e.getMessage(), e);
        }
    }
}
