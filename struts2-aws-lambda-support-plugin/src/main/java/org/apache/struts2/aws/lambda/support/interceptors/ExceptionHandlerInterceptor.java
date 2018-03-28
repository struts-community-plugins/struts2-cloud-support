package org.apache.struts2.aws.lambda.support.interceptors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.MethodFilterInterceptor;
import lombok.extern.log4j.Log4j2;
import org.apache.struts2.StrutsStatics;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * setting action error in case of any exception thrown during the request.
 */
@Log4j2
public class ExceptionHandlerInterceptor extends MethodFilterInterceptor {

    private static final String ACTION_ERROR = "actionError";

    private static ObjectMapper mapper = new ObjectMapper();

    @Override
    protected String doIntercept(ActionInvocation actionInvocation) throws Exception {
        try {
            return actionInvocation.invoke();
        } catch (Exception exception) {
            Map<String, Object> errors = new HashMap<>();

            int statusCode = HttpServletResponse.SC_BAD_REQUEST;

            if (exception instanceof SecurityException) {
                errors.put(ACTION_ERROR, "Operation not allowed!");
                statusCode = HttpServletResponse.SC_FORBIDDEN;
            } else {
                errors.put(ACTION_ERROR, exception.getMessage());
            }
            HttpServletResponse response = (HttpServletResponse) actionInvocation.getInvocationContext()
                    .get(StrutsStatics.HTTP_RESPONSE);
            response.setStatus(statusCode);
            response.addHeader(HttpStatusCodeToHeaderInterceptor.HEADER_STRUTS_STATUS_CODE, "" + statusCode);
            mapper.writeValue(response.getOutputStream(), errors);
            return Action.ERROR;
        }
    }
}
