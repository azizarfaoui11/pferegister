package com.example.register.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RegistrationRequest {


    @JsonProperty("message")
    private String message;

    public RegistrationRequest(String message) {
        this.message = message;
    }
    public String getMessage() {
        return message;
    }
}
