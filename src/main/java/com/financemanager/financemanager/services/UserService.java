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

@Service
public class UserService implements UserDetailsService {

    private final Map<String, User> store = new ConcurrentHashMap<>();
    private final PasswordEncoder encoder;

    public UserService(PasswordEncoder encoder) {
        this.encoder = encoder;
    }

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

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails u = store.get(username);
        if (u == null) throw new UsernameNotFoundException(username);
        return u;
    }
}