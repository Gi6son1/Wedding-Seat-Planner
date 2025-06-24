package uk.ac.aber.cs21120.wedding.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import uk.ac.aber.cs21120.wedding.interfaces.IPlan;
import uk.ac.aber.cs21120.wedding.solution.Plan;

import java.util.Set;

public class PlanTests {
    /**
     * Make sure that the seat count can be retrieved correctly from a new plan
     */
    @Test
    public void testSeatsPerTable(){
        IPlan p = new Plan(5,10);
        Assertions.assertEquals(10, p.getSeatsPerTable());
    }

    /**
     * Make sure the number of tables can be retrieved correctly from a new plan
     */
    @Test
    public void testNumberOfTables(){
        IPlan p = new Plan(12,72);
        Assertions.assertEquals(72, p.getSeatsPerTable());
    }
 
    /**
     * Make sure the number of guests at each table is zero in a new plan
     */
    @Test
    public void testGetGuestsCountInEmptyPlan(){
        IPlan p = new Plan(12,72);
        for(int i=0;i<12;i++) {
            Assertions.assertEquals(0, p.getGuestsAtTable(i).size());
        }
    }

    /**
     * Make sure the appropriate exception is thrown if we try to guest guests for a
     * table which is out of range
     */
    @Test
    public void testGetGuestsCountInvalidTable(){
        IPlan p = new Plan(12,72);
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> p.getGuestsAtTable(-1));
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> p.getGuestsAtTable(12));
    }

    /**
     * Make sure the appropriate exception is thrown if we try to add to a
     * table which is out of range
     */
    @Test
    public void testAddGuestInvalidTable(){
        IPlan p = new Plan(12,72);
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> p.addGuestToTable(-1,"Hamlet"));
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> p.addGuestToTable(12,"Hamlet"));

        // will also happen if Hamlet is already placed
        p.addGuestToTable(3,"Hamlet");
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> p.addGuestToTable(12,"Hamlet"));
    }

    /**
     * Make sure that adding a guest results to an empty plan results in a table count of 1 at
     * the correct table, zero at all the others, and that the guest string is correct.
     */
    @Test
    public void testAddGuest(){
        // we try all valid table numbers to make sure the bounds work correctly.
        for(int table=0;table<10;table++) {
            IPlan p = new Plan(10, 3);
            p.addGuestToTable(table, "Banquo");

            Set<String> guests = p.getGuestsAtTable(table);
            Assertions.assertEquals(1, guests.size());      // check how many guests
            Assertions.assertTrue(guests.contains("Banquo"));       // check the guest is correct for table 3
            Assertions.assertFalse(guests.contains("John of Gaunt"));       // this guest is nowhere

            // check no guests on all other tables
            for (int i = 0; i < 10; i++) {
                if (i != table) {
                    Assertions.assertEquals(0, p.getGuestsAtTable(i).size());
                }
            }
        }
    }


    /**
     * Check adding the same guest twice doesn't do anything
     */

    @Test
    public void testAddGuestTwice(){
        IPlan p = new Plan(10,3);
        p.addGuestToTable(3,"Banquo");
        p.addGuestToTable(3,"Banquo");  // will do nothing
        Set<String> guests = p.getGuestsAtTable(3);
        Assertions.assertEquals(1, guests.size());
        Assertions.assertTrue(guests.contains("Banquo"));
        // check no guests on all other tables
        for (int i = 0; i < 10; i++) {
            if (i != 3) {
                Assertions.assertEquals(0, p.getGuestsAtTable(i).size());
            }
        }
    }

    /**
     * Check adding the same guest twice to different tables also does nothing
     */

    @Test
    public void testAddGuestTwiceDifferentTables(){
        IPlan p = new Plan(10,3);
        p.addGuestToTable(3,"Banquo");
        p.addGuestToTable(4,"Banquo");  // will do nothing

        Set<String> guests = p.getGuestsAtTable(3);
        Assertions.assertEquals(1, guests.size());
        Assertions.assertTrue(guests.contains("Banquo"));

        // guest should not be at the second table. While the next block of code
        // ("check no guests on all other tables") also covers this case, the assertion
        // failing here points to a specific problem.

        guests = p.getGuestsAtTable(4);
        Assertions.assertEquals(0, guests.size());

        // check no guests on all other tables
        for (int i = 0; i < 10; i++) {
            if (i != 3) {
                Assertions.assertEquals(0, p.getGuestsAtTable(i).size());
            }
        }
    }

    /**
     *  Check adding different guests to different tables is OK
     */

    @Test
    public void testAddTwoGuestsDifferentTables(){
        IPlan p = new Plan(10,3);
        p.addGuestToTable(3,"Banquo");
        p.addGuestToTable(4,"Hamlet");  // this will work

        Set<String> guests = p.getGuestsAtTable(3);
        Assertions.assertEquals(1, guests.size());
        Assertions.assertTrue(guests.contains("Banquo"));
        guests = p.getGuestsAtTable(4);
        Assertions.assertEquals(1, guests.size());
        Assertions.assertTrue(guests.contains("Hamlet"));
        // check no guests on all other tables
        for(int i=0;i<10;i++) {
            if(i!=3 && i!=4) {
                Assertions.assertEquals(0, p.getGuestsAtTable(i).size());
            }
        }
    }

    /**
     * Check adding different guests to the same table is OK
     */

    @Test
    public void testAddTwoGuestsSameTable(){
        IPlan p = new Plan(10,3);
        p.addGuestToTable(3,"Banquo");
        p.addGuestToTable(3,"Hamlet");

        Set<String> guests = p.getGuestsAtTable(3);
        Assertions.assertEquals(2, guests.size());
        Assertions.assertTrue(guests.contains("Banquo"));
        Assertions.assertTrue(guests.contains("Hamlet"));

        // check no guests on all other tables
        for(int i=0;i<10;i++) {
            if(i!=3) {
                Assertions.assertEquals(0, p.getGuestsAtTable(i).size());
            }
        }
    }

    /**
     * Make sure adding too many guests does nothing
     */
    @Test
    public void testTooManyGuests(){
        IPlan p = new Plan(10,3);
        p.addGuestToTable(3,"Ophelia");
        p.addGuestToTable(3,"Hamlet");
        p.addGuestToTable(3, "Polonius");

        // make sure the three guests are correctly placed
        Set<String> guests;
        guests = p.getGuestsAtTable(3);
        Assertions.assertEquals(3, guests.size());
        Assertions.assertTrue(guests.contains("Ophelia"));
        Assertions.assertTrue(guests.contains("Polonius"));
        Assertions.assertTrue(guests.contains("Hamlet"));

        // try to add another
        p.addGuestToTable(3,"Banquo");

        // should be no change
        guests = p.getGuestsAtTable(3);
        Assertions.assertEquals(3, guests.size());
        Assertions.assertTrue(guests.contains("Ophelia"));
        Assertions.assertTrue(guests.contains("Polonius"));
        Assertions.assertTrue(guests.contains("Hamlet"));

        Assertions.assertTrue(p.isGuestPlaced("Ophelia"));
        Assertions.assertTrue(p.isGuestPlaced("Polonius"));
        Assertions.assertTrue(p.isGuestPlaced("Hamlet"));
        Assertions.assertFalse(p.isGuestPlaced("Banquo")); // NOT YOU.

    }


    /**
     * Ensure isGuestPlaced works as intended.
     */
    @Test
    public void testIsGuestPlaced(){
        IPlan p = new Plan(10,3);
        p.addGuestToTable(3,"Banquo");
        p.addGuestToTable(3,"Hamlet");

        Assertions.assertTrue(p.isGuestPlaced("Banquo"));
        Assertions.assertTrue(p.isGuestPlaced("Hamlet"));
        Assertions.assertFalse(p.isGuestPlaced("Polonius"));
    }

    /**
     * Make sure we can remove a guest
     */

    @Test
    public void testRemove(){
        // add two guests to the same table (and check size)
        IPlan p = new Plan(10,3);
        p.addGuestToTable(3,"Banquo");
        p.addGuestToTable(3,"Hamlet");
        Assertions.assertEquals(2, p.getGuestsAtTable(3).size());

        // remove one of them, and make sure that the remaining guest is placed, but not the
        // removed one
        p.removeGuestFromTable("Banquo");
        Assertions.assertTrue(p.isGuestPlaced("Hamlet"));
        Assertions.assertFalse(p.isGuestPlaced("Banquo"));

        // get the guests for that table and check it only contains the remaining guest
        Set<String> guests = p.getGuestsAtTable(3);
        Assertions.assertEquals(1, guests.size());
        Assertions.assertFalse(guests.contains("Banquo"));
        Assertions.assertTrue(guests.contains("Hamlet"));

        // check no guests on all other tables
        for(int i=0;i<10;i++) {
            if(i!=3) {
                Assertions.assertEquals(0, p.getGuestsAtTable(i).size());
            }
        }
    }

    /**
     * Make sure that removing a guest twice does nothing
     */
    @Test
    public void testRemoveTwice(){
        // add two guests to the same table (and check size)
        IPlan p = new Plan(10,3);
        p.addGuestToTable(3,"Banquo");
        p.addGuestToTable(3,"Hamlet");
        Assertions.assertEquals(2, p.getGuestsAtTable(3).size());

        // remove one of them, and make sure that the remaining guest is placed, but not the
        // removed one
        p.removeGuestFromTable("Banquo");
        Assertions.assertTrue(p.isGuestPlaced("Hamlet"));
        Assertions.assertFalse(p.isGuestPlaced("Banquo"));

        // get the guests for that table and check it only contains the remaining guest
        Set<String> guests = p.getGuestsAtTable(3);
        Assertions.assertEquals(1, guests.size());
        Assertions.assertFalse(guests.contains("Banquo"));
        Assertions.assertTrue(guests.contains("Hamlet"));

        // check no guests on all other tables
        for(int i=0;i<10;i++) {
            if(i!=3) {
                Assertions.assertEquals(0, p.getGuestsAtTable(i).size());
            }
        }

        // and do the whole remove again

        // remove one of them, and make sure that the remaining guest is placed, but not the
        // removed one
        p.removeGuestFromTable("Banquo");
        Assertions.assertTrue(p.isGuestPlaced("Hamlet"));
        Assertions.assertFalse(p.isGuestPlaced("Banquo"));

        // get the guests for that table and check it only contains the remaining guest
        guests = p.getGuestsAtTable(3);
        Assertions.assertEquals(1, guests.size());
        Assertions.assertFalse(guests.contains("Banquo"));
        Assertions.assertTrue(guests.contains("Hamlet"));

        // check no guests on all other tables
        for(int i=0;i<10;i++) {
            if(i!=3) {
                Assertions.assertEquals(0, p.getGuestsAtTable(i).size());
            }
        }


    }

    @Test
    public void testRemoveNonexistent() {
        // add two guests to the same table (and check size)
        IPlan p = new Plan(10,3);
        p.addGuestToTable(3,"Banquo");
        p.addGuestToTable(3,"Hamlet");
        Assertions.assertEquals(2, p.getGuestsAtTable(3).size());

        // remove a guest who doesn't exist; there should be no change.
        p.removeGuestFromTable("Feste"); // isn't even in this plan (or play, for that matter)

        Assertions.assertTrue(p.isGuestPlaced("Hamlet"));
        Assertions.assertTrue(p.isGuestPlaced("Banquo"));
        Assertions.assertFalse(p.isGuestPlaced("Feste"));

        // get the guests for that table and check it has both guests
        Set<String> guests = p.getGuestsAtTable(3);
        Assertions.assertEquals(2, guests.size());
        Assertions.assertTrue(guests.contains("Banquo"));
        Assertions.assertTrue(guests.contains("Hamlet"));

        // check no guests on all other tables
        for(int i=0;i<10;i++) {
            if(i!=3) {
                Assertions.assertEquals(0, p.getGuestsAtTable(i).size());
            }
        }
    }
}
