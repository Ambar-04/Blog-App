package com.springboot.blog.config;

import com.springboot.blog.repository.PostRepository;
import com.springboot.blog.security.JwtAuthenticationEntryPoint;
import com.springboot.blog.security.JwtAuthenticationFilter;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@SecurityScheme(
        name = "Bearer Authentication",
        type = SecuritySchemeType.HTTP, //This indicates security scheme relies on HTTP mechanisms for securing the API
        bearerFormat = "JWT", //specific to bearer token authentication. It indicates the format of the bearer token.
        scheme = "bearer" //It specifies the name of the HTTP authorization scheme. This is the value that clients will include in the Authorization header of their HTTP requests
)
public class SecurityConfig {

    private UserDetailsService userDetailsService; //instead of using CustomUserDetailsService(in security package)
                                                   //we are directly using the interface UserDetailsService
    private JwtAuthenticationEntryPoint authenticationEntryPoint;
    private JwtAuthenticationFilter authenticationFilter;

    public SecurityConfig(UserDetailsService userDetailsService,
                          JwtAuthenticationEntryPoint authenticationEntryPoint,
                          JwtAuthenticationFilter authenticationFilter){
        this.userDetailsService = userDetailsService;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.authenticationFilter = authenticationFilter;
    }


    @Bean
    public static PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    // BCryptPasswordEncoder class implements PasswordEncoder interface which is having encode() abstract method

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // configuration
        http.csrf((csrf) -> csrf.disable())
                .authorizeHttpRequests((authorize) ->
                        //authorize.anyRequest().authenticated()) //Authenticate any requests
                        authorize.requestMatchers(HttpMethod.GET,"api/**").permitAll() //Don't authenticate GET requests
                                .requestMatchers("/api/auth/**").permitAll() //All users have permission to access this api
                                .requestMatchers("/swagger-ui/**").permitAll()
                                .requestMatchers("/v3/api-docs/**").permitAll()
                                .anyRequest().authenticated() //Apart from GET, authenticate all other request
                )
                .exceptionHandling(exception -> exception.authenticationEntryPoint(authenticationEntryPoint))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build(); // returns instance of DefaultSecurityFilterChain that implements SecurityFilterChain interface.
    }



//    Securing REST API's with In-memory Authentication
//    @Bean
//    public UserDetailsService userDetailsService(){
//        UserDetails ambar = User.builder()
//                .username("ambar")
//                .password(passwordEncoder().encode("abcd"))
//                .roles("USER").build();
//
//        UserDetails admin = User.builder()
//                .username("admin")
//                .password(passwordEncoder().encode("admin"))
//                .roles("ADMIN").build();
//
//        return new InMemoryUserDetailsManager(ambar, admin);
//    }
}


//    Method Call:
//        passwordEncoder().encode("password")
//        passwordEncoder() is a method that returns a PasswordEncoder instance(for the @Bean).
//        In the case of passwordEncoder(), this method creates a PasswordEncoder bean.
//        .encode("password") calls the encode method on that instance.

//    Why Not Direct:
//        passwordEncoder.encode("password") assumes passwordEncoder is an instance,
//        but it’s a method, so you must call the method to get the instance first.