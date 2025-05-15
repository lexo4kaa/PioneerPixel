package com.krupenko.demo.mapper;

import com.krupenko.demo.dto.user.UserAuthDto;
import com.krupenko.demo.dto.user.UserReadDto;
import com.krupenko.demo.entity.EmailData;
import com.krupenko.demo.entity.PhoneData;
import com.krupenko.demo.entity.User;
import com.krupenko.demo.repository.UserRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class UserMapper {

    @Autowired
    protected UserRepository userRepository;

    @Mapping(source = "emails", target = "emails")
    @Mapping(source = "phones", target = "phones")
    @Mapping(source = "account.balance", target = "balance")
    public abstract UserReadDto userToUserReadDto(User user);

    public abstract UserAuthDto userToUserAuthDto(User user);

    protected User userIdToUser(Long userId) {
        return Optional.ofNullable(userId)
                .flatMap(userRepository::findById)
                .orElse(null);
    }

    protected Set<String> emailDataToEmails(Set<EmailData> emailData) {
        return emailData.stream()
                .map(EmailData::getEmail)
                .collect(Collectors.toSet());
    }

    protected Set<String> phoneDataToPhones(Set<PhoneData> phoneData) {
        return phoneData.stream()
                .map(PhoneData::getPhone)
                .collect(Collectors.toSet());
    }

}
