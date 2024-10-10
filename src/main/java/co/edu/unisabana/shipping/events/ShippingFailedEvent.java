package co.edu.unisabana.shipping.events;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShippingFailedEvent {
    private int orderId;
    private String reason;
    private String status;

    public ShippingFailedEvent(int orderId, String reason, String status) {
        this.orderId = orderId;
        this.reason = reason;
        this.status = status;
    }

}
