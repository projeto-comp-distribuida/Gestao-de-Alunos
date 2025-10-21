package com.distrischool.student.repository;

import com.distrischool.student.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para Address
 */
@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    /**
     * Busca endereços por CEP
     */
    List<Address> findByZipcode(String zipcode);

    /**
     * Busca endereços por cidade
     */
    List<Address> findByCityIgnoreCase(String city);

    /**
     * Busca endereços por estado
     */
    List<Address> findByState(String state);

    /**
     * Busca endereços por cidade e estado
     */
    List<Address> findByCityIgnoreCaseAndState(String city, String state);

    /**
     * Busca endereços primários
     */
    List<Address> findByIsPrimaryTrue();

    /**
     * Busca endereços por tipo
     */
    List<Address> findByAddressType(Address.AddressType addressType);

    /**
     * Busca endereços completos
     */
    @Query("SELECT a FROM Address a WHERE " +
           "LOWER(a.street) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(a.neighborhood) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(a.city) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Address> searchAddresses(@Param("search") String search);
}

