# Full-text search in Spring with PostgreSQL

## **Understanding** Full Text Searching

Full Text Searching, often known as text search, offers the capacity to find natural language documents that match a query and, if desired, to order them according to the query's relevancy. Finding all documents that include certain query phrases and returning them in the order that they most closely resemble the query is the most popular kind of search. Query and similarity notions are very adaptable and contingent upon the particular use case. In the most basic search, similarity is defined as the frequency of query terms in the document, and the query is a list of words.

---
## Creating separate `tsvector` column and index

**`to_tsvector`** is a function used for converting text data into a special format called a ‘tsvector’, which is optimized for full-text search operations.

Add column, which provide ‘tsvector’

```sql
ALTER TABLE addresses
    ADD COLUMN text_searchable tsvector
        GENERATED ALWAYS AS (to_tsvector('english', coalesce(street, '') || ' ' ||
                                                    coalesce(city, '') || ' ' ||
                                                    postal_code || ' ' ||
                                                    coalesce(house_no, '') || ' ' ||
                                                    coalesce(note, '') || ' ' ||
                                                    coalesce(building_no, '')
                             )) STORED;
```

*`coalesce` - The COALESCE() function returns the first non-null value in a list. (be careful with special characters)*

`english` is set because the content in my columns is in English. If your content is in other language, use the appropriate language code.

---
It is possible to install a dictionary to extend **PostgreSQL Full-Text Search**.
For example, for the Polish language:
[https://github.com/dominem/postgresql_fts_polish_dict](https://github.com/dominem/postgresql_fts_polish_dict)

---

Create an index on the ‘text_searchable’ column to speed up text search

```yaml
CREATE INDEX text_searchable_idx ON t_customers_addresses USING GIN (text_searchable);
```


## Register function to hibernate

For convenience, we register the function in Hibernate and will then be able to refer to it when creating a query.

Implement `PostgreSQLDialect:` (Remember about the correct first parameter in plainto_tsquery)

```jsx
import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.dialect.PostgreSQLDialect;

public class CustomPostgreSQLDialect extends PostgreSQLDialect {
  public CustomPostgreSQLDialect() {
    super();
  }

  @Override
  public void initializeFunctionRegistry(FunctionContributions functionContributions) {
    super.initializeFunctionRegistry(functionContributions);
    var functionRegistry = functionContributions.getFunctionRegistry();
    functionRegistry.registerPattern(
        "tsvector_match",
        "(?1 @@ plainto_tsquery('english',?2))"
    );
  }

}
```

and register this class in ‘application.yml’:

```yaml
spring:
  jpa:
    database-platform: <your-package>.CustomPostgreSQLDialect
```

## Creating query

The CriteriaBuilder is used to execute a query to get a result from a full-text search. The AddressRepo class contains a method findAll that uses the TextSearchableSpecification to create a predicate for the query.
```java
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
```

Generated sql query:
```sql
select a1_0.address_id,
       a1_0.building_no,
       a1_0.city,
       a1_0.house_no,
       a1_0.note,
       a1_0.postal_code,
       a1_0.street,
       a1_0.text_searchable
from addresses a1_0
where (
          a1_0.text_searchable @@ plainto_tsquery('english', 'Francisco San Golden ')
          )
offset ? rows fetch first ? rows only
```

## Run project:

1. Change the database connection settings in the `application.yml` file.
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/postgres
    username: postgres
    password: postgres
```
2. Run the project
3. Open the browser and go to [swagger](http://localhost:8080/swagger-ui/index.html)
4. Use the `GET` method to search for addresses by query. (/api/addresses)
---

*Reference:*

[https://www.postgresql.org/docs/current/textsearch-tables.html](https://www.postgresql.org/docs/current/textsearch-tables.html)