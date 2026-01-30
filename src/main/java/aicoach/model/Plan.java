package aicoach.model;
/**
 * @file plan.java
 * @brief model de date imutabil pentru un plan asociat unui utilizator.
 *
 * un plan poate reprezenta un plan de nutritie sau antrenament  stocat ca text/continut generat.
 */

public record Plan(
        long id,
        long userId,
        String planType,
        String content,
        String createdAt
) {}
