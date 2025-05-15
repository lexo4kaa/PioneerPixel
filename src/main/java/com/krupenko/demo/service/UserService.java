package com.krupenko.demo.service;

import com.krupenko.demo.dto.user.UserAuthDto;
import com.krupenko.demo.dto.user.UserFilter;
import com.krupenko.demo.dto.user.UserReadDto;
import com.krupenko.demo.entity.User;
import com.krupenko.demo.mapper.UserMapper;
import com.krupenko.demo.repository.UserRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.krupenko.demo.constansts.RedisConstants.USERS_BY_FILTER_CACHE_NAME;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Cacheable(value = USERS_BY_FILTER_CACHE_NAME, key = "#filter")
    public List<UserReadDto> findAll(UserFilter filter) {
        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize());
        Specification<User> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (filter.getDateOfBirth() != null) {
                predicates.add(criteriaBuilder.greaterThan(root.get("dateOfBirth"), filter.getDateOfBirth()));
            }
            if (filter.getPhone() != null) {
                predicates.add(criteriaBuilder.equal(root.get("phones").get("phone"), filter.getPhone()));
            }
            if (filter.getName() != null) {
                predicates.add(criteriaBuilder.like(root.get("name"), filter.getName() + "%"));
            }
            if (filter.getEmail() != null) {
                predicates.add(criteriaBuilder.equal(root.get("emails").get("email"), filter.getEmail()));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        return userRepository.findAll(spec, pageable)
                .stream().map(userMapper::userToUserReadDto)
                .toList();
    }

    public Optional<UserAuthDto> findAuthDataById(Long id) {
        return userRepository.findById(id)
                .stream().map(userMapper::userToUserAuthDto)
                .findFirst();
    }

    public Optional<UserReadDto> findByEmail(String email) {
        return userRepository.findByEmail(email)
                .stream().map(userMapper::userToUserReadDto)
                .findFirst();
    }

    public Optional<UserReadDto> findByPhone(String phone) {
        return userRepository.findByPhone(phone)
                .stream().map(userMapper::userToUserReadDto)
                .findFirst();
    }

}
