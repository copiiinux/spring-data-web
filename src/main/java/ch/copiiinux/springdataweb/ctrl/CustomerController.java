package ch.copiiinux.springdataweb.ctrl;

import ch.copiiinux.springdataweb.dto.request.CustomerRequestDTO;
import ch.copiiinux.springdataweb.dto.response.CustomerResponseDTO;
import ch.copiiinux.springdataweb.mapper.CustomerMapper;
import ch.copiiinux.springdataweb.repository.CustomerRepository;
import ch.copiiinux.springdataweb.validation.ValidationGroups;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers")
@AllArgsConstructor
public class CustomerController {
    private final CustomerMapper mapper;
    private final CustomerRepository repository;

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
    public ResponseEntity<List<CustomerResponseDTO>> getCollectionResource() {
        return ResponseEntity.ok(repository.findAll().stream().map(mapper::map).toList());
    }

    /**
     * <code>POST /{repository}</code> - Creates a new entity instance from the collection resource.
     *
     * @param dto the new entity instance
     * @return 201 with the new entity instance
     */
    @PostMapping
    public ResponseEntity<CustomerResponseDTO> postCollectionResource(@Validated(ValidationGroups.Full.class) @RequestBody CustomerRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.map(repository.save(mapper.map(dto))));
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
    public ResponseEntity<CustomerResponseDTO> getItemResource(@PathVariable Long id) {
        return repository.findById(id)
                         .map(mapper::map)
                         .map(ResponseEntity::ok)
                         .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * <code>PUT /{repository}/{id}</code> - Updates an existing entity or creates one at exactly that place.
     *
     * @param id  the id of the entity to update
     * @param dto the entity to update
     * @return 200 with the updated entity, 201 if created
     */
    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> putItemResource(@PathVariable Long id, @Validated(ValidationGroups.Full.class) @RequestBody CustomerRequestDTO dto) {
        return repository.findById(id).map(e -> {
            mapper.update(dto, e);
            return ResponseEntity.ok(mapper.map(repository.save(e)));
        }).orElseGet(() -> postCollectionResource(dto)); // lazy-eva
    }

    /**
     * <code>PATCH /{repository}/{id}</code> - Updates an existing entity or creates one at exactly that place.
     *
     * @param id  the id of the entity to update
     * @param dto the entity to update
     * @return 200 with the updated entity, 404 if not found
     */
    @PatchMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> patchItemResource(@PathVariable Long id, @Validated(ValidationGroups.Patch.class) @RequestBody CustomerRequestDTO dto) {
        return repository.findById(id).map(e -> {
            mapper.patch(dto, e);
            return ResponseEntity.ok(mapper.map(repository.save(e)));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * <code>DELETE /{repository}/{id}</code> - Deletes the entity backing the item resource.
     *
     * @param id the id of the entity to delete
     * @return 204 if existing, 404 otherwise
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteItemResource(@PathVariable Long id) {
        return repository.findById(id).map(e -> {
            repository.delete(e);
            return ResponseEntity.noContent().build();
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
