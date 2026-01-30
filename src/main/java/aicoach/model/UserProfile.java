package aicoach.model;
/**
 * @file userprofile.java
 * @brief model de date imutabil pentru profilul unui utilizator.
 *
 * contine date biometrice si preferinte (scop nivel de activitate etc.) plus momentul ultimei actualizari.
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
