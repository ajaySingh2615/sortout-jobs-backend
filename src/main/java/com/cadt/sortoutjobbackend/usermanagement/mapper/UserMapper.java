package com.cadt.sortoutjobbackend.usermanagement.mapper;

import com.cadt.sortoutjobbackend.usermanagement.dto.UserDTO;
import com.cadt.sortoutjobbackend.usermanagement.dto.UserRegistrationRequest;
import com.cadt.sortoutjobbackend.usermanagement.entity.User;
import org.mapstruct.Mapper;

// componentModel = "spring" allows us to Inject this mapper using @Autowired
@Mapper(componentModel = "spring")
public interface UserMapper {

    // Request -> Entity
    // MapStruct automatically maps fields with the same name!
    User toEntity(UserRegistrationRequest request);

    // Entity -> Response DTO
    UserDTO toDTO(User user);
}
