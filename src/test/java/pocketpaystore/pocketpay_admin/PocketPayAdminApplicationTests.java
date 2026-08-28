package pocketpaystore.pocketpay_admin;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"payment-events.enabled=false",
		"sse.timeout=30000",
		"sse.heartbeat-interval=15000",
		"sse.reconnect-time=3000"
})
class PocketPayAdminApplicationTests {

	@Test
	void contextLoads() {
	}

}
