package pl.sirant.fts.repository;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import pl.sirant.fts.Address;
import pl.sirant.fts.Address_;

@Repository
@RequiredArgsConstructor
class AddressRepo implements AddressRepository {

  private final JpaAddressRepository jpaAddressRepository;

  public Slice<Address> findAll(String query, Pageable pageable) {
    return jpaAddressRepository.findAll(new TextSearchableSpecification(query), pageable);
  }

  private record TextSearchableSpecification(String query) implements Specification<Address> {
    @Override
    public Predicate toPredicate(Root<Address> root, CriteriaQuery<?> q, CriteriaBuilder cb) {
      if (query == null || query.isBlank()) {
        return cb.conjunction();
      }
      return cb.or(
          cb.isTrue(
              cb.function(
                  "tsvector_match",
                  Boolean.class,
                  root.get(Address_.textSearchable),
                  cb.literal(query)
              )
          )
      );
    }
  }

}
