package ch.copiiinux.springdataweb.repository;

import ch.copiiinux.springdataweb.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
