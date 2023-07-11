package com.example.it32007telegram.services;

import com.example.it32007telegram.daos.repositories.UserRepository;
import com.example.it32007telegram.daos.repositories.UserStatusRepository;
import com.example.it32007telegram.models.entities.users.User;
import com.example.it32007telegram.models.entities.users.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    @Override
    public User createUser(Message message) {
        org.telegram.telegrambots.meta.api.objects.User userTg = message.getFrom();
        User user = User.builder()
                .firstName(userTg.getFirstName())
                .lastName(userTg.getLastName())
                .userStatus(userStatusRepository.findByCode(UserStatus.Code.ACTIVE.name()))
                .dateCreated(LocalDate.now())
                .username(userTg.getUserName())
                .state("created")
                .build();
        return userRepository.save(user);
    }
}
