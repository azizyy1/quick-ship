
package org.example.serviceclients.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerResponseDTO {

    private Long id;
    private String nom;
    private String email;
    private BigDecimal encours;
    private BigDecimal plafondCredit;
    private String statut;
}
