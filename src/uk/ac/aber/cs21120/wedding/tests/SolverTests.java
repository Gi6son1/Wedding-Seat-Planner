package uk.ac.aber.cs21120.wedding.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import uk.ac.aber.cs21120.wedding.interfaces.IPlan;
import uk.ac.aber.cs21120.wedding.interfaces.IRules;
import uk.ac.aber.cs21120.wedding.interfaces.ISolver;
import uk.ac.aber.cs21120.wedding.solution.Plan;
import uk.ac.aber.cs21120.wedding.solution.Rules;
import uk.ac.aber.cs21120.wedding.solution.Solver;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Solver tests - note that these tests rely on your implementations of Plan and Rules working
 * correctly.
 */
public class SolverTests {

    /**
     * Return true if these two guests are on different tables. If guest a is not present
     * in the plan, a special assertion will occur. If guest b is not present, the method
     * will return false
     * @param p a plan
     * @param a a guest
     * @param b another guest
     * @return true if the guests are apart (or b is not present)
     */
    private boolean areApart(IPlan p, String a, String b){
        // run through the tables until we find the first guest,
        // then return true if the other guest isn't there
        for(int t=0;t<p.getNumberOfTables();t++){
            Set<String> g = p.getGuestsAtTable(t);
            if(g.contains(a)){
                return !g.contains(b);
            }
        }
        Assertions.fail("Expected guest not found in Plan - Plan probably has a bug.");
        return false; // never happens
    }

    /**
     * Return true if these two guests are on the same table. If guest a is not present
     * in the plan, a special assertion will occur. If guest b is not present, the method
     * will return false
     * @param p a plan
     * @param a a guest
     * @param b another guest
     * @return true if the guests are together (or b is not present)
     */

    private boolean areTogether(IPlan p, String a, String b){
        // run through the tables until we find the first guest,
        // then return true if the other guest is there
        for(int t=0;t<p.getNumberOfTables();t++){
            Set<String> g = p.getGuestsAtTable(t);
            if(g.contains(a)){
                return g.contains(b);
            }
        }
        Assertions.fail("Expected guest not found in Plan - Plan probably has a bug.");
        return false; // never happens
    }

    /**
     * Have all the guests been placed once, and once only?
     * @return true if all guests are actually present in the plan, and only once.
     */
    private boolean allGuestsPresent(IPlan p, String[] guests){
        // keep track of how many times each guest appears
        Map<String, Integer> countPerGuest = new HashMap<>();
        // now run through the plan
        for(int table=0;table<p.getNumberOfTables();table++){
            Set<String> guestsAtTable = p.getGuestsAtTable(table);
            for(String g: guestsAtTable){
                // increment the count for each guest at the table
                int ct = countPerGuest.getOrDefault(g, 0);
                countPerGuest.put(g, ct+1);
            }
        }
        // did each guest appear exactly once?
        for(String g: guests){
            int ct = countPerGuest.getOrDefault(g, 0);
            if(ct != 1)
                return false; // guest did not appear, or appeared more than once!
        }
        return true; // all is OK
    }

    /**
     * Utility for generating an array of N guests. For ease, we will just generate them
     * as numbers.
     */
    private String[] generateGuests(int n){
        String[] guests = new String[n];
        for(int i=0;i<n;i++){
            guests[i] = Integer.toString(i);
        }
        return guests;
    }

    /**
     * It should be possible to solve any problem with no rules.
     */
    @Test
    public void testNoRules(){
        String[] guests = generateGuests(48);
        IRules r = new Rules();
        // we try all combinations of seats and tables for 48 guests.
        for(int seats: new int[]{2, 3, 4, 6, 8, 12, 24}){
            int tables = 48/seats;
            // make a new plan for each combination
            IPlan p = new Plan(tables, seats);
            // and a new solver (but reuse the empty rule set we created earlier)
            ISolver s = new Solver(guests, p, r);
            // assert that we solved correctly
            Assertions.assertTrue(s.solve());
            System.out.println(p);
            // Make sure that each guest is seated exactly once.
            // I'm regenerating the guests in case the solver somehow modified the list.
            // It really shouldn't.
            Assertions.assertTrue(allGuestsPresent(p, generateGuests(48)));
        }
    }

