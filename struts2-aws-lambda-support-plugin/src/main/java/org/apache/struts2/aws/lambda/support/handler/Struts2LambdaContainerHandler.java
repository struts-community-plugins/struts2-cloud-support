package org.apache.struts2.aws.lambda.support.handler;

import com.amazonaws.serverless.proxy.AwsProxyExceptionHandler;
import com.amazonaws.serverless.proxy.AwsProxySecurityContextWriter;
import com.amazonaws.serverless.proxy.ExceptionHandler;
import com.amazonaws.serverless.proxy.RequestReader;
import com.amazonaws.serverless.proxy.ResponseWriter;
import com.amazonaws.serverless.proxy.SecurityContextWriter;
import com.amazonaws.serverless.proxy.internal.servlet.AwsHttpServletResponse;
import com.amazonaws.serverless.proxy.internal.servlet.AwsLambdaServletContainerHandler;
import com.amazonaws.serverless.proxy.internal.servlet.AwsProxyHttpServletRequest;
import com.amazonaws.serverless.proxy.internal.servlet.AwsProxyHttpServletRequestReader;
import com.amazonaws.serverless.proxy.internal.servlet.AwsProxyHttpServletResponseWriter;
import com.amazonaws.serverless.proxy.internal.testutils.Timer;
import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.services.lambda.runtime.Context;
import lombok.extern.log4j.Log4j2;
import org.apache.struts2.aws.lambda.support.interceptors.HttpStatusCodeToHeaderInterceptor;
import org.apache.struts2.dispatcher.filter.StrutsPrepareAndExecuteFilter;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.util.EnumSet;
import java.util.concurrent.CountDownLatch;

/**
 * A Lambda handler to initialize the Struts2 filter and proxy the requests.
 *
 * @param <RequestType>  request type
 * @param <ResponseType> response type
 */
@Log4j2
public class Struts2LambdaContainerHandler<RequestType, ResponseType> extends AwsLambdaServletContainerHandler<RequestType, ResponseType, AwsProxyHttpServletRequest, AwsHttpServletResponse> {
    private static final String TIMER_STRUTS_2_CONTAINER_CONSTRUCTOR = "STRUTS2_CONTAINER_CONSTRUCTOR";
    private static final String TIMER_STRUTS_2_HANDLE_REQUEST = "STRUTS2_HANDLE_REQUEST";
    private static final String TIMER_STRUTS_2_COLD_START_INIT = "STRUTS2_COLD_START_INIT";
    private static final String STRUTS_FILTER_NAME = "Struts2Filter";

    private boolean initialized;

    public static Struts2LambdaContainerHandler<AwsProxyRequest, AwsProxyResponse> getAwsProxyHandler() {
        return new Struts2LambdaContainerHandler(
                new AwsProxyHttpServletRequestReader(),
                new AwsProxyHttpServletResponseWriter(),
                new AwsProxySecurityContextWriter(),
                new AwsProxyExceptionHandler());
    }

    public Struts2LambdaContainerHandler(RequestReader<RequestType, AwsProxyHttpServletRequest> requestReader,
                                         ResponseWriter<AwsHttpServletResponse, ResponseType> responseWriter,
                                         SecurityContextWriter<RequestType> securityContextWriter,
                                         ExceptionHandler<ResponseType> exceptionHandler) {

        super(requestReader, responseWriter, securityContextWriter, exceptionHandler);
        Timer.start(TIMER_STRUTS_2_CONTAINER_CONSTRUCTOR);
        this.initialized = false;
        Timer.stop(TIMER_STRUTS_2_CONTAINER_CONSTRUCTOR);
    }

    protected AwsHttpServletResponse getContainerResponse(AwsProxyHttpServletRequest request, CountDownLatch latch) {
        return new AwsHttpServletResponse(request, latch);
    }

    @Override
    protected void handleRequest(AwsProxyHttpServletRequest httpServletRequest,
                                 AwsHttpServletResponse httpServletResponse,
                                 Context lambdaContext) throws Exception {
        Timer.start(TIMER_STRUTS_2_HANDLE_REQUEST);
        super.handleRequest(httpServletRequest, httpServletResponse, lambdaContext);
        if (!this.initialized) {
            log.info("Initialize Struts2 Lambda Application ...");
            Timer.start(TIMER_STRUTS_2_COLD_START_INIT);
            if (this.startupHandler != null) {
                this.startupHandler.onStartup(this.getServletContext());
            }
            StrutsPrepareAndExecuteFilter filter = new StrutsPrepareAndExecuteFilter();
            FilterRegistration.Dynamic filterRegistration = this.getServletContext()
                    .addFilter(STRUTS_FILTER_NAME, filter);
            filterRegistration.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");
            this.initialized = true;
            Timer.stop(TIMER_STRUTS_2_COLD_START_INIT);
            log.info("... initialize of Struts2 Lambda Application completed!");
        }

        httpServletRequest.setServletContext(this.getServletContext());
        log.debug("Filter request {} proxy request body: {}", httpServletRequest.getPathInfo(), httpServletRequest.getAwsProxyRequest()
                .getBody());
        this.doFilter(httpServletRequest, httpServletResponse, null);
        String responseStatusCode = httpServletResponse.getHeader(HttpStatusCodeToHeaderInterceptor.HEADER_STRUTS_STATUS_CODE);
        if (responseStatusCode != null) {
            log.debug("Set response headers status to: {}", responseStatusCode);
            httpServletResponse.setStatus(Integer.parseInt(responseStatusCode));
        }
        Timer.stop(TIMER_STRUTS_2_HANDLE_REQUEST);
    }
}
