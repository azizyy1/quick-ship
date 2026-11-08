
package org.example.serviceclients.service;

import org.example.serviceclients.dto.request.CustomerRequestDTO;
import org.example.serviceclients.dto.response.CustomerResponseDTO;
import org.example.serviceclients.entity.Customer;
import org.example.serviceclients.exception.BusinessException;
import org.example.serviceclients.exception.ResourceNotFoundException;
import org.example.serviceclients.mapper.CustomerMapper;
import org.example.serviceclients.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository repository;
    private final CustomerMapper mapper;

    public CustomerResponseDTO create(CustomerRequestDTO dto) {

        if (repository.existsByEmail(dto.getEmail())) {
            throw new BusinessException(
                    "Un client avec cet email existe déjà");
        }

        Customer customer = mapper.toEntity(dto);

        return mapper.toResponse(
                repository.save(customer)
        );
    }

    public List<CustomerResponseDTO> getAll() {

        return repository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    public CustomerResponseDTO getById(Long id) {

        Customer customer = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Client introuvable"));

        return mapper.toResponse(customer);
    }

    public CustomerResponseDTO update(
            Long id,
            CustomerRequestDTO dto) {

        Customer customer = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Client introuvable"));

        customer.setNom(dto.getNom());
        customer.setEmail(dto.getEmail());
        customer.setPlafondCredit(dto.getPlafondCredit());
        customer.setStatut(dto.getStatut());

        return mapper.toResponse(
                repository.save(customer)
        );
    }

    public void delete(Long id) {

        Customer customer = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Client introuvable"));

        repository.delete(customer);
    }
}