    /**
     * On a 6-guest plan, with 2 tables of 3 guests, try every combination of two guests
     * who should be sat apart. Make sure the solver solves, and the guests are indeed apart.
     */
    @Test
    public void testOneApartRule() {
        for(int i=0;i<6;i++) {
            for (int j = 0; j < 6; j++) {
                // go through every combo of guests
                if(i!=j) {
                    String[] guests = generateGuests(6);
                    IPlan p = new Plan(2, 3);
                    IRules r = new Rules();
                    String a = guests[i];
                    String b = guests[j];
                    r.addMustBeApart(a,b);
                    ISolver s = new Solver(guests, p, r);
                    Assertions.assertTrue(s.solve());
                    Assertions.assertTrue(areApart(p,a,b));
                }
            }
        }
    }

    /**
     * On a 6-guest plan, with 2 tables of 3 guests, try every combination of two guests
     * who should be sat together. Make sure the solver solves, and the guests are indeed
     * at the same table.
     */
    @Test
    public void testOneTogetherRule(){
        for(int i=0;i<6;i++) {
            for (int j = 0; j < 6; j++) {
                // go through every combo of guests
                if(i!=j) {
                    String[] guests = generateGuests(6);
                    IPlan p = new Plan(2, 3);
                    IRules r = new Rules();
                    String a = guests[i];
                    String b = guests[j];
                    r.addMustBeTogether(a,b);
                    ISolver s = new Solver(guests, p, r);
                    Assertions.assertTrue(s.solve());
                    Assertions.assertTrue(areTogether(p,a,b));
                }
            }
        }
    }

    /**
     * Test two apart rules
     */
    @Test
    public void testTwoApartRules(){
        String[] guests = generateGuests(6);
        IPlan p = new Plan(2, 3);
        IRules r = new Rules();
        r.addMustBeApart("0","1");
        r.addMustBeApart("2","4");
        ISolver s = new Solver(guests, p, r);
        Assertions.assertTrue(s.solve());
        Assertions.assertTrue(areApart(p,"0","1"));
        Assertions.assertTrue(areApart(p,"2","4"));
    }

    /**
     * Test two together rules (which overlap, so forming a group of three)
     */
    @Test
    public void testTwoTogetherRules(){
        String[] guests = generateGuests(9);
        IPlan p = new Plan(3, 3);
        IRules r = new Rules();
        r.addMustBeTogether("0","3");
        r.addMustBeTogether("3","7");
        ISolver s = new Solver(guests, p, r);
        Assertions.assertTrue(s.solve());
        Assertions.assertTrue(areTogether(p,"0","3"));
        Assertions.assertTrue(areTogether(p,"3","7"));
    }

    /**
     * Test a together rule and an apart rule
     */
    @Test
    public void testApartAndTogetherRules(){
        String[] guests = generateGuests(6);
        IPlan p = new Plan(2, 3);
        IRules r = new Rules();
        r.addMustBeTogether("0","3");
        r.addMustBeApart("0","1");
        ISolver s = new Solver(guests, p, r);
        Assertions.assertTrue(s.solve());
        Assertions.assertTrue(areTogether(p,"0","3"));
        Assertions.assertTrue(areApart(p,"0","1"));
    }

    /**
     * Test a set of unsatisfiable apart rules: we demand that 1,2,3,4 are all at different tables from 0, but
     * there are only two tables with three seats each.
     */
    @Test
    public void testUnsatisfiableApart(){
        String[] guests = generateGuests(6);
        IPlan p = new Plan(2, 3);
        IRules r = new Rules();
        r.addMustBeApart("0","1");
        r.addMustBeApart("0","2");
        r.addMustBeApart("0","3");
        r.addMustBeApart("0","4");
        ISolver s = new Solver(guests, p, r);
        Assertions.assertFalse(s.solve());
    }

