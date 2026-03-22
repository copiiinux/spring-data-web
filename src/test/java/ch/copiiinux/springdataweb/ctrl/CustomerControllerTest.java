package ch.copiiinux.springdataweb.ctrl;

import ch.copiiinux.springdataweb.dto.CustomerRequestDTO;
import ch.copiiinux.springdataweb.entity.Customer;
import ch.copiiinux.springdataweb.repository.CustomerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CustomerControllerTest {
    private static final String URI_TEMPLATE = "/customers";
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private CustomerRepository repository;
    private Customer customer;

    @BeforeEach
    void setUp() {
        // The CommandLineRunner seeds rows on startup — wipe them first so every
        // test starts from a known, empty state.
        repository.deleteAll();
        Customer c = new Customer();
        c.setName("Jane Doe");
        customer = repository.save(c);
    }

    @AfterEach
    void tearDown() {
        repository.deleteAll();
    }
    // -------------------------------------------------------------------------
    // HEAD /customers
    // -------------------------------------------------------------------------

    @Test
    void headCollectionResource_shouldReturn204() throws Exception {
        mockMvc.perform(head(URI_TEMPLATE)).andExpect(status().isNoContent());
    }
    // -------------------------------------------------------------------------
    // GET /customers
    // -------------------------------------------------------------------------

    @Test
    void getCollectionResource_shouldReturnAllCustomers() throws Exception {
        mockMvc.perform(get(URI_TEMPLATE).accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].id").value(customer.getId()))
               .andExpect(jsonPath("$[0].name").value(customer.getName()));
    }

    @Test
    void getCollectionResource_shouldReturnEmptyListWhenNoCustomers() throws Exception {
        repository.deleteAll();
        mockMvc.perform(get(URI_TEMPLATE).accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$").isEmpty());
    }
    // -------------------------------------------------------------------------
    // POST /customers
    // -------------------------------------------------------------------------

    @Test
    void postCollectionResource_shouldCreateAndReturn201() throws Exception {
        CustomerRequestDTO dto = new CustomerRequestDTO("John Doe");
        mockMvc.perform(post(URI_TEMPLATE).contentType(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(dto)))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.id").isNotEmpty())
               .andExpect(jsonPath("$.name").value("John Doe"));
        assertThat(repository.count()).isEqualTo(2); // setUp customer + new one
    }

    @Test
    void postCollectionResource_shouldReturn400WhenNameIsNull() throws Exception {
        mockMvc.perform(post(URI_TEMPLATE).contentType(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(new CustomerRequestDTO(null))))
               .andExpect(status().isBadRequest());
        assertThat(repository.count()).isEqualTo(1); // unchanged
    }

    @Test
    void postCollectionResource_shouldReturn400WhenNameIsTooShort() throws Exception {
        mockMvc.perform(post(URI_TEMPLATE).contentType(MediaType.APPLICATION_JSON)
                                          .content(objectMapper.writeValueAsString(new CustomerRequestDTO("x"))))
               .andExpect(status().isBadRequest());
        assertThat(repository.count()).isEqualTo(1); // unchanged
    }
    // -------------------------------------------------------------------------
    // HEAD /customers/{id}
    // -------------------------------------------------------------------------

    @Test
    void headForItemResource_shouldReturn204WhenExists() throws Exception {
        mockMvc.perform(head(URI_TEMPLATE + "/" + customer.getId())).andExpect(status().isNoContent());
    }

    @Test
    void headForItemResource_shouldReturn404WhenNotExists() throws Exception {
        mockMvc.perform(head(URI_TEMPLATE + "/999")).andExpect(status().isNotFound());
    }
    // -------------------------------------------------------------------------
    // GET /customers/{id}
    // -------------------------------------------------------------------------

    @Test
    void getItemResource_shouldReturnCustomerWhenFound() throws Exception {
        mockMvc.perform(get(URI_TEMPLATE + "/" + customer.getId()).accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(customer.getId()))
               .andExpect(jsonPath("$.name").value(customer.getName()));
    }

    @Test
    void getItemResource_shouldReturn404WhenNotFound() throws Exception {
        mockMvc.perform(get(URI_TEMPLATE + "/999").accept(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
    }
    // -------------------------------------------------------------------------
    // PUT /customers/{id}
    // -------------------------------------------------------------------------

    @Test
    void putItemResource_shouldUpdateAndReturn200WhenExists() throws Exception {
        CustomerRequestDTO dto = new CustomerRequestDTO("Updated Name");
        mockMvc.perform(put(URI_TEMPLATE + "/" + customer.getId()).contentType(MediaType.APPLICATION_JSON)
                                                                  .content(objectMapper.writeValueAsString(dto)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(customer.getId()))
               .andExpect(jsonPath("$.name").value("Updated Name"));
        assertThat(repository.findById(customer.getId())).isPresent()
                                                         .get()
                                                         .extracting(Customer::getName)
                                                         .isEqualTo("Updated Name");
        assertThat(repository.count()).isEqualTo(1); // unchanged
    }

    @Test
    void putItemResource_shouldCreateAndReturn201WhenNotFound() throws Exception {
        CustomerRequestDTO dto = new CustomerRequestDTO("Brand New");
        mockMvc.perform(put(URI_TEMPLATE + "/999").contentType(MediaType.APPLICATION_JSON)
                                                  .content(objectMapper.writeValueAsString(dto)))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.name").value("Brand New"));
        assertThat(repository.count()).isEqualTo(2); // setUp customer + new one
    }

    @Test
    void putItemResource_shouldReturn400WhenNameIsNull() throws Exception {
        mockMvc.perform(put(URI_TEMPLATE + "/" + customer.getId()).contentType(MediaType.APPLICATION_JSON)
                                                                  .content(objectMapper.writeValueAsString(new CustomerRequestDTO(null))))
               .andExpect(status().isBadRequest());
        assertThat(repository.count()).isEqualTo(1); // unchanged
    }

    @Test
    void putItemResource_shouldReturn400WhenNameIsTooShort() throws Exception {
        mockMvc.perform(put(URI_TEMPLATE + "/" + customer.getId()).contentType(MediaType.APPLICATION_JSON)
                                                                  .content(objectMapper.writeValueAsString(new CustomerRequestDTO("x"))))
               .andExpect(status().isBadRequest());
        assertThat(repository.count()).isEqualTo(1); // unchanged
    }
    // -------------------------------------------------------------------------
    // PATCH /customers/{id}
    // -------------------------------------------------------------------------

    @Test
    void patchItemResource_shouldUpdateAndReturn200WhenExists() throws Exception {
        CustomerRequestDTO dto = new CustomerRequestDTO("Patched Name");
        mockMvc.perform(patch(URI_TEMPLATE + "/" + customer.getId()).contentType(MediaType.APPLICATION_JSON)
                                                                    .content(objectMapper.writeValueAsString(dto)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(customer.getId()))
               .andExpect(jsonPath("$.name").value("Patched Name"));
        assertThat(repository.findById(customer.getId())).isPresent()
                                                         .get()
                                                         .extracting(Customer::getName)
                                                         .isEqualTo("Patched Name");
        assertThat(repository.count()).isEqualTo(1); // unchanged
    }

    @Test
    void patchItemResource_shouldNotOverwriteFieldWhenNull() throws Exception {
        // null name in patch DTO — existing "Jane Doe" must be preserved
        mockMvc.perform(patch(URI_TEMPLATE + "/" + customer.getId()).contentType(MediaType.APPLICATION_JSON)
                                                                    .content(objectMapper.writeValueAsString(new CustomerRequestDTO(null))))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.name").value("Jane Doe"));
        assertThat(repository.findById(customer.getId())).isPresent()
                                                         .get()
                                                         .extracting(Customer::getName)
                                                         .isEqualTo("Jane Doe");
        assertThat(repository.count()).isEqualTo(1); // unchanged
    }

    @Test
    void patchItemResource_shouldReturn404WhenNotExists() throws Exception {
        mockMvc.perform(patch(URI_TEMPLATE + "/999").contentType(MediaType.APPLICATION_JSON)
                                                    .content(objectMapper.writeValueAsString(new CustomerRequestDTO("Jane Doe"))))
               .andExpect(status().isNotFound());
        assertThat(repository.count()).isEqualTo(1); // unchanged
    }

    @Test
    void patchItemResource_shouldReturn400WhenNameIsTooShort() throws Exception {
        mockMvc.perform(patch(URI_TEMPLATE + "/" + customer.getId()).contentType(MediaType.APPLICATION_JSON)
                                                                    .content(objectMapper.writeValueAsString(new CustomerRequestDTO("x"))))
               .andExpect(status().isBadRequest());
        assertThat(repository.count()).isEqualTo(1); // unchanged
    }
    // -------------------------------------------------------------------------
    // DELETE /customers/{id}
    // -------------------------------------------------------------------------

    @Test
    void deleteItemResource_shouldReturn204AndRemoveCustomer() throws Exception {
        mockMvc.perform(delete(URI_TEMPLATE + "/" + customer.getId())).andExpect(status().isNoContent());
        assertThat(repository.existsById(customer.getId())).isFalse();
        assertThat(repository.count()).isZero(); // deleted
    }

    @Test
    void deleteItemResource_shouldReturn404WhenNotExists() throws Exception {
        mockMvc.perform(delete(URI_TEMPLATE + "/999")).andExpect(status().isNotFound());
        assertThat(repository.count()).isEqualTo(1); // unchanged
    }
}