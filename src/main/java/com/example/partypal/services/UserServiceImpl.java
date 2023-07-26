package com.example.partypal.services;

import com.example.partypal.daos.repositories.UserRepository;
import com.example.partypal.daos.repositories.UserStatusRepository;
import com.example.partypal.models.entities.users.User;
import com.example.partypal.models.entities.users.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    @Override
    public User getOrCreateUser(org.telegram.telegrambots.meta.api.objects.User userTg) {
        return findUserByTelegramId(userTg.getId()).orElseGet(() -> createUser(userTg));
    }

    @Cacheable("user")
    public Optional<User> findUserByTelegramId(Long tgId){
        return userRepository.findByTelegramId(tgId);
    }

    @Override
    @Transactional
    public User createUser(org.telegram.telegrambots.meta.api.objects.User userTg) {
        String language = userTg.getLanguageCode();
        if(language == null){
            language = "ru";
        }
        if(language.equals("kz")){
            language = "kk";
        }
        User user = User.builder()
                .firstName(userTg.getFirstName())
                .lastName(userTg.getLastName())
                .userStatus(userStatusRepository.findByCode(UserStatus.Code.ACTIVE.name()))
                .dateCreated(LocalDate.now())
                .telegramId(userTg.getId())
                .telegramUsername(userTg.getUserName())
                .lang(language)
                .lastLoginDateTime(LocalDateTime.now())
                .build();
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void saveUser(User user) {
        userRepository.save(user);
    }
}
