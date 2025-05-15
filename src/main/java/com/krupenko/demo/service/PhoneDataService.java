package com.krupenko.demo.service;

import com.krupenko.demo.mapper.PhoneDataMapper;
import com.krupenko.demo.dto.phonedata.PhoneDataCreateEditDto;
import com.krupenko.demo.dto.phonedata.PhoneDataReadDto;
import com.krupenko.demo.repository.PhoneDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PhoneDataService {

    private final PhoneDataRepository phoneDataRepository;
    private final PhoneDataMapper phoneDataMapper;

    public Optional<Long> findPhoneOwnerIdById(Long id) {
        return phoneDataRepository.findById(id)
                .map(phoneData -> phoneData.getUser().getId());
    }

    public Optional<PhoneDataReadDto> findByPhone(String phone) {
        return phoneDataRepository.findByPhone(phone)
                .map(phoneDataMapper::phoneDataToPhoneDataReadDto);
    }

    public int getUserPhonesCount(Long userId) {
        return phoneDataRepository.countByUserId(userId);
    }

    @Transactional
    public PhoneDataReadDto create(PhoneDataCreateEditDto phoneDataDto) {
        return Optional.of(phoneDataDto)
                .map(phoneDataMapper::phoneDataCreateEditDtoToPhoneData)
                .map(phoneDataRepository::save)
                .map(phoneDataMapper::phoneDataToPhoneDataReadDto)
                .orElseThrow();
    }

    @Transactional
    public Optional<PhoneDataReadDto> update(Long id, PhoneDataCreateEditDto phoneDataDto) {
        return phoneDataRepository.findById(id)
                .map(entity -> phoneDataMapper.phoneDataCreateEditDtoToPhoneData(phoneDataDto, entity))
                .map(phoneDataRepository::saveAndFlush)
                .map(phoneDataMapper::phoneDataToPhoneDataReadDto);
    }

    @Transactional
    public boolean delete(Long id) {
        return phoneDataRepository.findById(id)
                .map(entity -> {
                    phoneDataRepository.delete(entity);
                    phoneDataRepository.flush();
                    return true;
                }).orElse(false);
    }


}
