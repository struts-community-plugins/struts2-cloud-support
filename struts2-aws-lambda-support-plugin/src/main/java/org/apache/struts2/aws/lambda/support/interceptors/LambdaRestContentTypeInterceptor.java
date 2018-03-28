package org.apache.struts2.aws.lambda.support.interceptors;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.rest.ContentTypeHandlerManager;
import org.apache.struts2.rest.handler.ContentTypeHandler;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;

@Log4j2
public class LambdaRestContentTypeInterceptor extends AbstractInterceptor {

    private static final long serialVersionUID = 7353444754536750933L;

    private ContentTypeHandlerManager selector;

    @Inject
    public void setContentTypeHandlerSelector(ContentTypeHandlerManager sel) {
        this.selector = sel;
    }

    public String intercept(ActionInvocation invocation) throws Exception {
        HttpServletRequest request = ServletActionContext.getRequest();
        ContentTypeHandler handler = selector.getHandlerForRequest(request);
        log.debug("Found content type handler for request: {}", handler);

        Object target = invocation.getAction();
        if (target instanceof ModelDriven) {
            target = ((ModelDriven) target).getModel();
        }

        if (request.getContentLength() > 0) {
            InputStream is = request.getInputStream();
            InputStreamReader reader = new InputStreamReader(is);
            handler.toObject(invocation, reader, target);
        } else {
            if (requestHasBody(request)) {
                String body = IOUtils.toString(request.getReader());
                handler.toObject(invocation, new StringReader(body), target);
            }
        }
        return invocation.invoke();
    }

    private boolean requestHasBody(HttpServletRequest request) throws IOException {
        return ("POST".equalsIgnoreCase(request.getMethod()) || "PUT".equalsIgnoreCase(request.getMethod())) && request.getReader() != null;
    }
}
