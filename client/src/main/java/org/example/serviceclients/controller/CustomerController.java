package org.example.serviceclients.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.serviceclients.dto.request.CustomerRequestDTO;
import org.example.serviceclients.dto.response.CustomerResponseDTO;
import org.example.serviceclients.service.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerResponseDTO createCustomer(
            @Valid @RequestBody CustomerRequestDTO request) {

        return customerService.create(request);
    }

    @GetMapping
    public List<CustomerResponseDTO> getAllCustomers() {

        return customerService.getAll();
    }

    @GetMapping("/{id}")
    public CustomerResponseDTO getCustomerById(
            @PathVariable Long id) {

        return customerService.getById(id);
    }

    @PutMapping("/{id}")
    public CustomerResponseDTO updateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody CustomerRequestDTO request) {

        return customerService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCustomer(
            @PathVariable Long id) {

        customerService.delete(id);
    }
}