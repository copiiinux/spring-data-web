package ch.copiiinux.springdataweb.dto.request;

import ch.copiiinux.springdataweb.validation.ValidationGroups;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Single write DTO for all mutating operations.
 * <ul>
 *   <li><b>POST / PUT</b>: validate with {@code ValidationGroups.Full}.</li>
 *   <li><b>PATCH</b>: validate with {@code ValidationGroups.Patch}.</li>
 * </ul>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRequestDTO {
    @NotNull(groups = ValidationGroups.Full.class)
    @Size(min = 2, max = 64, groups = {ValidationGroups.Full.class, ValidationGroups.Patch.class})
    private String name;
}