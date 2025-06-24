package uk.ac.aber.cs21120.wedding.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import uk.ac.aber.cs21120.wedding.interfaces.IPlan;
import uk.ac.aber.cs21120.wedding.interfaces.IRules;
import uk.ac.aber.cs21120.wedding.interfaces.ISolver;
import uk.ac.aber.cs21120.wedding.solution.Plan;
import uk.ac.aber.cs21120.wedding.solution.Rules;
import uk.ac.aber.cs21120.wedding.solution.Solver;

public class AddedTests {

    /**
     * Create a plan and seat guests using variable arguments. The guests passed in will be divided into tables, so
     * if we have 3 seats per table and 2 tables, the guests "A","B","C","D","E","F" will be put onto
     * tables as (A,B,C),(D,E,F). If a string is null, no guest will be added (allowing you to make blank spaces)
     *
     * @param tablect       number of tables
     * @param seatspertable seats per table
     * @param values        lots of strings - the guests
     * @return a new plan
     */
    private IPlan createPlan(int tablect, int seatspertable, String... values) {
        IPlan p = new Plan(tablect, seatspertable);
        if (values.length != tablect * seatspertable)
            throw new RuntimeException("Length of guest list incorrect in createPlan");
        int i = 0;
        for (int t = 0; t < tablect; t++) {
            for (int s = 0; s < seatspertable; s++) {
                String g = values[i++];
                if (g != null)
                    p.addGuestToTable(t, g);
            }
        }
        return p;
    }

    /**
     * Test that checks if blank, whitespace, or null guests can be added to the plan.
     */
    @Test
    public void testAddEmptyOrNullGuest() {
        IPlan p = new Plan(1, 2);
        p.addGuestToTable(0, "");
        p.addGuestToTable(0, "   ");
        p.addGuestToTable(0, null);

        Assertions.assertEquals(0, p.getGuestsAtTable(0).size());
    }

    /**
     * Test that checks if an empty, whitespace, or null guest can be removed from the plan.
     */
    @Test
    public void testRemoveNullOrEmptyGuest() {
        IPlan p = new Plan(2, 2);
        p.addGuestToTable(0, "A");
        p.addGuestToTable(0, "B");
        p.addGuestToTable(1, "C");
        p.addGuestToTable(1, "D");
        p.removeGuestFromTable(null);
        p.removeGuestFromTable("");
        p.removeGuestFromTable("  ");

    }

    /**
     * Test to check if a guest can be made an "enemy" of itself
     */
    @Test
    public void testMakeGuestItsOwnEnemy() {
        IRules r = new Rules();
        r.addMustBeApart("A", "A");

        IPlan p = createPlan(1, 1, "A");
        Assertions.assertTrue(r.isPlanOK(p));
    }

    /**
     * Test to check if an empty plan doesn't crash the solver algorithm
     */
    @Test
    public void testEmptyPlan() {
        IRules r = new Rules();
        IPlan p = new Plan(0, 0);
        String[] guests = new String[0];
        ISolver s = new Solver(guests, p, r);

        Assertions.assertTrue(s.solve());
    }

}
