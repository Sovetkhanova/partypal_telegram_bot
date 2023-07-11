package com.example.it32007telegram.daos.repositories;

import com.example.it32007telegram.daos.repositories.base.BaseEntityWithCodeAndLangRepository;
import com.example.it32007telegram.models.entities.users.Role;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends BaseEntityWithCodeAndLangRepository<Role> {

}