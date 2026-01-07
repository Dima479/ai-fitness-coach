package aicoach.model;
/**
 * @file User.java
 * @brief Model de date imutabil pentru un utilizator.
 *
 * Contine identificatorul, emailul, hash-ul parolei si data crearii contului.
 */

public record User(
        long id,
        String email,
        String passwordHash,
        String createdAt
) {}
