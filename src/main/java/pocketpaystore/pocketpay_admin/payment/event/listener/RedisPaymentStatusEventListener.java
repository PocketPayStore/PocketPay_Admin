package pocketpaystore.pocketpay_admin.payment.event.listener;

import java.nio.charset.StandardCharsets;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pocketpaystore.pocketpay_admin.payment.event.model.PaymentStatusChangedEvent;
import pocketpaystore.pocketpay_admin.sse.service.PaymentSseService;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisPaymentStatusEventListener implements MessageListener {
	private final ObjectMapper objectMapper;
	private final PaymentSseService paymentSseService;

	@Override
	public void onMessage(Message message, byte[] pattern) {
		try {
			PaymentStatusChangedEvent event = objectMapper.readValue(
					new String(message.getBody(), StandardCharsets.UTF_8),
					PaymentStatusChangedEvent.class);
			paymentSseService.send(event);
		} catch (Exception e) {
			log.error("결제 상태 이벤트 처리 실패", e);
		}
	}
}
