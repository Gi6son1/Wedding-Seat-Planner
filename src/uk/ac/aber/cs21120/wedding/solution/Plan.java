package uk.ac.aber.cs21120.wedding.solution;

import uk.ac.aber.cs21120.wedding.interfaces.IPlan;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A class used to model a wedding seating plan.
 * It contains a data structure used to hold the plan itself, as well as various methods that can
 * be used to access and modify that plan, as given by the IPlan Interface.
 *
 * @author Owain Gibson
 * @version 1.0
 */
public class Plan implements IPlan {

    private final int numberOfTables; //these are final bc the num tables/seats shouldn't be changeable once declared
    private final int seatsPerTable;

    private List<Set<String>> tables; //this will hold all the tables, with all the guests

    /**
     * Constructor to create the implementation of the seating plan.
     *
     * @param numberOfTables defines how many tables are included in the plan
     * @param seatsPerTable defines how many seats are available for each table
     */
    public Plan(int numberOfTables, int seatsPerTable){
        this.numberOfTables = numberOfTables;
        this.seatsPerTable = seatsPerTable;

        this.tables = new ArrayList<>(numberOfTables);
        for (int i = 0; i < numberOfTables; i++){
            this.tables.add(new HashSet<>(seatsPerTable)); //programming to implementation
        }
    }

    /**
     * Method to return the total number of seats at each table
     * @return returns the number of seats
     * Runtime --> O(1)
     */
    @Override
    public int getSeatsPerTable() {
        return seatsPerTable;
    }

    /**
     * Method to return the number of tables in the plan
     * @return returns the number of tables
     * Runtime --> O(1)
     */
    @Override
    public int getNumberOfTables() {
        return numberOfTables;
    } //

    /**
     * Method to add a guest to a table.
     * It first checks if the table number inputted corresponds to an actual table before doing anything.
     * Also only performs function if the guest is not null or a blank string
     * If the guest isn't already seated and the table isn't full, it adds them to the table.
     * @param table the table number
     * @param guest the name of the guest
     * @throws IndexOutOfBoundsException if the table number given does not match a table
     */
    @Override
    public void addGuestToTable(int table, String guest) throws IndexOutOfBoundsException{
        validateTable(table);

        if (validateGuestExistence(guest) && !isGuestPlaced(guest) && (tables.get(table).size() < seatsPerTable)){
            tables.get(table).add(guest);
        }
    }

    /**
     * Method to remove a guest from any table in the plan.
     * Only performs function if the guest is not null or a blank string
     * The tables are iterated through, and once the guest is removed from the current table,
     * the program doesn't need to search through the rest and stops early if possible.
     * @param guest the name of the guest
     */
    @Override
    public void removeGuestFromTable(String guest) {
        if (validateGuestExistence(guest)) {
            for (Set<String> table : tables) {
                if (table.remove(guest)) { //this returns a boolean based on if the guest was found and removed
                    // (because I'm a good student who reads the documentation like that)
                    break;
                }
            }
        }
    }

    /**
     * Method to check if a guest is placed in the seating plan
     * @param guest the name of the guest
     * @return returns true if the guest has been found in the plan, or false if they haven't been found (if they are null or blank, they also won't be in the plan)
     */
    @Override
    public boolean isGuestPlaced(String guest) {
        if (validateGuestExistence(guest)) {
            for (Set<String> table : tables) {
                if (table.contains(guest)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Method to return the table corresponding to the given table number,
     * after checking that the given number is not invalid
     * @param t the table number
     * @return the table as a Set
     * @throws IndexOutOfBoundsException if the number given does not match any table in the plan
     * Runtime --> O(1)
     */
    @Override
    public Set<String> getGuestsAtTable(int t) throws IndexOutOfBoundsException{
        validateTable(t);
        return tables.get(t);
    }

    /**
     * Method to validate the table number that is passed to functions.
     * It makes sure that the number corresponds to an actual table.
     * This is a separate validator method because otherwise this code would be basically duplicated across two methods otherwise. (and we don't like that).
     * @param t the table number to be checked
     * @throws IndexOutOfBoundsException if the table number does not correspond to an actual table in the plan
     * Runtime --> O(1)
     */
    private void validateTable(int t) throws IndexOutOfBoundsException{
        if (t < 0 || t > numberOfTables -1){ //if the number is < 0, this obviously doesn't correspond to an actual table
            throw new IndexOutOfBoundsException("This table doesn't exist, please double-check " +
                    "the table number you want to check.");
        }
    }

    private boolean validateGuestExistence(String guest){
        return guest != null && !guest.isBlank();
    }
}
