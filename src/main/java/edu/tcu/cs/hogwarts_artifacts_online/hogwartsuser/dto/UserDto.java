package edu.tcu.cs.hogwarts_artifacts_online.hogwartsuser.dto;

import jakarta.validation.constraints.NotEmpty;

public record UserDto(Integer id,
                      @NotEmpty(message = "username is required.")
                      String username,

                      boolean enabled,

                      @NotEmpty(message = "roles are required.")
                      String roles) {
}
