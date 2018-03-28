package org.apache.struts2.examples.aws.lambda.actions.data;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ModelDriven;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.rest.RestActionSupport;

import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@Log4j2
public class LanguageController extends RestActionSupport implements ModelDriven<Map<String, String>> {

    private static final long serialVersionUID = 8876488723718041965L;

    private Map<String, String> model;

    public String index() {

        Locale locale = ActionContext.getContext().getLocale();
        log.debug("Get texts resources for locale: {}", locale);

        ResourceBundle bundle = getTexts("frontend");
        this.model = bundle.keySet()
                .stream()
                .collect(Collectors.toMap(
                        key -> key,
                        bundle::getString));

        return Action.SUCCESS;
    }


    @Override
    public Map<String, String> getModel() {
        return model;
    }
}
