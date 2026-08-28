package pocketpaystore.pocketpay_admin.sse.repository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Component
public class PaymentSseEmitterRepository {
	private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

	public void save(String emitterId, SseEmitter emitter) {
		emitters.put(emitterId, emitter);
	}

	public List<Map.Entry<String, SseEmitter>> findAll() {
		return List.copyOf(emitters.entrySet());
	}

	public void delete(String emitterId) {
		emitters.remove(emitterId);
	}

	public int count() {
		return emitters.size();
	}
}
