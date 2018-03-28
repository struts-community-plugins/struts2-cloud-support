package org.apache.struts2.aws.lambda.support.interceptors;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.MethodFilterInterceptor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.StrutsStatics;

import javax.servlet.http.HttpServletResponse;

/**
 * Interceptor to set CORS header on request.
 */
@Log4j2
public class CorsHeaderInterceptor extends MethodFilterInterceptor {

    private String allowOrigin = "";
    private String allowHeaders = "Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token";
    private String allowCredentials = "true";
    private String contentType = "application/json";

    @Inject(value = "struts.corsHeader.allowOrigin", required = false)
    public void setAllowOrigin(String allowOrigin) {
        this.allowOrigin = allowOrigin;
    }

    @Inject(value = "struts.corsHeader.allowHeaders", required = false)
    public void setAllowHeaders(String allowHeaders) {
        this.allowHeaders = allowHeaders;
    }

    @Inject(value = "struts.corsHeader.allowCredentials", required = false)
    public void setAllowCredentials(String allowCredentials) {
        this.allowCredentials = allowCredentials;
    }

    @Inject(value = "struts.corsHeader.contentType", required = false)
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Override
    protected String doIntercept(ActionInvocation actionInvocation) throws Exception {
        String invoke = actionInvocation.invoke();
        if (StringUtils.isNotBlank(allowOrigin)) {
            HttpServletResponse response = (HttpServletResponse) actionInvocation.getInvocationContext()
                    .get(StrutsStatics.HTTP_RESPONSE);
            response.addHeader("Access-Control-Allow-Origin", "*");
            response.addHeader("Access-Control-Allow-Headers", this.allowHeaders);
            response.addHeader("Access-Control-Allow-Credentials", this.allowCredentials);
            response.addHeader("Content-Type", this.contentType);
        }
        return invoke;
    }
}
