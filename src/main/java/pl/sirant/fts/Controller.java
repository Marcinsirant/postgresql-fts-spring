package pl.sirant.fts;

import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.sirant.fts.repository.AddressRepository;

@RestController
@RequestMapping("api/addresses")
@RequiredArgsConstructor
public class Controller {

  private final AddressRepository addressRepository;

  @GetMapping
  public Slice<Address> page(@RequestParam String query, @ParameterObject Pageable pageable) {
    return addressRepository.findAll(query, pageable);
  }

}
