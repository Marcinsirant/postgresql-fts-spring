create table addresses
(
    address_id  uuid not null
        constraint pk_addresses
            primary key,
    street      varchar,
    city        varchar,
    postal_code varchar,
    house_no    varchar,
    building_no varchar,
    note        varchar
);

ALTER TABLE addresses
    ADD COLUMN text_searchable tsvector
        GENERATED ALWAYS AS (to_tsvector('english', coalesce(street, '') || ' ' ||
                                                   coalesce(city, '') || ' ' ||
                                                   postal_code || ' ' ||
                                                   coalesce(house_no, '') || ' ' ||
                                                   coalesce(note, '') || ' ' ||
                                                   coalesce(building_no, '')
                             )) STORED;

CREATE INDEX text_searchable_idx ON addresses USING GIN (text_searchable);