package aicoach.model;
/**
 * @file chatmessage.java
 * @brief model de date imutabil pentru un mesaj din istoricul conversatiei.
 *
 * contine rolul(generat de ai sau user) continutul mesajului si momentul trimiterii.
 */


public record ChatMessage(
        long id,
        long userId,
        String role,
        String message,
        String timestamp
) {}
