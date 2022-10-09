package org.apache.struts2.aws.lambda.support.interceptors;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.MethodFilterInterceptor;
import lombok.extern.log4j.Log4j2;
import org.apache.struts2.StrutsStatics;

import javax.servlet.http.HttpServletResponse;

/**
 * Lambda can not handle the request status itself. Workaround is to set the status code as response header and read this afterwards in the {@link com.amazonaws.serverless.proxy.struts.StrutsLambdaContainerHandler} to set correct status code.
 */
@Log4j2
public class HttpStatusCodeToHeaderInterceptor extends MethodFilterInterceptor {

    public static final String HEADER_STRUTS_STATUS_CODE = "X-Struts-StatusCode";

    @Override
    protected String doIntercept(ActionInvocation actionInvocation) throws Exception {
        String invoke = actionInvocation.invoke();
        HttpServletResponse response = (HttpServletResponse) actionInvocation.getInvocationContext()
                .get(StrutsStatics.HTTP_RESPONSE);
        log.debug("Set struts header response status to: {}", response.getStatus());
        response.addHeader(HEADER_STRUTS_STATUS_CODE, "" + response.getStatus());
        return invoke;
    }
}
