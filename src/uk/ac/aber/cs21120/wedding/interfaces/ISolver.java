package uk.ac.aber.cs21120.wedding.interfaces;

/**
 * The class which solves the Plan. There must be a constructor of the form
 *          public Solver(String[] guests, IPlan p, IRules r)
 * where guests is an array of guest names, p is a reference to an object conforming
 * to the IPlan interface (Note: NOT Plan) and r is a reference to an object conforming to the
 * IRules interface (not Rules).
 */
public interface ISolver {
    /**
     * Attempt to solve the plan with the rules and guests provided in
     * the constructor. Will recurse.
     * @return true if a solution was found, false if no solution was found.
     */
    boolean solve();
}
