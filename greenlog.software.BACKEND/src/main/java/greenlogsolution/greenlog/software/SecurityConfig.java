package greenlogsolution.greenlog.software;

import org.springframework.beans.factory.annotation.Autowired; // <-- IMPORT NECESSÁRIO
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import repositories.UsuarioRepository;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(withDefaults())
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authorize -> authorize
                .anyRequest().authenticated()
            )
            .httpBasic(withDefaults())
            
            .exceptionHandling(customizer -> 
                customizer.authenticationEntryPoint(customAuthenticationEntryPoint));
            
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(UsuarioRepository usuarioRepository) {
        return username -> usuarioRepository.findByLogin(username)
            .map(usuario -> org.springframework.security.core.userdetails.User.builder()
                .username(usuario.getLogin())
                .password(usuario.getSenha())
                .roles("USER")
                .build())
            .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));
    }
}