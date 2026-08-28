package pocketpaystore.pocketpay_admin.sse.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import pocketpaystore.pocketpay_admin.payment.domain.PaymentStatus;
import pocketpaystore.pocketpay_admin.payment.event.model.PaymentStatusChangedEvent;
import pocketpaystore.pocketpay_admin.sse.repository.PaymentSseEmitterRepository;

class PaymentSseServiceTest {
	private PaymentSseEmitterRepository emitterRepository;
	private PaymentSseService paymentSseService;

	@BeforeEach
	void setUp() {
		emitterRepository = new PaymentSseEmitterRepository();
		paymentSseService = new PaymentSseService(emitterRepository);
		ReflectionTestUtils.setField(paymentSseService, "timeout", 30_000L);
		ReflectionTestUtils.setField(paymentSseService, "reconnectTime", 3_000L);
	}

	@Test
	void SSE_연결을_생성하고_관리한다() {
		SseEmitter emitter = paymentSseService.subscribe();

		assertThat(emitter).isNotNull();
		assertThat(emitterRepository.count()).isEqualTo(1);
	}

	@Test
	void 전송에_실패한_SSE_연결을_제거한다() {
		emitterRepository.save("failed-emitter", new FailingSseEmitter());
		PaymentStatusChangedEvent event = new PaymentStatusChangedEvent(
				"event-2", 1L, 2L, "ORDER-001", PaymentStatus.DONE, LocalDateTime.now());

		paymentSseService.send(event);

		assertThat(emitterRepository.count()).isZero();
	}

	private static class FailingSseEmitter extends SseEmitter {
		@Override
		public void send(SseEventBuilder builder) throws IOException {
			throw new IOException("연결 종료");
		}
	}
}
