package org.example.employee;

import static org.springframework.security.config.Customizer.withDefaults;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
public class EmployeeConfiguration {

    @Bean
    Map<Integer, Employee> employees() {
        final var employees = new HashMap<Integer, Employee>();
        employees.put(1, Employee.builder()
            .id(1)
            .firstName("John")
            .lastName("Doe")
            .email("jdoe@gmail.com")
            .build());
        employees.put(2, Employee.builder()
            .id(2)
            .firstName("Mary")
            .lastName("Moe")
            .email("mmoe@yahoo.com")
            .build());

        return employees;
    }

    @Bean
    SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {
//        http.anonymous();
        http.cors(withDefaults()).csrf(AbstractHttpConfigurer::disable);
//        http.csrf(Customizer.withDefaults());
//        http.csrf(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    UserDetailsService userDetailsService() {
        return new InMemoryUserDetailsManager();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.applyPermitDefaultValues();
//        configuration.setAllowedOrigins(Arrays.asList("https://example.com"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "DELETE"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /*

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        final var configuration = new CorsConfiguration();
        configuration.applyPermitDefaultValues();
        configuration.setAllowedMethods(List.of("*"));
//        final var allowedOriginPatterns = corsProperties.getAllowedOriginPatterns();

//        if (Objects.nonNull(corsProperties.getAdditionalOriginPatterns()) && !corsProperties.getAdditionalOriginPatterns().isEmpty()) {
//            allowedOriginPatterns.addAll(corsProperties.getAdditionalOriginPatterns());
//        }
//        configuration.setAllowedOriginPatterns(allowedOriginPatterns);
        final var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    FilterRegistrationBean<CorsFilter> corsFilter(final CorsConfigurationSource corsConfigurationSource) {
        return new FilterRegistrationBean<>(new CorsFilter(corsConfigurationSource));
//        final var dateFixFilter = new FilterRegistrationBean<>(new DateFixFilter());
//        dateFixFilter.setUrlPatterns(List.of("/livebetter/challenges/v1/trackChallenges/*", "/livebetter/challenges/v1/trackChallenges"));
//        return null;
    }
     */

    /*
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        final var configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "DELETE", "OPTIONS"));
//        configuration.setAllowedMethods(Arrays.asList("*"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        final var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
     */

    /*
    @Bean
    WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(final CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins("*").allowedMethods("*");
//                registry.addMapping("/**").allowedOrigins("http://members.medibank.com.au:8090").allowedMethods("GET", "POST", "DELETE", "OPTIONS");
            }
        };
    }
     */
}
