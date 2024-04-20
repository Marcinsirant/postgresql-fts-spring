package pl.sirant.fts;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "addresses")
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address {

  @Id
  @UuidGenerator
  @Column(name = "address_id")
  private UUID id;

  @Column(name = "street")
  private String street;

  @Column(name = "city")
  private String city;

  @Column(name = "postal_code")
  private String postalCode;

  @Column(name = "building_no")
  private String buildingNo;

  @Column(name = "house_no")
  private String houseNo;

  @Column(name = "note")
  private String note;

  @JsonIgnore
  @Column(name = "text_searchable", columnDefinition = "tsvector", insertable = false, updatable = false)
  private String textSearchable;

}
