package aicoach.model;
/**
 * @file user.java
 * @brief model de date imutabil pentru un utilizator.
 *
 * contine identificatorul emailul hash-ul parolei si data crearii contului.
 */

public record User(
        long id,
        String email,
        String passwordHash,
        String createdAt
) {}
