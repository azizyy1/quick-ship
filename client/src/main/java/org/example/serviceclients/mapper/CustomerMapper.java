
package org.example.serviceclients.mapper;

import org.example.serviceclients.dto.request.CustomerRequestDTO;
import org.example.serviceclients.dto.response.CustomerResponseDTO;
import org.example.serviceclients.entity.Customer;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class CustomerMapper {

    public Customer toEntity(CustomerRequestDTO dto) {

        return Customer.builder()
                .nom(dto.getNom())
                .email(dto.getEmail())
                .encours(BigDecimal.ZERO)
                .plafondCredit(dto.getPlafondCredit())
                .statut(dto.getStatut())
                .build();
    }

    public CustomerResponseDTO toResponse(Customer customer) {

        return CustomerResponseDTO.builder()
                .id(customer.getId())
                .nom(customer.getNom())
                .email(customer.getEmail())
                .encours(customer.getEncours())
                .plafondCredit(customer.getPlafondCredit())
                .statut(customer.getStatut())
                .build();
    }
}
