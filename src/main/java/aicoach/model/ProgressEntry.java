package aicoach.model;
/**
 * @file progressentry.java
 * @brief model de date imutabil pentru o inregistrare de progres a unui utilizator.
 *
 * contine datele monitorizate pe o anumita zi (greutate calorii antrenament) si notite optionale.
 */
public record ProgressEntry(
        long id,
        long userId,
        String entryDate,
        Double weightKg,
        Integer caloriesConsumed,
        Integer workoutMin,
        String notes
) {}
