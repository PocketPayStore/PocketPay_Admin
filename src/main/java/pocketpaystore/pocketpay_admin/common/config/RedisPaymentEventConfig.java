package pocketpaystore.pocketpay_admin.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.scheduling.annotation.EnableScheduling;

import pocketpaystore.pocketpay_admin.payment.event.listener.RedisPaymentStatusEventListener;

@Configuration
@EnableScheduling
public class RedisPaymentEventConfig {
	@Bean
	@ConditionalOnProperty(name = "payment-events.enabled", havingValue = "true")
	public RedisMessageListenerContainer redisMessageListenerContainer(
			RedisConnectionFactory connectionFactory,
			RedisPaymentStatusEventListener listener,
			@Value("${payment-events.channel}") String channel) {
		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.addMessageListener(listener, new ChannelTopic(channel));
		return container;
	}
}
