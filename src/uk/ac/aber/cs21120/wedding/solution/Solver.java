package uk.ac.aber.cs21120.wedding.solution;

import uk.ac.aber.cs21120.wedding.interfaces.IPlan;
import uk.ac.aber.cs21120.wedding.interfaces.IRules;
import uk.ac.aber.cs21120.wedding.interfaces.ISolver;

import java.util.List;
import java.util.Set;

/**
 * Class to blueprint the solver for solving the wedding planner problem
 * It contains the methods needed for solving such a problem, given by the ISolver interface.
 *
 * @author Owain Gibson
 * @version 1.0
 */
public class Solver implements ISolver {

    private String[] guests;
    private IPlan plan;
    private IRules rules;

    /**
     * Constructor for creating the Solver implementation, it holds all the necessary variables for solving the problem
     *
     * @param guests holds the array of guests
     * @param plan   holds the plan object to add to/remove guests from
     * @param rules  holds the rules object to check against the plan
     */
    public Solver(String[] guests, IPlan plan, IRules rules) {
        this.guests = guests;
        this.plan = plan;
        this.rules = rules;
    }

    /**
     * Method for solving the problem.
     * It uses back-tracking to recursively add/remove guests from the plan, making sure that every guest
     * is seated on a table with no enemies, but also with people that they are friends with.
     *
     * @return true if the current implementation of the problem is solvable given the guests and rules, false if not
     */
    @Override
    public boolean solve() {
        int numTables = plan.getNumberOfTables();
        int seatsPerTable = plan.getSeatsPerTable();
        boolean result;
        int unfilledSeats;

        for (int tableNumber = 0; tableNumber < numTables; tableNumber++) {
            Set<String> currentTable = plan.getGuestsAtTable(tableNumber);
            unfilledSeats = seatsPerTable - currentTable.size();

            for (int i = 0; i < unfilledSeats; i++) {

                for (String guest : guests) {

                    if (!plan.isGuestPlaced(guest)) {
                        plan.addGuestToTable(tableNumber, guest);
                        if (rules.isPlanOK(plan)) {
                            result = solve();
                            if (result) {
                                return true;
                            }
                        }
                        plan.removeGuestFromTable(guest);
                    }
                }

                return false;
            }
        }
        return true;
    }

    //ALTERNATE SOLUTION - TRIED TO PRE-FILL TABLES WITH MUST-HAVE GROUPS TO REDUCE RUNTIME OF BRUTE FORCE RECURSIVE SOLVER ALGORITHM
    //OUTCOME -- SUCCESSFULLY MERGED GROUPS AND RULES TESTS PASSED, HOWEVER SOLVER TESTS FAILED WHEN IMPLEMENTING THE PRE-FILL
    /*
    Why this failed:
    Pre-emptively filling each table with a friend group would work well initially, as it would mean that those friends who were always guaranteed to
    be together, were put together, so the algorithm wouldn't need to brute force them together after many tries/failures.

    What the algorithm also did was that if another friend group was already at the inspected table, it would look for an empty table instead
    so that you wouldn't have one fully-filled table consisting of only friend groups, meaning that once the actual solve method was called, you didn't have a leftover table that
    you could only fill with people who possibly weren't friends and could've possibly been enemies.
    i.e. Guests: {A B C D E F G H}. Friend groups: {A B}, {C D}. ENEMIES: {E F}
    Without this algorithm condition, pre-filling 2 tables could've been {A B C D} {_ _ _ _}. Then solver class HAS to put E and F on the second table together, breaking the rule.
    With this condition, pre-filling gives {A B _ _} {C D _ _}, meaning that E and F can go on different tables.

    However, the problem with this algorithm is that it didn't take into account the sizes of each friend group and if filling the tables with certain friend groups would leave room for other ones.
    i.e. Guests: {A B C D E F G H I J}. Friend groups: {A B C} {E F} {G H I J}
    Pre-filling could give {A B C _ _} {E F _ _ _}, meaning that in solve(), there is no room to put {G H I J} together, and thus the algorithm would fail.

    If I had more time, I might've improved on this algorithm to make it work, however by that point I was tired and gave up,
    as slightly improving on a brute force algorithm seems pointless when there are algorithms in general for this wedding seating planner. And I'm also lazy ¯\_(ツ)_/¯
    */

    /*private void preFill() {
        List<Set<String>> friends = rules.organiseGroups(); //organise the groups into final seating rules (i.e. rule A B C and rule E F C would merge into a combined rule A B C E F)
        int numTables = plan.getNumberOfTables();
        int seatsPerTable = plan.getSeatsPerTable();
        int unfilledSeats;

        for (Set<String> friendGroup : friends) {

            for (int tableNumber = 0; tableNumber < numTables; tableNumber++) {
                Set<String> currentTable = plan.getGuestsAtTable(tableNumber);
                unfilledSeats = seatsPerTable - currentTable.size();

                if (unfilledSeats == seatsPerTable && friendGroup.size() <= unfilledSeats){ //if no one at table and they can all fit
                    currentTable.addAll(friendGroup); //add the group to the table
                    break; //move onto the next group
                }
            }
        }
    }*/
}

