package com.krupenko.demo.mapper;

import com.krupenko.demo.dto.emaildata.EmailDataCreateEditDto;
import com.krupenko.demo.dto.emaildata.EmailDataReadDto;
import com.krupenko.demo.entity.EmailData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface EmailDataMapper {

    EmailDataReadDto emailDataToEmailDataReadDto(EmailData emailData);

    @Mapping(source = "userId", target = "user")
    EmailData emailDataCreateEditDtoToEmailData(EmailDataCreateEditDto emailDataCreateEditDto);

    @Mapping(source = "userId", target = "user")
    EmailData emailDataCreateEditDtoToEmailData(EmailDataCreateEditDto fromObject, @MappingTarget EmailData toObject);

}
