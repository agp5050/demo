package com.agp.demo.mapstruct;

import com.agp.demo.Person;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PersonVOConverter  {
    PersonVOConverter INSTANCE= Mappers.getMapper(PersonVOConverter.class);
    @Mappings({@Mapping(source = "name",target = "userName"),@Mapping(source = "age",target = "userAge")})
    PersonVO getPersonVO(Person person);
}
