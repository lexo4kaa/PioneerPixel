package com.krupenko.demo.controller;

import com.krupenko.demo.dto.emaildata.EmailDataCreateEditDto;
import com.krupenko.demo.dto.emaildata.EmailDataReadDto;
import com.krupenko.demo.dto.emaildata.EmailDataFormDto;
import com.krupenko.demo.service.EmailDataService;
import com.krupenko.demo.service.TokenService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

import static com.krupenko.demo.constansts.ExceptionMessages.EMAIL_ADDRESS_IS_ALREADY_IN_USE;
import static com.krupenko.demo.constansts.ExceptionMessages.TRY_TO_DELETE_LAST_EMAIL_ADDRESS;
import static com.krupenko.demo.constansts.ExceptionMessages.TRY_TO_INFLUENCE_OTHER_EMAIL_ADDRESS;

@RestController
@RequestMapping("/api/v1/emails")
@Tag(name = "Email data", description = "API for operations with email data")
public class EmailDataController extends BaseController {

    private final EmailDataService emailDataService;

    public EmailDataController(TokenService tokenService, EmailDataService emailDataService) {
        super(tokenService);
        this.emailDataService = emailDataService;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid EmailDataFormDto emailFormData, HttpServletRequest request) {
        String email = emailFormData.getEmail();
        checkEmailExistence(email);

        Long currentUserId = getUserIdFromRequest(request);
        EmailDataCreateEditDto emailDataDto = new EmailDataCreateEditDto(email, currentUserId);
        EmailDataReadDto emailDataReadDto = emailDataService.create(emailDataDto);
        return new ResponseEntity<>(emailDataReadDto, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") Long id, @RequestBody @Valid EmailDataFormDto emailFormData,
                                    HttpServletRequest request) {
        Long emailOwnerId = emailDataService.findEmailOwnerIdById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Long currentUserId = getUserIdFromRequest(request);
        String email = emailFormData.getEmail();
        checkEmailOwnership(emailOwnerId, currentUserId);
        checkEmailExistence(email);

        EmailDataCreateEditDto emailDataDto = new EmailDataCreateEditDto(email, currentUserId);
        return emailDataService.update(id, emailDataDto)
                .map(updatedSensor -> new ResponseEntity<>(updatedSensor, HttpStatus.OK))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id, HttpServletRequest request) {
        Long emailOwnerId = emailDataService.findEmailOwnerIdById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Long currentUserId = getUserIdFromRequest(request);
        checkEmailOwnership(emailOwnerId, currentUserId);

        if (emailDataService.getUserEmailsCount(currentUserId) == 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, TRY_TO_DELETE_LAST_EMAIL_ADDRESS);
        }

        boolean success = emailDataService.delete(id);
        return new ResponseEntity<>(success ? HttpStatus.NO_CONTENT : HttpStatus.NOT_FOUND);
    }

    private void checkEmailExistence(String email) {
        if (emailDataService.findByEmail(email).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, EMAIL_ADDRESS_IS_ALREADY_IN_USE);
        }
    }

    private void checkEmailOwnership(Long emailOwnerId, Long userId) {
        if (!Objects.equals(emailOwnerId, userId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, TRY_TO_INFLUENCE_OTHER_EMAIL_ADDRESS);
        }
    }

}
