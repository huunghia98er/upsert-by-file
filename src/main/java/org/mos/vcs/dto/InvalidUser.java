package org.mos.vcs.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * @Author: HuuNghia
 * @LastModified: 2024/11/12
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvalidUser {
    private String username;
    private String password;
    private String email;
    private String phone;
    private String address;
    private String role;
    private Instant createdDate;
    private String error;

    public String[] toStringArray() {
        String createdDate = this.createdDate == null ? "" : this.createdDate.toString();
        return new String[] {
            username,
            password,
            email,
            phone,
            address,
            role, createdDate,
            error
        };
    }
}
