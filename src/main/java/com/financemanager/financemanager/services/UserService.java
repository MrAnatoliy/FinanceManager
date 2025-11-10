package com.financemanager.financemanager.services;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.financemanager.financemanager.entities.UserEntity;
import com.financemanager.financemanager.repositories.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;  

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository repo;
    private final PasswordEncoder encoder;

    /* ---------- create (used by /register) ---------- */
    @Transactional
    public void create(String username, String rawPassword, Set<String> roles) {
        if (repo.findByUsername(username).isPresent())
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User exists");

        String rolesCsv = roles == null || roles.isEmpty()
                ? "ROLE_USER"
                : roles.stream()
                       .map(r -> r.startsWith("ROLE_") ? r : "ROLE_" + r)
                       .collect(Collectors.joining(","));

        UserEntity ue = new UserEntity();
        ue.setUsername(username);
        ue.setPassword(encoder.encode(rawPassword));
        ue.setRoles(rolesCsv);
        repo.save(ue);
    }

    /* ---------- Spring-Security lookup ---------- */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity ue = repo.findByUsername(username)
                            .orElseThrow(() -> new UsernameNotFoundException(username));

        List<? extends GrantedAuthority> authorities =
        Arrays.stream(ue.getRoles().split(","))
              .map(SimpleGrantedAuthority::new)
              .toList();

        return new User(ue.getUsername(), ue.getPassword(), authorities);
    }
}