package uk.ac.aber.cs21120.wedding.interfaces;

import java.util.List;
import java.util.Set;

/**
 * An interface for classes which implement a list of rules for a seating plan.
 *
 */
public interface IRules {
    /**
     * Add a rule that two guests must be seated at the same table.
     * Adding the same rule twice has no effect.
     * @param a a guest
     * @param b another guest
     */
    void addMustBeTogether(String a, String b);

    /**
     * Add a rule that two guests must never be seated at the same table.
     * Adding the same rule twice has no effect.
     * @param a a guest
     * @param b another guest
     */
    void addMustBeApart(String a, String b);

    /**
     * Return true if the given plan does not break any of the stored rules.
     * @param p a plan
     * @return true if the plan is OK, false if it breaks rules.
     */
    boolean isPlanOK(IPlan p);

}
