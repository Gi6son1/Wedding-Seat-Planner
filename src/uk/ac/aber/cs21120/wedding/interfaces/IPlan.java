package uk.ac.aber.cs21120.wedding.interfaces;

import java.util.Set;

/**
 * An interface for classes which implement the seating plan, storing which guests are sat at which table
 * as well as the number of tables and the number of seats at each table.
 *
 * Conforming classes MUST also have the following constructor:
 *
 *          public Plan(int tablect, int seatspertable)
 *
 * where tablect is the number of tables and seatspertable is the number of seats at each table.
 */
public interface IPlan {
    /**
     * return the number of seats per table. All tables have the same number of seats.
     * This doesn't change once the Plan has been created.
     * @return the number of seats, a positive non-zero integer
     */
    int getSeatsPerTable();

    /**
     * Return the number of tables.
     * This doesn't change once the Plan has been created.
     * @return the number of tables, a positive non-zero integer
     */
    int getNumberOfTables();

    /**
     * Add a guest to a table. If the guest is already seated at any table it will
     * do nothing. If the table is already full (i.e. the number of guests at that table is
     * equal to getSeatsPerTable()) it will do nothing. If the table number is less than zero,
     * or greater than or equal to getNumberOfTables(), it will raise IndexOutOfBoundsException.
     * @param table the table number
     * @param guest the name of the guest
     */
    void addGuestToTable(int table, String guest);

    /**
     * Remove a guest from any table they are sitting at. If the guest is not at any
     * table, it will do nothing.
     * @param guest the name of the guest
     */
    void removeGuestFromTable(String guest);

    /**
     * Return whether a guest is sitting at any table.
     * @param guest the name of the guest
     * @return true if the guest is at a table, false otherwise
     */
    boolean isGuestPlaced(String guest);

    /**
     * Return a set of the guests seated at a particular table. If the
     * table number of out of range it will raise IndexOutOfBoundsException.
     * @param t the table number
     * @return a set of strings - the guests at that table
     */
    Set<String> getGuestsAtTable(int t);
}
