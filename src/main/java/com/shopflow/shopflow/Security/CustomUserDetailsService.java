package com.shopflow.shopflow.Security;
import com.shopflow. shopflow.entity.User;
import com.shopflow.shopflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Charger un utilisateur par son email (utilisé par Spring Security)
     * Le "username" dans notre cas est l'email
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Utilisateur non trouvé avec l'email : " + email));

        // Vérifier si le compte est actif
        if (!user.isActif()) {
            throw new UsernameNotFoundException("Ce compte est désactivé");
        }

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getMotDePasse(),
                user.isActif(),       // enabled
                true,                  // accountNonExpired
                true,                  // credentialsNonExpired
                true,                  // accountNonLocked
                Collections.singletonList(
                        new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
                )
        );
    }
}
