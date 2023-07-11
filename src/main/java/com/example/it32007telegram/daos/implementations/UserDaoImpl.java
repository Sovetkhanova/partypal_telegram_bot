package com.example.it32007telegram.daos.implementations;

import com.example.it32007telegram.daos.CriteriaDao;
import com.example.it32007telegram.daos.UserDao;
import com.example.it32007telegram.daos.repositories.UserRepository;
import com.example.it32007telegram.daos.repositories.UserStatusRepository;
import com.example.it32007telegram.exceptions.NotFoundException;
import com.example.it32007telegram.models.entities.users.User;
import com.example.it32007telegram.models.entities.users.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserDaoImpl implements UserDao {
    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;
    @PersistenceContext
    private EntityManager entityManager;


    public static final String USER_WAS_NOT_FOUND = "User with %s = %s was not found";


    @Override
    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format(USER_WAS_NOT_FOUND, "id", id)));
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User createUser(org.telegram.telegrambots.meta.api.objects.User userTg) {
        User user = User.builder()
                .firstName(userTg.getFirstName())
                .lastName(userTg.getLastName())
                .userStatus(userStatusRepository.findByCode(UserStatus.Code.ACTIVE.name()))
                .dateCreated(LocalDate.now())
                .username(userTg.getUserName())
                .lastLoginDateTime(LocalDateTime.now())
                .state("userCreated")
                .build();
        return userRepository.save(user);
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public Class<User> getType() {
        return User.class;
    }

    @Override
    public CriteriaDao<User> getImplementation() {
        return this;
    }
}
