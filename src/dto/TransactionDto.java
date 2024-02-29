package dto;

import java.io.Serializable;
import java.math.BigDecimal;

public class TransactionDto implements Serializable {

    private String queue;

    private String operation;

    private BigDecimal value;

    public String getQueue() {
        return this.queue;
    }

    public String getOperation() {
        return this.operation;
    }

    public BigDecimal getValue() {
        return this.value;
    }

    public void setQueue(String name) {
        this.queue = name;
    }

    public void setOperation(String op) {
        this.operation = op;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

}
