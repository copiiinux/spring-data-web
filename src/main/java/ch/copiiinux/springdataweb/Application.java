package ch.copiiinux.springdataweb;

import ch.copiiinux.springdataweb.dto.request.CustomerRequestDTO;
import ch.copiiinux.springdataweb.mapper.CustomerMapper;
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
    CommandLineRunner runner(final CustomerRepository repository, CustomerMapper customerMapper) {
        return _ -> {
            List<CustomerRequestDTO> results = new ArrayList<>();
            results.add(new CustomerRequestDTO("Abraham Lincoln"));
            results.add(new CustomerRequestDTO("Béatrice de Cuir"));
            results.add(new CustomerRequestDTO("Cindy Green"));
            results.add(new CustomerRequestDTO("Fiona Green"));
            results.add(new CustomerRequestDTO("Débora Hoffman"));
            repository.saveAll(results.stream().map(customerMapper::map).toList());
        };
    }
}
