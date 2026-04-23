package pe.edu.upeu.msauth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.msauth.dto.AuthUserDto;
import pe.edu.upeu.msauth.dto.TokenDto;
import pe.edu.upeu.msauth.entity.AuthUser;
import pe.edu.upeu.msauth.service.AuthUserService;

@RestController
@RequestMapping("/auth")
public class AuthUserController {
    @Autowired
    AuthUserService authUserService;

    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@RequestBody AuthUserDto authUserDto) {
        TokenDto tokenDto = authUserService.login(authUserDto);
        if (tokenDto == null)
            return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(tokenDto);
    }

    @PostMapping("/validate")
    public ResponseEntity<TokenDto> validate(@RequestParam String token) {
        TokenDto tokenDto = authUserService.validate(token);
        if (tokenDto == null)
            return ResponseEntity
                    .badRequest()
                    .body(new TokenDto("Token inválido o expirado"));
        return ResponseEntity.ok(tokenDto);
    }

    @PostMapping("/create")
    public ResponseEntity<AuthUser> create(@RequestBody AuthUserDto authUserDto) {
        AuthUser authUser = authUserService.save(authUserDto);
        if (authUser == null)
            return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(authUser);
    }
    @GetMapping("/{id}")
    public ResponseEntity<AuthUserDto> getById(@PathVariable Integer id) {
        AuthUser authUser = authUserService.getById(id);
        if (authUser == null)
            return ResponseEntity.notFound().build();

        AuthUserDto response = AuthUserDto.builder()
                .userName(authUser.getUserName())
                .build();

        return ResponseEntity.ok(response);
    }


}
