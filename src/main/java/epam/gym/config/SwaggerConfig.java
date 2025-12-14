package epam.gym.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .info(new Info()
                        .title("Gym CRM System - RESTful API")
                        .description("""
                                A comprehensive Gym Customer Relationship Management (CRM) system providing RESTful APIs \
                                for managing trainees, trainers, training sessions, and training types.

                                ## Authentication & Authorization

                                * **JWT Authentication**: Login with username/password to receive a Bearer token
                                * **Role-Based Access Control**: Three roles - TRAINEE, TRAINER, ADMIN
                                * **Brute Force Protection**: Account locked for 5 minutes after 3 failed login attempts
                                * **Public Endpoints**: Registration (`/api/v1/trainees`, `/api/v1/trainers`) and login (`/api/auth/login`)
                                * **Authorization Rules**:
                                  - Users can modify their own profiles and change their own passwords
                                  - Admins can activate/deactivate any user account
                                  - Profile ownership is validated using `@PreAuthorize` expressions

                                ## Key Features

                                * **User Management**: Register and manage trainee and trainer profiles with auto-generated credentials
                                * **Profile Operations**: Update user profiles, activate/deactivate accounts, and change passwords
                                * **Training Management**: Create training sessions, view training history with flexible filtering
                                * **Trainer Assignment**: Manage many-to-many relationships between trainees and trainers
                                * **Training Types**: Support for multiple training specializations (Fitness, Yoga, Zumba, etc.)

                                ## Security Features

                                * Stateless JWT token-based authentication
                                * BCrypt password hashing
                                * In-memory brute force protection tracking
                                * Automatic account unlock after 5 minutes
                                * Role-based endpoint protection

                                ## Technical Highlights

                                * RESTful architecture with proper HTTP methods and status codes
                                * Spring Security with JWT tokens
                                * Comprehensive request validation using Jakarta Bean Validation
                                * Global exception handling with meaningful error responses
                                * Transaction-level logging with correlation IDs for request tracing
                                * Swagger/OpenAPI documentation with Bearer token support

                                ## Usage Notes

                                1. Register a new user via `/api/v1/trainees` or `/api/v1/trainers` (no authentication required)
                                2. Login via `/api/auth/login` to receive JWT token
                                3. Use the token in the `Authorization: Bearer <token>` header for all protected endpoints
                                4. Username generation follows pattern: `FirstName.LastName` with incremental suffixes for duplicates
                                5. Passwords are randomly generated 10-character alphanumeric strings (BCrypt hashed)
                                6. Cascade delete: Deleting a trainee removes all associated training sessions
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Abdulbasit Manurov")
                                .url("https://next.telescope.epam.com/telescope/profile?p=%2Fwho%2FAbdulbasit_Manurov%3Ftab%3Dprofile")
                                .email("abdulbasit_manuorv@epam.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")));
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("gym-crm-api")
                .packagesToScan("epam.gym.infrastructure","epam.gym.domain")
                .build();
    }
}