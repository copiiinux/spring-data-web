package ch.copiiinux.springdataweb;

import ch.copiiinux.springdataweb.entity.Customer;
import ch.copiiinux.springdataweb.repository.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class Application {

    static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    CommandLineRunner runner(final CustomerRepository repository) {
        return _ -> {
            List<Customer> results = new ArrayList<>();
            Customer c = new Customer();
            c.setName("aa");
            results.add(c);
            c.setName("cc");
            results.add(c);
            c.setName("bb");
            results.add(c);
            c.setName("ee");
            results.add(c);
            c.setName("dd");
            results.add(c);
            repository.saveAll(results);
        };
    }

}
