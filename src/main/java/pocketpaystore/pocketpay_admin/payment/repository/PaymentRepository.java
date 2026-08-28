package pocketpaystore.pocketpay_admin.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import pocketpaystore.pocketpay_admin.payment.domain.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long>, PaymentRepositoryCustom {
}
