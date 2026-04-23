package pe.edu.upeu.msauth.service;

import pe.edu.upeu.msauth.dto.AuthUserDto;
import pe.edu.upeu.msauth.dto.TokenDto;
import pe.edu.upeu.msauth.entity.AuthUser;

public interface AuthUserService {
     AuthUser save(AuthUserDto authUserDto);


     TokenDto login(AuthUserDto authUserDto);

     AuthUser getById(Integer id);
     TokenDto validate(String token);
}