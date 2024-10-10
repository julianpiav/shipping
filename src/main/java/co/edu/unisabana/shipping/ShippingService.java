package co.edu.unisabana.shipping;

import co.edu.unisabana.shipping.events.OrderShippedEvent;
import co.edu.unisabana.shipping.events.PaymentSuccessfulEvent;
import co.edu.unisabana.shipping.entities.Venta;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class ShippingService {

    private final KafkaTemplate<String, Object> kafkaTemplate;  // Object para manejar diferentes tipos de eventos
    private final VentaRepository ventaRepository;

    @Autowired
    public ShippingService(KafkaTemplate<String, Object> kafkaTemplate,
                           VentaRepository ventaRepository) {
        this.kafkaTemplate = kafkaTemplate;
        this.ventaRepository = ventaRepository;
    }

    @KafkaListener(topics = "PaymentSuccesful", groupId = "shipping")
    public void handlePaymentSuccessful(PaymentSuccessfulEvent event) {
        Optional<Venta> ventaOpt = ventaRepository.findById(event.getOrderId());

        if (ventaOpt.isPresent()) {
            Venta venta = ventaOpt.get();
            processShipping(venta);
        } else {
            System.out.println("Venta no encontrada para el orderId: " + event.getOrderId());
        }
    }

    public void processShipping(Venta venta) {
        String trackingNumber = generateTrackingNumber();
        venta.setEstado("enviado");
        ventaRepository.save(venta);

        kafkaTemplate.send("OrderShipped", new OrderShippedEvent(venta.getIdCompra(), trackingNumber, "shipped"));
    }

    private String generateTrackingNumber() {
        return "TRACK" + System.currentTimeMillis();
    }

    public Optional<Venta> getVentaById(int orderId) {
        return ventaRepository.findById(orderId);
    }
}
