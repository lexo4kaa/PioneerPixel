package com.krupenko.demo.mapper;

import com.krupenko.demo.dto.phonedata.PhoneDataCreateEditDto;
import com.krupenko.demo.dto.phonedata.PhoneDataReadDto;
import com.krupenko.demo.entity.PhoneData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface PhoneDataMapper {

    PhoneDataReadDto phoneDataToPhoneDataReadDto(PhoneData phoneData);

    @Mapping(source = "userId", target = "user")
    PhoneData phoneDataCreateEditDtoToPhoneData(PhoneDataCreateEditDto phoneDataCreateEditDto);

    @Mapping(source = "userId", target = "user")
    PhoneData phoneDataCreateEditDtoToPhoneData(PhoneDataCreateEditDto fromObject, @MappingTarget PhoneData toObject);

}
