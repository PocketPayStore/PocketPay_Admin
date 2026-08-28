package pocketpaystore.pocketpay_admin.payment.event.listener;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.data.redis.connection.Message;

import pocketpaystore.pocketpay_admin.payment.domain.PaymentStatus;
import pocketpaystore.pocketpay_admin.payment.event.model.PaymentStatusChangedEvent;
import pocketpaystore.pocketpay_admin.sse.service.PaymentSseService;
import tools.jackson.databind.ObjectMapper;

class RedisPaymentStatusEventListenerTest {
	private final ObjectMapper objectMapper = mock(ObjectMapper.class);
	private final PaymentSseService paymentSseService = mock(PaymentSseService.class);
	private final RedisPaymentStatusEventListener listener =
			new RedisPaymentStatusEventListener(objectMapper, paymentSseService);

	@Test
	void Redis_결제_상태_이벤트를_SSE로_전달한다() throws Exception {
		String json = "{\"eventId\":\"event-1\"}";
		Message message = mock(Message.class);
		PaymentStatusChangedEvent event = new PaymentStatusChangedEvent(
				"event-1", 1L, 2L, "ORDER-001", PaymentStatus.DONE, LocalDateTime.now());
		when(message.getBody()).thenReturn(json.getBytes(StandardCharsets.UTF_8));
		when(objectMapper.readValue(eq(json), eq(PaymentStatusChangedEvent.class))).thenReturn(event);

		listener.onMessage(message, null);

		verify(paymentSseService).send(event);
	}
}
