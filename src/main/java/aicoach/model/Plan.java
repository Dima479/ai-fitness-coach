package aicoach.model;
/**
 * @file Plan.java
 * @brief Model de date imutabil pentru un plan asociat unui utilizator.
 *
 * Un plan poate reprezenta un plan de nutritie sau antrenament , stocat ca text/continut generat.
 */

public record Plan(
        long id,
        long userId,
        String planType,
        String content,
        String createdAt
) {}
