package com.example.it32007telegram.daos.repositories;

import com.example.it32007telegram.daos.repositories.base.BaseEntityRepository;
import com.example.it32007telegram.models.entities.base.City;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CityRepository extends BaseEntityRepository<City> {

    @Query(value = "SELECT city FROM City city WHERE city.country.id =:countryId and lower(city.name) like concat('%', lower(CAST(:name as text)), '%' ) ")
    Optional<City> findByCountryIdAndName(Long countryId, String name);
}
