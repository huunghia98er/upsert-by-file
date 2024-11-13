package org.mos.vcs.repository;

import org.mos.vcs.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @Author: HuuNghia
 * @LastModified: 2024/11/12
 */

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
