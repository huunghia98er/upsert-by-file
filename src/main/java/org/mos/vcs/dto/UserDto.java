package org.mos.vcs.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.Instant;

/**
 * @Author: HuuNghia
 * @LastModified: 2024/11/12
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    @NotNull
    @NotBlank
    @NotEmpty
    private String username;
    @NonNull
    private String password;
    @Email
    @NonNull
    private String email;
    @NonNull
    private String phone;
    @NonNull
    private String address;
    @NonNull
    private String role;
    @Future
    private Instant createdDate;
}
