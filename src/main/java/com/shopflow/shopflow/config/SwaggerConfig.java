package com.shopflow.shopflow.config;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                // Informations générales de l'API
                .info(new Info()
                        .title("ShopFlow API")
                        .description("API REST pour la plateforme e-commerce ShopFlow. "
                                + "Marketplace B2C avec gestion de catalogue, panier, "
                                + "commandes, paiement simulé et avis clients.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("ShopFlow Team")
                                .email("contact@shopflow.com"))
                )

                // Ajouter le bouton "Authorize" dans Swagger UI
                .addSecurityItem(new SecurityRequirement()
                        .addList(securitySchemeName))

                // Définir le schéma de sécurité JWT
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Entrez votre JWT token. "
                                                + "Exemple : eyJhbGciOiJIUzI1NiJ9...")
                        )
                );
    }
}
