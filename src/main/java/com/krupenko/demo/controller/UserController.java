package com.krupenko.demo.controller;

import com.krupenko.demo.dto.user.UserFilter;
import com.krupenko.demo.dto.user.UserReadDto;
import com.krupenko.demo.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "API for operations with users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserReadDto>> findAll(@Valid @ParameterObject UserFilter filter) {
        return ResponseEntity.ok(userService.findAll(filter));
    }

}
