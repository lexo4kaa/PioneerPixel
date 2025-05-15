package com.krupenko.demo.controller;

import com.krupenko.demo.dto.phonedata.PhoneDataCreateEditDto;
import com.krupenko.demo.dto.phonedata.PhoneDataReadDto;
import com.krupenko.demo.dto.phonedata.PhoneDataFormDto;
import com.krupenko.demo.service.PhoneDataService;
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

import static com.krupenko.demo.constansts.ExceptionMessages.PHONE_NUMBER_IS_ALREADY_IN_USE;
import static com.krupenko.demo.constansts.ExceptionMessages.TRY_TO_DELETE_LAST_PHONE_NUMBER;
import static com.krupenko.demo.constansts.ExceptionMessages.TRY_TO_INFLUENCE_OTHER_PHONE_NUMBER;

@RestController
@RequestMapping("/api/v1/phones")
@Tag(name = "Phone data", description = "API for operations with phone data")
public class PhoneDataController extends BaseController {

    private final PhoneDataService phoneDataService;

    public PhoneDataController(TokenService tokenService, PhoneDataService phoneDataService) {
        super(tokenService);
        this.phoneDataService = phoneDataService;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid PhoneDataFormDto phoneFormData, HttpServletRequest request) {
        String phone = phoneFormData.getPhone();
        checkPhoneExistence(phone);

        Long currentUserId = getUserIdFromRequest(request);
        PhoneDataCreateEditDto phoneDataDto = new PhoneDataCreateEditDto(phone, currentUserId);
        PhoneDataReadDto phoneDataReadDto = phoneDataService.create(phoneDataDto);
        return new ResponseEntity<>(phoneDataReadDto, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") Long id, @RequestBody @Valid PhoneDataFormDto phoneFormData,
                                    HttpServletRequest request) {
        Long phoneOwnerId = phoneDataService.findPhoneOwnerIdById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Long currentUserId = getUserIdFromRequest(request);
        String phone = phoneFormData.getPhone();
        checkPhoneOwnership(phoneOwnerId, currentUserId);
        checkPhoneExistence(phone);

        PhoneDataCreateEditDto phoneDataDto = new PhoneDataCreateEditDto(phone, currentUserId);
        return phoneDataService.update(id, phoneDataDto)
                .map(updatedSensor -> new ResponseEntity<>(updatedSensor, HttpStatus.OK))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id, HttpServletRequest request) {
        Long phoneOwnerId = phoneDataService.findPhoneOwnerIdById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Long currentUserId = getUserIdFromRequest(request);
        checkPhoneOwnership(phoneOwnerId, currentUserId);

        if (phoneDataService.getUserPhonesCount(currentUserId) == 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, TRY_TO_DELETE_LAST_PHONE_NUMBER);
        }

        boolean success = phoneDataService.delete(id);
        return new ResponseEntity<>(success ? HttpStatus.NO_CONTENT : HttpStatus.NOT_FOUND);
    }

    private void checkPhoneExistence(String phone) {
        if (phoneDataService.findByPhone(phone).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, PHONE_NUMBER_IS_ALREADY_IN_USE);
        }
    }

    private void checkPhoneOwnership(Long phoneOwnerId, Long currentUserId) {
        if (!Objects.equals(phoneOwnerId, currentUserId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, TRY_TO_INFLUENCE_OTHER_PHONE_NUMBER);
        }
    }

}
