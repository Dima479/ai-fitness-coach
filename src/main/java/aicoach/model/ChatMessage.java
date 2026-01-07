package aicoach.model;
/**
 * @file ChatMessage.java
 * @brief Model de date imutabil pentru un mesaj din istoricul conversatiei.
 *
 * Contine rolul(generat de AI sau user), continutul mesajului si momentul trimiterii.
 */


public record ChatMessage(
        long id,
        long userId,
        String role,
        String message,
        String timestamp
) {}
