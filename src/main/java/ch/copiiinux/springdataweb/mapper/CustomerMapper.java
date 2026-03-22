package ch.copiiinux.springdataweb.mapper;

import ch.copiiinux.springdataweb.dto.request.CustomerRequestDTO;
import ch.copiiinux.springdataweb.dto.response.CustomerResponseDTO;
import ch.copiiinux.springdataweb.entity.Customer;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    /**
     * Entity → ResponseDTO.
     */
    CustomerResponseDTO map(Customer customer);

    /**
     * RequestDTO → Entity (id stays null — set by JPA).
     */
    Customer map(CustomerRequestDTO dto);

    /**
     * Apply a full-write DTO onto an existing entity (PUT / upsert).
     * All fields are overwritten.
     */
    void update(CustomerRequestDTO dto, @MappingTarget Customer customer);

    /**
     * Apply a patch DTO onto an existing entity (PATCH).
     * Null fields in the DTO are ignored — only non-null values are copied.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patch(CustomerRequestDTO dto, @MappingTarget Customer customer);
}
