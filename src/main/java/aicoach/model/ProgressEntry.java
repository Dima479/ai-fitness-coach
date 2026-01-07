package aicoach.model;
/**
 * @file ProgressEntry.java
 * @brief Model de date imutabil pentru o inregistrare de progres a unui utilizator.
 *
 * Contine datele monitorizate pe o anumita zi (greutate, calorii, antrenament) si notite optionale.
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
