package pl.sirant.fts.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.sirant.fts.Address;

import java.util.UUID;

interface JpaAddressRepository extends JpaRepository<Address, UUID> {

  Page<Address> findAll(Specification<Address> specification, Pageable pageable);
}