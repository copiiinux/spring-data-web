package ch.copiiinux.springdataweb.ctrl;

import ch.copiiinux.springdataweb.entity.Customer;
import ch.copiiinux.springdataweb.repository.CustomerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerRepository repository;

    public CustomerController(CustomerRepository repository) {
        this.repository = repository;
    }

    /**
     * <code>HEAD /{repository}</code>
     *
     * @return 204 if existing
     */
    @RequestMapping(method = RequestMethod.HEAD)
    public ResponseEntity<Void> headCollectionResource() {
        return ResponseEntity.noContent().build();
    }

    /**
     * <code>GET /{repository}</code> - Returns the collection resource.
     *
     * @return 200 with the collection resource
     */
    @GetMapping
    public ResponseEntity<List<Customer>> getCollectionResource() {
        return ResponseEntity.ok(repository.findAll());
    }

    /**
     * <code>POST /{repository}</code> - Creates a new entity instance from the collection resource.
     *
     * @param c the new entity instance
     * @return 201 with the new entity instance
     */
    @PostMapping
    public ResponseEntity<Customer> postCollectionResource(@RequestBody Customer c) {
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(c));
    }

    /**
     * <code>HEAD /{repository}/{id}</code>
     *
     * @param id the id of the entity to check
     * @return 204 if existing, 404 otherwise
     */
    @RequestMapping(path = "/{id}", method = RequestMethod.HEAD)
    public ResponseEntity<Void> headForItemResource(@PathVariable Long id) {
        return repository.existsById(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    /**
     * <code>GET /{repository}/{id}</code> - Returns a single entity.
     *
     * @param id the id of the entity to return
     * @return 200 with the entity, 404 otherwise
     */
    @GetMapping("/{id}")
    public ResponseEntity<Customer> getItemResource(@PathVariable Long id) {
        return repository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    /**
     * <code>PUT /{repository}/{id}</code> - Updates an existing entity or creates one at exactly that place.
     *
     * @param id the id of the entity to update
     * @param c  the entity to update
     * @return 200 with the updated entity, 201 if created
     */
    @PutMapping("{id}")
    public ResponseEntity<Customer> putItemResource(@PathVariable Long id, @RequestBody Customer c) {
        if (repository.existsById(id)) {
            return ResponseEntity.ok(repository.save(c));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(c));
    }

    /**
     * <code>PATCH /{repository}/{id}</code> - Updates an existing entity or creates one at exactly that place.
     *
     * @param id the id of the entity to update
     * @param c  the entity to update
     * @return 200 with the updated entity, 404 if not found
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Customer> patchItemResource(@PathVariable Long id, @RequestBody Customer c) {
        return repository.existsById(id) ? ResponseEntity.ok(repository.save(c)) : ResponseEntity.notFound().build();
    }

    /**
     * <code>DELETE /{repository}/{id}</code> - Deletes the entity backing the item resource.
     *
     * @param id the id of the entity to delete
     * @return 204 if existing, 404 otherwise
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItemResource(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
