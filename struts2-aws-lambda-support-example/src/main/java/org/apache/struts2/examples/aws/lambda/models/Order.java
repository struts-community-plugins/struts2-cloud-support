package org.apache.struts2.examples.aws.lambda.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order implements Serializable {

    String id;

    @NotBlank(message = "validation.order.client")
    String clientName;

    @Min(value = 10, message = "validation.order.amount")
    @Max(value = 666, message = "validation.order.amount")
    int amount;
}
