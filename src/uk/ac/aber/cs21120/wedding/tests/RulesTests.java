package uk.ac.aber.cs21120.wedding.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import uk.ac.aber.cs21120.wedding.interfaces.IPlan;
import uk.ac.aber.cs21120.wedding.interfaces.IRules;
import uk.ac.aber.cs21120.wedding.solution.Plan;
import uk.ac.aber.cs21120.wedding.solution.Rules;

public class RulesTests {

    /**
     * Create a plan and seat guests using variable arguments. The guests passed in will be divided into tables, so
     * if we have 3 seats per table and 2 tables, the guests "A","B","C","D","E","F" will be put onto
     * tables as (A,B,C),(D,E,F). If a string is null, no guest will be added (allowing you to make blank spaces)
     * @param tablect       number of tables
     * @param seatspertable seats per table
     * @param values        lots of strings - the guests
     * @return a new plan
     */
    private IPlan createPlan(int tablect, int seatspertable, String... values){
        IPlan p = new Plan(tablect, seatspertable);
        if(values.length != tablect*seatspertable)
            throw new RuntimeException("Length of guest list incorrect in createPlan");
        int i=0;
        for(int t=0;t<tablect;t++){
            for(int s=0;s<seatspertable;s++){
                String g = values[i++];
                if(g!=null)
                    p.addGuestToTable(t, g);
            }
        }
        return p;
    }

    /**
     * Make sure adding rules doesn't crash (even if the same rule is added twice)
     */
    @Test
    public void testAddMustBeTogether() {
        IRules r = new Rules();
        r.addMustBeTogether("A", "B");
        r.addMustBeTogether("A", "C");
        r.addMustBeTogether("A", "B");
        r.addMustBeTogether("D", "F");
        r.addMustBeTogether("E", "F");
        r.addMustBeTogether("F", "G");
        r.addMustBeTogether("A", "F");

        r.addMustBeApart("A", "B");
        r.addMustBeApart("A", "C");
        r.addMustBeApart("A", "B");
    }

    /**
     * Make sure empty rule sets are OK
     */
    @Test
    public void testEmptyRules(){
        IPlan p = createPlan(2,2, "A","B","C","D");

        IRules r = new Rules();
        Assertions.assertTrue(r.isPlanOK(p));
    }

    /**
     * Make sure that the must-be-together rule works on a full table.
     * This rule should only ever be applied on full tables - tables
     * with a space might get the "friend" seated later on.
     */
    @Test
    public void testMustBeTogetherFullTable(){
        IPlan p = createPlan(2,2, "A","B","C","D");

        // make a rule set in which A and C have to sit together, so the rules
        // are broken
        IRules r = new Rules();
        r.addMustBeTogether("A","C");
        Assertions.assertFalse(r.isPlanOK(p));

        // make a rule set in which A and B have to sit together, so the rules
        // are met
        r = new Rules();
        r.addMustBeTogether("A","B");
        Assertions.assertTrue(r.isPlanOK(p));
    }

    /**
     * Test two rules in the "must be together" category on a full table.
     * The first rule should still hold when a second is added.
     */
    @Test
    public void testTwoMustBeTogetherRulesFullTable(){
        // make a rule set in which A and C have to sit together, and so do A and B.
        IRules r = new Rules();
        r.addMustBeTogether("A","C");
        r.addMustBeTogether("A","B");

        IPlan p = createPlan(2,2, "A","B","C","D");
        Assertions.assertFalse(r.isPlanOK(p)); // here the first rule is broken

        p = createPlan(2,2, "A","C","B","D");
        Assertions.assertFalse(r.isPlanOK(p)); // here the second rule is broken

        p = createPlan(2,3, "A","B","C","D","E","F");
        Assertions.assertTrue(r.isPlanOK(p)); //  but this is OK (ABC)(DEF)

    }

    /**
     * "Friends" rules don't have any effect on tables which aren't full
     */
    @Test
    public void testMustBeTogetherRulesPartialTable(){
        // note the spare two seats here, so we have A,B,empty at one table and C,D,empty at the other.
        IPlan p = createPlan(2,3, "A","B",null,"C","D",null);

        // make a rule set in which A and C have to sit together, so the rules
        // are broken
        IRules r = new Rules();
        r.addMustBeTogether("A","C");
        Assertions.assertTrue(r.isPlanOK(p)); // but the plan is OK, because that table is not full.
    }

    /**
     * Test a single "enemies" rule
     */
    @Test
    public void testMustBeApartRule(){
        IPlan p = createPlan(2,2, "A","B","C","D");

        IRules r = new Rules();
        r.addMustBeApart("A","B");
        Assertions.assertFalse(r.isPlanOK(p)); // that plan is NOT OK.
    }

    /**
     * Test two "enemies" rules
     */
    @Test
    public void testTwoMustBeApartRules(){
        IRules r = new Rules();
        r.addMustBeApart("C","D");
        r.addMustBeApart("B","D");

        IPlan p = createPlan(2,2, "A","B","C","D");
        Assertions.assertFalse(r.isPlanOK(p)); // that plan is NOT OK.

        p = createPlan(2,2, "A","C","B","D");
        Assertions.assertFalse(r.isPlanOK(p)); // that plan is still NOT OK.
    }

    /**
     * Test that "enemies" rules still apply on partially filled tables
     */
    @Test
    public void testMustBeApartRulesPartialTable(){
        IPlan p = createPlan(2,3, "A","B",null,"C","D",null);

        // these rules should STILL fail even though the tables are only partially full
        IRules r = new Rules();
        r.addMustBeApart("C","D");
        Assertions.assertFalse(r.isPlanOK(p)); // that plan is NOT OK.
        r.addMustBeApart("B","D");
        Assertions.assertFalse(r.isPlanOK(p)); // that plan is still NOT OK.
    }

    /**
     * Test that apart and together rules work together.
     */

    @Test
    public void testApartAndTogether(){
        IRules r = new Rules();
        r.addMustBeTogether("A","B");
        r.addMustBeApart("B","C");

        IPlan p = createPlan(2,2, "A","B","C","D");
        Assertions.assertTrue(r.isPlanOK(p));

        p = createPlan(2,2, "A","C","B","D"); // breaks rule 1
        Assertions.assertFalse(r.isPlanOK(p));

        p = createPlan(2,3, "A","B","C","D","E","F"); // breaks rule 2
        Assertions.assertFalse(r.isPlanOK(p));
    }

}