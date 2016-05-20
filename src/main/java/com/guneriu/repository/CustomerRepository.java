package com.guneriu.repository;

import com.guneriu.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by ugur on 20.05.2016.
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
