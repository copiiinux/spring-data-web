package ch.copiiinux.springdataweb.ctrl;

import ch.copiiinux.springdataweb.entity.Customer;
import ch.copiiinux.springdataweb.repository.CustomerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {
    public static final String URI_TEMPLATE = "/customers";
    private final ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private CustomerRepository repository;
    private Customer customer;

    public CustomerControllerTest() {
        this.objectMapper = new ObjectMapper();
    }

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);
        customer.setName("Jane Doe");
        // Add any other fields your Customer entity has
    }

    // -------------------------------------------------------------------------
    // HEAD /URI_TEMPLATE
    // -------------------------------------------------------------------------

    @Test
    void headCollectionResource_shouldReturn204() throws Exception {
        mockMvc.perform(head(URI_TEMPLATE)).andExpect(status().isNoContent());
    }

    // -------------------------------------------------------------------------
    // GET /URI_TEMPLATE
    // -------------------------------------------------------------------------

    @Test
    void getCollectionResource_shouldReturnListOfCustomers() throws Exception {
        when(repository.findAll()).thenReturn(List.of(customer));

        mockMvc.perform(get(URI_TEMPLATE).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(customer.getId()))
                .andExpect(jsonPath("$[0].name").value(customer.getName()));
    }

    @Test
    void getCollectionResource_shouldReturnEmptyList() throws Exception {
        when(repository.findAll()).thenReturn(List.of());

        mockMvc.perform(get(URI_TEMPLATE).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    // -------------------------------------------------------------------------
    // POST /URI_TEMPLATE
    // -------------------------------------------------------------------------

    @Test
    void postCollectionResource_shouldCreateAndReturn201() throws Exception {
        when(repository.save(any(Customer.class))).thenReturn(customer);

        mockMvc.perform(post(URI_TEMPLATE).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(customer.getId()))
                .andExpect(jsonPath("$.name").value(customer.getName()));

        verify(repository, times(1)).save(any(Customer.class));
    }

    // -------------------------------------------------------------------------
    // HEAD /URI_TEMPLATE/id
    // -------------------------------------------------------------------------

    @Test
    void headForItemResource_shouldReturn204WhenExists() throws Exception {
        when(repository.existsById(1L)).thenReturn(true);

        mockMvc.perform(head(URI_TEMPLATE + "/1")).andExpect(status().isNoContent());
    }

    @Test
    void headForItemResource_shouldReturn404WhenNotExists() throws Exception {
        when(repository.existsById(99L)).thenReturn(false);

        mockMvc.perform(head(URI_TEMPLATE + "/99")).andExpect(status().isNotFound());
    }

    // -------------------------------------------------------------------------
    // GET /URI_TEMPLATE/id
    // -------------------------------------------------------------------------

    @Test
    void getItemResource_shouldReturnCustomerWhenFound() throws Exception {
        when(repository.findById(1L)).thenReturn(Optional.of(customer));

        mockMvc.perform(get(URI_TEMPLATE + "/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(customer.getId()))
                .andExpect(jsonPath("$.name").value(customer.getName()));
    }

    @Test
    void getItemResource_shouldReturn404WhenNotFound() throws Exception {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get(URI_TEMPLATE + "/99").accept(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
    }

    // -------------------------------------------------------------------------
    // PUT /URI_TEMPLATE/id
    // -------------------------------------------------------------------------

    @Test
    void putItemResource_shouldUpdateAndReturn200WhenExists() throws Exception {
        when(repository.existsById(1L)).thenReturn(true);
        when(repository.save(any(Customer.class))).thenReturn(customer);

        mockMvc.perform(put(URI_TEMPLATE + "/1").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(customer.getId()))
                .andExpect(jsonPath("$.name").value(customer.getName()));

        verify(repository, times(1)).save(any(Customer.class));
    }

    @Test
    void putItemResource_shouldCreateAndReturn201WhenNotFound() throws Exception {
        when(repository.existsById(99L)).thenReturn(false);

        mockMvc.perform(put(URI_TEMPLATE + "/99").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customer))).andExpect(status().isCreated());

        verify(repository, times(1)).save(any(Customer.class));
    }

    // -------------------------------------------------------------------------
    // PATCH /URI_TEMPLATE/id
    // -------------------------------------------------------------------------

    @Test
    void patchItemResource_shouldUpdateAndReturn200WhenExists() throws Exception {
        when(repository.existsById(1L)).thenReturn(true);
        when(repository.save(any(Customer.class))).thenReturn(customer);

        mockMvc.perform(patch(URI_TEMPLATE + "/1").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(customer.getId()))
                .andExpect(jsonPath("$.name").value(customer.getName()));

        verify(repository, times(1)).save(any(Customer.class));
    }

    @Test
    void patchItemResource_shouldReturn404WhenNotExists() throws Exception {
        when(repository.existsById(99L)).thenReturn(false);

        mockMvc.perform(patch(URI_TEMPLATE + "/99").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customer))).andExpect(status().isNotFound());

        verify(repository, never()).save(any(Customer.class));
    }

    // -------------------------------------------------------------------------
    // DELETE /URI_TEMPLATE/id
    // -------------------------------------------------------------------------

    @Test
    void deleteItemResource_shouldReturn204WhenDeleted() throws Exception {
        when(repository.existsById(1L)).thenReturn(true);
        doNothing().when(repository).deleteById(1L);

        mockMvc.perform(delete(URI_TEMPLATE + "/1")).andExpect(status().isNoContent());

        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    void deleteItemResource_shouldReturn404WhenNotExists() throws Exception {
        when(repository.existsById(99L)).thenReturn(false);

        mockMvc.perform(delete(URI_TEMPLATE + "/99")).andExpect(status().isNotFound());

        verify(repository, never()).deleteById(any());
    }
}
