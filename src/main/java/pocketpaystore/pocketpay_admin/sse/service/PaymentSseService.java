package pocketpaystore.pocketpay_admin.sse.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pocketpaystore.pocketpay_admin.payment.event.model.PaymentStatusChangedEvent;
import pocketpaystore.pocketpay_admin.sse.repository.PaymentSseEmitterRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentSseService {
	private final PaymentSseEmitterRepository emitterRepository;

	@Value("${sse.timeout}")
	private long timeout;

	@Value("${sse.reconnect-time}")
	private long reconnectTime;

	public SseEmitter subscribe() {
		String emitterId = UUID.randomUUID().toString();
		SseEmitter emitter = new SseEmitter(timeout);
		emitterRepository.save(emitterId, emitter);
		emitter.onCompletion(() -> emitterRepository.delete(emitterId));
		emitter.onTimeout(() -> emitterRepository.delete(emitterId));
		emitter.onError(e -> emitterRepository.delete(emitterId));

		try {
			emitter.send(SseEmitter.event()
					.name("connected")
					.reconnectTime(reconnectTime)
					.data(Map.of("connectionId", emitterId)));
		} catch (IOException e) {
			emitterRepository.delete(emitterId);
			emitter.completeWithError(e);
		}
		return emitter;
	}

	public void send(PaymentStatusChangedEvent event) {
		emitterRepository.findAll().forEach(entry -> send(entry.getKey(), entry.getValue(),
				SseEmitter.event()
						.id(event.getEventId())
						.name("payment-status")
						.data(event)));
	}

	@Scheduled(fixedDelayString = "${sse.heartbeat-interval}")
	public void sendHeartbeat() {
		emitterRepository.findAll().forEach(entry -> send(entry.getKey(), entry.getValue(),
				SseEmitter.event().name("heartbeat").data(LocalDateTime.now())));
	}

	private void send(String emitterId, SseEmitter emitter, SseEmitter.SseEventBuilder event) {
		try {
			emitter.send(event);
		} catch (IOException | IllegalStateException e) {
			log.debug("SSE 연결 종료: emitterId={}", emitterId);
			emitterRepository.delete(emitterId);
			emitter.complete();
		}
	}
}
