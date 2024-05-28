package com.example.register.auth;

import com.example.register.security.JwtService;
import com.example.register.user.User;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthenticationController {

    private final AuthenticationService service;

    @PostMapping("/register")
    public ResponseEntity<RegistrationRequest> register(
            @RequestBody User request
    ) {
        return ResponseEntity.ok(service.register(request));
    }

    /*@PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(
            @RequestBody User request
    ) {
        return ResponseEntity.ok(service.authenticate(request));
    }*/
        @GetMapping("/getuser")
        public User getclient(@RequestParam String username)
        {
           return service.getclient(username);
        }
    @PostMapping("/login")
    public String login(
            @RequestBody User request
    ) {
        return (service.authenticate(request));
    }
    @PostMapping("/refresh_token")
    public ResponseEntity refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        return service.refreshToken(request, response);
    }

   // @PreAuthorize("hasAuthority('Admin')")
    @GetMapping("/ping")
    public String test() {
        try {
            return "Welcome";
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }


}