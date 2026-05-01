package com.shopflow.shopflow.repository;
import com.shopflow.shopflow.entity.User;
import com.shopflow.shopflow.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findByRefreshToken(String refreshToken);

    Optional<User> findByResetToken(String resetToken);

    List<User> findByRole(Role role);

    List<User> findByActif(boolean actif);
}
