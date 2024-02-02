package org.example.employee;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import org.springframework.scheduling.annotation.EnableScheduling;
import software.amazon.awssdk.services.ecs.EcsClient;

@Configuration(proxyBeanMethods = false)
//@EnableWebSecurity
@EnableScheduling
@EnableCaching
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
    EcsClient getEcsClient() {
        return EcsClient.builder().build();
    }

    @Bean
    public RedisCacheManagerBuilderCustomizer myRedisCacheManagerBuilderCustomizer() {
        return (builder) -> builder
            .withCacheConfiguration("employee-directory-employees", RedisCacheConfiguration
                .defaultCacheConfig().entryTtl(Duration.ofMinutes(10))
                .serializeValuesWith(SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer())));
    }

    /*

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
     */

    /*

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
     */

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
