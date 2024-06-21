package com.springboot.blog.security;

import com.springboot.blog.entity.User;
import com.springboot.blog.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(()-> new UsernameNotFoundException("User not found with username/email: "+ usernameOrEmail));

        // converting set of roles into set of GrantedAuthority
        Set<GrantedAuthority> authorities = user
                .getRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toSet());

        // here we are returning instance of User class that is given by "Spring Security"
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),   // (Or,) user.getUsername(), but can't use both together.... **
                user.getPassword(),
                authorities // same as getRoles(), but we are passing GrantedAuthority
        );
    }
}

// **
// In your User class, you have both username and email fields, but they are marked as unique.
// This implies that either the username or the email can be used to uniquely identify a user in your system.
// In the loadUserByUsername method, you are searching for a user by their username or email.
// Whichever value is provided as usernameOrEmail will be used to query the database,
// and if a user is found, you are using their email (which serves as the username in this case) to construct the org.springframework.security.core.userdetails.User instance.
// If you intended to use the username instead of the email as the username in the UserDetails instance,
// you would replace user.getEmail() with user.getUsername().
// However, it seems like your application's design has chosen to use the email as the username for authentication purposes