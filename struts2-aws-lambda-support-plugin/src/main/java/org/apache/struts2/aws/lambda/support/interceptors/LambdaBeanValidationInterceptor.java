package org.apache.struts2.aws.lambda.support.interceptors;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.ValidationAware;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.struts.beanvalidation.validation.interceptor.BeanValidationInterceptor;
import org.apache.struts2.StrutsStatics;
import org.apache.struts2.interceptor.validation.SkipValidation;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Validator;

@Log4j2
public class LambdaBeanValidationInterceptor extends BeanValidationInterceptor {

    private int validationFailureStatusCode = 406;

    @Inject(value = "struts.rest.validationFailureStatusCode", required = false)
    public void setValidationFailureStatusCode(String validationFailureStatusCode) {
        this.validationFailureStatusCode = Integer.parseInt(validationFailureStatusCode);
    }

    @Override
    protected String doIntercept(ActionInvocation invocation) throws Exception {
        Validator validator = this.beanValidationManager.getValidator();
        if (validator == null) {
            log.debug("There is no Bean Validator configured in class path. Skipping Bean validation..");
            return invocation.invoke();
        }
        log.debug("Starting bean validation using validator: {}", validator.getClass());

        Object action = invocation.getAction();
        ActionProxy actionProxy = invocation.getProxy();
        String methodName = actionProxy.getMethod();

        log.debug("Validating [{}/{}] with method [{}]", invocation.getProxy().getNamespace(), invocation.getProxy().getActionName(), methodName);

        if (null == MethodUtils.getAnnotation(getActionMethod(action.getClass(), methodName), SkipValidation.class,
                true, true)) {
            Class<?>[] validationGroup = getValidationGroups(action, methodName);
            // performing bean validation on action
            performBeanValidation(action, validator, validationGroup);
        }

        if(action instanceof ValidationAware && ((ValidationAware) action).hasErrors()) {
            HttpServletResponse response = (HttpServletResponse) invocation.getInvocationContext()
                    .get(StrutsStatics.HTTP_RESPONSE);
            response.setStatus(validationFailureStatusCode);

            return Action.INPUT;
        }
        return invocation.invoke();
    }


}
