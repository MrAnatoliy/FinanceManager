package com.financemanager.financemanager.services;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
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

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    /* ---------- 1. production: use your real repository ----------
    private final UserRepository repo;
    ---------------------------------------------------------------*/
    // 2. demo: simple in-memory store
    private final Map<String, User> store = new ConcurrentHashMap<>();
    private final PasswordEncoder encoder;

    /* ----------- create (used by /register) ----------- */
    public void create(String username, String rawPassword, Set<String> roles) {
        if (store.containsKey(username))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User exists");

        Set<GrantedAuthority> authorities = roles == null || roles.isEmpty()
                ? Set.of(new SimpleGrantedAuthority("ROLE_USER"))
                : roles.stream().map(r -> r.startsWith("ROLE_") ? r : "ROLE_" + r)
                                 .map(SimpleGrantedAuthority::new)
                                 .collect(Collectors.toSet());

        User user = new User(username, encoder.encode(rawPassword), authorities);
        store.put(username, user);
    }

    /* ----------- loadUserByUsername (Spring Security) ----------- */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails u = store.get(username);
        if (u == null) throw new UsernameNotFoundException(username);
        return u;
    }
}