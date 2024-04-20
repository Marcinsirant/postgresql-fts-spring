package pl.sirant.fts.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import pl.sirant.fts.Address;

public interface AddressRepository {

  Slice<Address> findAll(String query, Pageable pageable);
}
