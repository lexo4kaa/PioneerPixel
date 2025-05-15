package com.krupenko.demo.service;

import com.krupenko.demo.mapper.EmailDataMapper;
import com.krupenko.demo.dto.emaildata.EmailDataCreateEditDto;
import com.krupenko.demo.dto.emaildata.EmailDataReadDto;
import com.krupenko.demo.repository.EmailDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmailDataService {

    private final EmailDataRepository emailDataRepository;
    private final EmailDataMapper emailDataMapper;

    public Optional<Long> findEmailOwnerIdById(Long id) {
        return emailDataRepository.findById(id)
                .map(emailData -> emailData.getUser().getId());
    }

    public Optional<EmailDataReadDto> findByEmail(String email) {
        return emailDataRepository.findByEmail(email)
                .map(emailDataMapper::emailDataToEmailDataReadDto);
    }

    public int getUserEmailsCount(Long userId) {
        return emailDataRepository.countByUserId(userId);
    }

    @Transactional
    public EmailDataReadDto create(EmailDataCreateEditDto emailDataDto) {
        return Optional.of(emailDataDto)
                .map(emailDataMapper::emailDataCreateEditDtoToEmailData)
                .map(emailDataRepository::save)
                .map(emailDataMapper::emailDataToEmailDataReadDto)
                .orElseThrow();
    }

    @Transactional
    public Optional<EmailDataReadDto> update(Long id, EmailDataCreateEditDto emailDataDto) {
        return emailDataRepository.findById(id)
                .map(entity -> emailDataMapper.emailDataCreateEditDtoToEmailData(emailDataDto, entity))
                .map(emailDataRepository::saveAndFlush)
                .map(emailDataMapper::emailDataToEmailDataReadDto);
    }

    @Transactional
    public boolean delete(Long id) {
        return emailDataRepository.findById(id)
                .map(entity -> {
                    emailDataRepository.delete(entity);
                    emailDataRepository.flush();
                    return true;
                }).orElse(false);
    }


}
