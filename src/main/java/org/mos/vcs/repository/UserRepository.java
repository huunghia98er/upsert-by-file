package org.mos.vcs.repository;

import org.mos.vcs.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @Author: HuuNghia
 * @LastModified: 2024/11/12
 */

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsernameAndEmailAndPhone(@Param("username") String username, @Param("email") String email, @Param("phone") String phone);
}
