package aicoach.model;
/**
 * @file UserProfile.java
 * @brief Model de date imutabil pentru profilul unui utilizator.
 *
 * Contine date biometrice si preferinte (scop, nivel de activitate etc.), plus momentul ultimei actualizari.
 */

public record UserProfile(
        long userId,
        Integer age,
        Integer heightCm,
        Double weightKg,
        String goal,
        String activityLevel,
        String gender,
        String updatedAt
) {}
