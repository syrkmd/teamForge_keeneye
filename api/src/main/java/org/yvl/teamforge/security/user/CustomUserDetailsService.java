package org.yvl.teamforge.security.user;


import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.yvl.teamforge.entity.User;
import org.yvl.teamforge.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = repository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User with email: " + email + " not found"));

        return new UserPrincipal(user);
    }
}