    /**
     * Test a set of unsatisfiable together rules - here, we demand 1,2,3,4 are all at the same table as 0,
     * but there are only 3 seats per table.
     */
    @Test
    public void testUnsatisfiableTogether(){
        String[] guests = generateGuests(6);
        IPlan p = new Plan(2, 3);
        IRules r = new Rules();
        r.addMustBeTogether("0","1");
        r.addMustBeTogether("0","2");
        r.addMustBeTogether("0","3");
        r.addMustBeTogether("0","4");
        ISolver s = new Solver(guests, p, r);
        Assertions.assertFalse(s.solve());
    }

    /**
     * This is the same problem as the example analysed for task 3 in depth,
     * only we use 0-5 instead of A-F. It requires a single backtrack.
     */
    @Test
    public void testABCDEF(){
        String[] guests = generateGuests(6);
        IRules r = new Rules();
        r.addMustBeTogether("0","3");
        r.addMustBeApart("0","1");
        r.addMustBeApart("2","3");

        IPlan p = new Plan(2, 3);
        ISolver s = new Solver(guests, p, r);

        Assertions.assertTrue(s.solve());   // did we solve it?

        // do the Rules say it's valid? (This may say "yes" when it isn't valid,
        // because Rules may have a bug.)
        Assertions.assertTrue(r.isPlanOK(p));

        // check out the solution. The algorithm is deterministic, so it always makes
        // the same solution. However, you may have done something unusual which still
        // works, so I'll just check each rule "by hand"

        Set<String> g0 = p.getGuestsAtTable(0);
        Set<String> g1 = p.getGuestsAtTable(1);

        Assertions.assertEquals(3, g0.size());  // both tables have 3 people
        Assertions.assertEquals(3, g1.size());

        // check 0-3 Together rule
        if(g0.contains("0"))
            Assertions.assertTrue(g0.contains("3"));
        else
            Assertions.assertTrue(g1.contains("3"));

        // 0-1 are enemies
        if(g0.contains("0"))
            Assertions.assertTrue(g1.contains("1"));
        else
            Assertions.assertTrue(g0.contains("1"));

        // 2-3 are enemies
        if(g0.contains("2"))
            Assertions.assertTrue(g1.contains("3"));
        else
            Assertions.assertTrue(g0.contains("3"));
    }

    /**
     * This is a test of the example problem given in the introduction. It requires a lot of backtracking.
     */
    @Test
    public void testComplexProblem(){
        String[] guests = {
                "John Smith",	"Mary Smith",	"Peter Jones",	"Anne Jones",	"Xiuyan Wang",
                "Elin Haf",	"Waldemar Król",	"Łucja Głowacka",	"Zixin Chen",	"Gruff Jones",
                "Luke Williams",	"Olivia Grey",	"Martin Peel",	"Evan Webern",	"Suzanne Higgs"

        };

        IRules r = new Rules();
        r.addMustBeTogether("John Smith","Mary Smith");
        r.addMustBeApart("John Smith","Anne Jones");
        r.addMustBeTogether("Xiuyan Wang","Zixin Chen");
        r.addMustBeApart("Olivia Grey",	"Martin Peel");
        r.addMustBeApart("Martin Peel",	"Mary Smith");
        r.addMustBeApart("Evan Webern",	"Martin Peel");
        r.addMustBeTogether("Suzanne Higgs",	"Olivia Grey");

        IPlan p = new Plan(3,5);
        ISolver s = new Solver(guests, p, r);

        Assertions.assertTrue(s.solve());

        Assertions.assertTrue(areTogether(p,"John Smith","Mary Smith"));
        Assertions.assertTrue(areApart(p,"John Smith","Anne Jones"));
        Assertions.assertTrue(areTogether(p,"Xiuyan Wang","Zixin Chen"));
        Assertions.assertTrue(areApart(p,"Olivia Grey",	"Martin Peel"));
        Assertions.assertTrue(areApart(p,"Martin Peel",	"Mary Smith"));
        Assertions.assertTrue(areApart(p,"Evan Webern",	"Martin Peel"));
        Assertions.assertTrue(areTogether(p,"Suzanne Higgs",	"Olivia Grey"));
    }
}
