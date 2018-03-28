package org.apache.struts2.examples.aws.lambda.actions.data;

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
    private OrdersService ordersService = new OrdersService();

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
