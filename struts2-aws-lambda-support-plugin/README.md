# Struts2 Cloud Support Plugin

This plugin allows you to write AWS Lambda function with REST based Apache Struts 2 applications.

### Usage

Add lambda plugin to your dependencies.

```xml
        <dependency>
            <groupId>com.jgeppert.struts2</groupId>
            <artifactId>struts2-aws-lambda-support-plugin</artifactId>
            <version>1.4.1</version>
        </dependency>
```

### Configure Application

#### Example struts.xml configuration file

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE struts PUBLIC
        "-//Apache Software Foundation//DTD Struts Configuration 2.5//EN"
        "http://struts.apache.org/dtds/struts-2.5.dtd">
<struts>

    <constant name="struts.enable.DynamicMethodInvocation" value="false"/>

    <!-- Disable dev mode in productions -->
    <constant name="struts.devMode" value="false"/>
    <constant name="struts.handle.exception" value="false"/>

    <!--  Overwrite Convention -->
    <constant name="struts.convention.action.suffix" value="Controller"/>
    <constant name="struts.convention.action.mapAllMatches" value="true"/>
    <constant name="struts.convention.package.locators" value="actions"/>
    <constant name="struts.convention.default.parent.package" value="data"/>
    <constant name="struts.action.extension" value=",,xml,json,action"/>
    <constant name="struts.rest.defaultExtension" value="json"/>

    <constant name="struts.custom.i18n.resources" value="frontend,validation,exceptions"/>

    <constant name="struts.mapper.class" value="rest"/>

    <!-- Use Jackson lib as content handler for all JSON requests -->
    <bean type="org.apache.struts2.rest.handler.ContentTypeHandler"
          name="jackson"
          class="org.apache.struts2.rest.handler.JacksonJsonHandler"/>
    <constant name="struts.rest.handlerOverride.json" value="jackson"/>

    <!-- Set to false if the json content can be returned for any kind of http method -->
    <constant name="struts.rest.content.restrictToGET" value="false"/>

    <!-- Set custom validation failure status code -->
    <constant name="struts.rest.validationFailureStatusCode" value="406"/>

    <!-- Allow public access for demo purposes. Should be specified in production! -->
    <constant name="struts.corsHeader.allowOrigin" value="*"/>

    <!-- Set encoding to UTF-8, default is ISO-8859-1 -->
    <constant name="struts.i18n.encoding" value="UTF-8"/>

    <package name="data" extends="struts-lambda-support" namespace="/data">
    </package>

</struts>
```

Create a package **com.mycompany.myapp.actions.data** and place a **package-info.java** with following content:

```java
@ParentPackage("data")
@Namespace("/data")
package com.mycompany.myapp.actions.data;

import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
```

Now you can start creating a Struts2 REST Controller:

```java
package com.mycompany.myapp.actions.data;

import com.opensymphony.xwork2.ModelDriven;
import lombok.extern.log4j.Log4j2;
import org.apache.struts2.examples.aws.lambda.models.Order;
import org.apache.struts2.examples.aws.lambda.services.OrdersService;
import org.apache.struts2.rest.DefaultHttpHeaders;
import org.apache.struts2.rest.HttpHeaders;
import org.apache.struts2.rest.RestActionSupport;

import java.util.Collection;

@Log4j2
public class OrderController extends RestActionSupport implements ModelDriven<Object> {

    private static final long serialVersionUID = 3772072430797186L;

    private Order model = new Order();
    private String id;
    private Collection<Order> list = null;
    private final OrdersService ordersService = new OrdersService();

    // GET /data/order/1
    public HttpHeaders show() {
        return new DefaultHttpHeaders("show");
    }

    // GET /data/order
    public HttpHeaders index() {
        list = ordersService.getAll();
        return new DefaultHttpHeaders("index")
                .disableCaching();
    }

    // DELETE /data/order/1
    public String destroy() {
        log.info("Delete order with id: {}", id);
        ordersService.remove(id);
        return SUCCESS;
    }

    // POST /data/order
    public HttpHeaders create() {
        log.debug("Create new order: {}", model);
        if (ordersService.getAll().stream().anyMatch(o -> o.getClientName().equalsIgnoreCase(model.getClientName()))) {
            throw new RuntimeException(getText("exception.client.already.exists"));
        }
        ordersService.save(model);
        return new DefaultHttpHeaders("success")
                .setLocationId(model.getId());
    }

    // PUT /data/order/1
    public String update() {
        log.debug("Update order: {}", model);
        ordersService.save(model);
        return SUCCESS;
    }

    public void setId(String id) {
        if (id != null) {
            this.model = ordersService.get(id);
        }
        this.id = id;
    }

    public Object getModel() {
        if (list != null) {
            return list;
        } else {
            if (model == null) {
                model = new Order();
            }
            return model;
        }
    }
}
```

### Assembly Lambda ZIP

In maven applications the assembly plugin will help to build a Lambda zip file for deployment

#### Maven configuration
```xml
    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-assembly-plugin</artifactId>
                <version>3.1.0</version>
                <configuration>
                    <descriptors>
                        <descriptor>src/main/assembly/dist.xml</descriptor>
                    </descriptors>
                </configuration>
                <executions>
                    <execution>
                        <id>lambda</id>
                        <phase>package</phase>
                        <goals>
                            <goal>single</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
```

#### Assembly plugin descriptor
```xml
<assembly xmlns="http://maven.apache.org/plugins/maven-assembly-plugin/assembly/1.1.2"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/plugins/maven-assembly-plugin/assembly/1.1.2 http://maven.apache.org/xsd/assembly-1.1.2.xsd">
    <id>lambda</id>
    <formats>
        <format>zip</format>
    </formats>
    <includeBaseDirectory>false</includeBaseDirectory>
    <dependencySets>
        <dependencySet>
            <outputDirectory>lib</outputDirectory>
            <useProjectArtifact>false</useProjectArtifact>
        </dependencySet>
    </dependencySets>
    <fileSets>
        <fileSet>
            <directory>${basedir}/src/main/resources</directory>
            <outputDirectory>/</outputDirectory>
            <includes>
                <include>*</include>
            </includes>
        </fileSet>
        <fileSet>
            <directory>${project.build.directory}/classes</directory>
            <outputDirectory>/</outputDirectory>
            <includes>
                <include>**/*.class</include>
            </includes>
        </fileSet>
    </fileSets>
</assembly>
```

### Deploy as AWS Lambda function 

1. Run **mvn install** to build ZIP file
2. In AWS Console or over AWS CLI deploy generated ZIP file
3. Use _com.amazonaws.serverless.proxy.struts.StrutsLambdaHandler::handleRequest_ as Handler

### Optional API Gateway configuration

1. Open AWS API Gateway configuration and create new API
2. Create _resources_ and _methods_ matching to your namespace/action/method
3. Open METHOD (GET/POST/PUT/DELETE) and click on _Integration Request_
4. Select **Lambda Function** as _Integration Type_
5. **Check true** the _Use Lambda Proxy Integration_
6. Select your deployed Struts2 Lambda in _Lambda Function_
7. Go back and click _Test_ in the Client section.
8. Enter following headers in the Headers text area and execute

```
Accept:application/json
Content-Type:application/json;charset=UTF-8
```