package org.prgrms.dev.customer.service;

import org.prgrms.dev.customer.domain.Customer;
import org.prgrms.dev.customer.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<Customer> blackList() {
        return customerRepository.findAllInBlackList();
    }
}