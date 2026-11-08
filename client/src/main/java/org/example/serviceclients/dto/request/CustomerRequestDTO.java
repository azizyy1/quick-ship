package org.example.serviceclients.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRequestDTO {

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    @Email(message = "Email invalide")
    @NotBlank(message = "L'email est obligatoire")
    private String email;

    @NotNull(message = "Le plafond est obligatoire")
    @PositiveOrZero(message = "Le plafond doit être positif")
    private BigDecimal plafondCredit;

    @NotBlank(message = "Le statut est obligatoire")
    private String statut;
}
