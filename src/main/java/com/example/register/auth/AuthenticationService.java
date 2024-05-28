package com.example.register.auth;

//import com.example.register.email.EmailService;
//import com.example.register.email.EmailTemplateName;

import com.example.register.user.Role;
import com.example.register.security.JwtService;
import com.example.register.user.TokenRepository;
import com.example.register.user.UserRepository;
import com.example.register.user.Token;


import com.example.register.user.User;

import com.sun.tools.jconsole.JConsoleContext;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    //private final EmailService emailService;
    private final TokenRepository tokenRepository;

    // @Value("${application.mailing.frontend.activation-url}")
    //private String activationUrl;

    public User getclient (@RequestParam String username )
    {
         return  userRepository.findByUsername(username);
    }

    public RegistrationRequest register(User request) {

        // check if user already exist. if exist than authenticate the user
      /*  if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return new RegistrationRequest("User already exist");
        }*/

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
       //  user.setPassword(request.getPassword());



        user.setRole(request.getRole());

        user = userRepository.save(user);

       // String accessToken = jwtService.generateAccessToken(user);
       // String refreshToken = jwtService.generateRefreshToken(user);

        //saveUserToken(accessToken, refreshToken, user);

        //return new AuthenticationResponse(accessToken, refreshToken, "User registration was successful");
        return new RegistrationRequest( "User registration was successful");


    }

   /* public AuthenticationResponse authenticate(User request) {
       authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        User user= this.getclient(request.getUsername());
       // User user = userRepository.findByUsername(request.getUsername());
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        //revokeAllTokenByUser(user);
        saveUserToken(accessToken, refreshToken, user);

        return new AuthenticationResponse(accessToken, refreshToken, "User login was successful");

    }*/
   public String authenticate(User request) {


       User user= this.getclient(request.getUsername());
       if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
           throw new IllegalArgumentException("Mot de passe incorrect");
       }
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

       //revokeAllTokenByUser(user);
        saveUserToken(accessToken, refreshToken, user);

         return accessToken;
   }


    private void revokeAllTokenByUser(User user) {
        List<Token> validTokens = tokenRepository.findAllAccessTokensByUser(user.getId());
        if (validTokens.isEmpty()) {
            return;
        }

        validTokens.forEach(t -> {
            t.setLoggedOut(true);
        });

        tokenRepository.saveAll(validTokens);
    }

    private void saveUserToken(String accessToken, String refreshToken, User user) {
        Token token = new Token();
        token.setAccessToken(accessToken);
        token.setRefreshToken(refreshToken);
        token.setLoggedOut(false);
        token.setUser(user);
        tokenRepository.save(token);
    }

    public ResponseEntity refreshToken(
            HttpServletRequest request,
            HttpServletResponse response) {
        // extract the token from authorization header
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }

        String token = authHeader.substring(7);

        // extract username from token
        String username = jwtService.extractUsername(token);

        // check if the user exist in database
        User user = userRepository.findByUsername(username);

        // check if the token is valid
        if (jwtService.isValidRefreshToken(token, user)) {
            // generate access token
            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            revokeAllTokenByUser(user);
            saveUserToken(accessToken, refreshToken, user);

            return new ResponseEntity(new AuthenticationResponse(accessToken, refreshToken, "New token generated"), HttpStatus.OK);
        }

        return new ResponseEntity(HttpStatus.UNAUTHORIZED);

    }
}