package com.login.authentication.controllers;


import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.login.authentication.payload.request.SignupRequest;
import com.login.authentication.security.jwt.JwtUtils;
import com.login.authentication.security.services.UserDetalhe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.login.authentication.models.EnumTipoUsuario;
import com.login.authentication.models.TipoUsuario;
import com.login.authentication.models.User;
import com.login.authentication.payload.request.LoginRequest;
import com.login.authentication.payload.response.UserInfoResponse;
import com.login.authentication.payload.response.MessageResponse;
import com.login.authentication.repository.TipoUsuarioRepository;
import com.login.authentication.repository.UserRepository;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
  @Autowired
  AuthenticationManager authenticationManager;

  @Autowired
  UserRepository userRepository;

  @Autowired
  TipoUsuarioRepository tipoUsuarioRepository;

  @Autowired
  PasswordEncoder encoder;

  @Autowired
  JwtUtils jwtUtils;

  @PostMapping("/signup")
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
    if (userRepository.existsByUsername(signUpRequest.getUsername())) {
      return ResponseEntity.badRequest().body(new MessageResponse("Error: Username ja esta em uso!"));
    }

    if (userRepository.existsByEmail(signUpRequest.getEmail())) {
      return ResponseEntity.badRequest().body(new MessageResponse("Error: Email ja esta em uso!"));
    }

    User user = new User(signUpRequest.getUsername(),
            signUpRequest.getEmail(),
            encoder.encode(signUpRequest.getPassword()));

    Set<String> strTiposUsuario = signUpRequest.getTipoUsuario();
    Set<TipoUsuario> tiposUsuario = new HashSet<>();

    if (strTiposUsuario == null) {
      TipoUsuario tipoUsuario = tipoUsuarioRepository.findByName(EnumTipoUsuario.USER_PADRAO)
              .orElseThrow(() -> new RuntimeException("Error: Tipo do usuário não encontrado."));
      tiposUsuario.add(tipoUsuario);
    } else {
      strTiposUsuario.forEach(tipoUsuario -> {
        switch (tipoUsuario) {
          case "admin":
            TipoUsuario tipoUsuarioAdmin = tipoUsuarioRepository.findByName(EnumTipoUsuario.USER_ADMIN)
                    .orElseThrow(() -> new RuntimeException("Error: Tipo do usuário não encontrado."));
            tiposUsuario.add(tipoUsuarioAdmin);

            break;
          case "mod":
            TipoUsuario tipoUsuarioMod = tipoUsuarioRepository.findByName(EnumTipoUsuario.USER_MODERADOR)
                    .orElseThrow(() -> new RuntimeException("Error: Tipo do usuário não encontrado."));
            tiposUsuario.add(tipoUsuarioMod);

            break;
          default:
            TipoUsuario tipoUsuarioPadr = tipoUsuarioRepository.findByName(EnumTipoUsuario.USER_PADRAO)
                    .orElseThrow(() -> new RuntimeException("Error: Tipo do usuário não encontrado."));
            tiposUsuario.add(tipoUsuarioPadr);
        }
      });
    }

    user.setTiposUsuario(tiposUsuario);
    userRepository.save(user);

    return ResponseEntity.ok(new MessageResponse("Usuário registrado com sucesso!"));
  }

  @PostMapping("/signin")
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

    Authentication authentication = authenticationManager
        .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);

    UserDetalhe userDetails = (UserDetalhe) authentication.getPrincipal();

    ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

    List<String> tiposUsuario = userDetails.getAuthorities().stream()
        .map(item -> item.getAuthority())
        .collect(Collectors.toList());

    return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
        .body(new UserInfoResponse(userDetails.getId(),
                                   userDetails.getUsername(),
                                   userDetails.getEmail(),
                                   tiposUsuario));
  }
  @PostMapping("/signout")
  public ResponseEntity<?> logoutUser() {
    ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
    return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
        .body(new MessageResponse("Você foi desconectado!"));
  }
}
