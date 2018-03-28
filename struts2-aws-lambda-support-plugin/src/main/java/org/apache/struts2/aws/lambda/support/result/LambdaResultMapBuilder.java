package org.apache.struts2.aws.lambda.support.result;

import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.config.entities.ResultTypeConfig;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.finder.ResourceFinder;
import com.opensymphony.xwork2.util.finder.Test;
import lombok.extern.log4j.Log4j2;
import org.apache.struts2.convention.DefaultResultMapBuilder;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

/**
 * Override the {@link DefaultResultMapBuilder} createFromResources method. Because the default is not compatible with AWS lambda runtime.
 */
@Log4j2
public class LambdaResultMapBuilder extends DefaultResultMapBuilder {

    /**
     * Constructs the SimpleResultMapBuilder using the given result location.
     *
     * @param servletContext      The ServletContext for finding the resources of the web application.
     * @param container           The Xwork container
     * @param relativeResultTypes The list of result types that can have locations that are relative
     *                            and the result location (which is the resultPath plus the namespace) prepended to them.
     */
    @Inject
    public LambdaResultMapBuilder(ServletContext servletContext, Container container,
                                  @Inject("struts.convention.relative.result.types") String relativeResultTypes) {
        super(servletContext, container, relativeResultTypes);
    }

    /**
     *
     * Needs to be overridde because of null pointer exception
     *
     * Creates any result types from the resources available in the web application. This scans the
     * web application resources using the servlet context.
     *
     * @param actionClass        The action class the results are being built for.
     * @param results            The results map to put the result configs created into.
     * @param resultPath         The calculated path to the resources.
     * @param resultPrefix       The prefix for the result. This is usually <code>/resultPath/actionName</code>.
     * @param actionName         The action name which is used only for logging in this implementation.
     * @param packageConfig      The package configuration which is passed along in order to determine
     * @param resultsByExtension The map of extensions to result type configuration instances.
     */
    @Override
    protected void createFromResources(Class<?> actionClass, Map<String, ResultConfig> results,
                                       final String resultPath, final String resultPrefix, final String actionName,
                                       PackageConfig packageConfig, Map<String, ResultTypeConfig> resultsByExtension) {
        if (log.isTraceEnabled()) {
            log.trace("Searching for results in the Servlet container at [{}] with result prefix of {}", resultPath, resultPrefix);
        }

        // Building from the classpath
        String classPathLocation = resultPath.startsWith("/") ?
                resultPath.substring(1, resultPath.length()) : resultPath;
        if (log.isTraceEnabled()) {
            log.trace("Searching for results in the class path at [{}]"
                            + " with a result prefix of [{}] and action name [{}]", classPathLocation, resultPrefix,
                    actionName);
        }

        ResourceFinder finder = new ResourceFinder(classPathLocation, getClassLoaderInterface());
        try {
            Map<String, URL> matches = finder.getResourcesMap("");
            if (matches != null) {
                Test<URL> resourceTest = getResourceTest(resultPath, actionName);
                for (Map.Entry<String, URL> entry : matches.entrySet()) {
                    if (resourceTest.test(entry.getValue())) {
                        log.trace("Processing URL [{}]", entry.getKey());

                        String urlStr = entry.getValue().toString();
                        int index = urlStr.lastIndexOf(resultPrefix);
                        String path = urlStr.substring(index);
                        makeResults(actionClass, path, resultPrefix, results, packageConfig, resultsByExtension);
                    }

                }
            }
        } catch (IOException ex) {
            log.error("Unable to scan directory [{}] for results", ex, classPathLocation);
        }
    }

    private Test<URL> getResourceTest(final String resultPath, final String actionName) {
        return url -> {
            String urlStr = url.toString();
            int index = urlStr.lastIndexOf(resultPath);
            String path = urlStr.substring(index + resultPath.length());
            return path.startsWith(actionName);
        };
    }

}
