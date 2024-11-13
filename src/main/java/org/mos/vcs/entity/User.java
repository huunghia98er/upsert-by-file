package org.mos.vcs.entity;

import io.hypersistence.utils.hibernate.id.BatchSequence;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * @Author: HuuNghia
 * @LastModified: 2024/11/12
 */

@Getter
@Setter
@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @BatchSequence(
            name = "post_sequence",
            fetchSize = 5
    )
    private Long id;
    private String username;
    private String password;
    private String email;
    private String phone;
    private String address;
    private String role;
    private Instant createdDate;
}
