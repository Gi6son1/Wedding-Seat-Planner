package uk.ac.aber.cs21120.wedding.solution;

import uk.ac.aber.cs21120.wedding.interfaces.IPlan;
import uk.ac.aber.cs21120.wedding.interfaces.IRules;

import java.util.*;

/**
 * Class to blueprint what the rules of the wedding seating planner should look like.
 * It also includes the methods used to add to the rules, and check the validity of the current plan,
 * as given by the IRules Interface.
 *
 * @author Owain Gibson
 * @version 1.0
 */
public class Rules implements IRules {

    private List<Set<String>> friendGroups;
    private Map<String, Set<String>> guestEnemies;

    /**
     * Constructor for creating the Rules implementation.
     * It uses an arrayList for the friend groups and a Hashmap for the enemy lists
     */
    public Rules(){
        this.friendGroups = new ArrayList<>();
        this.guestEnemies = new HashMap<>();
    }

    /**
     * Method for adding a "friend" rule, where two guests HAVE to be sat at the same table
     * It also makes sure that the guests aren't enemies first, before adding the rule.
     * @param a a guest
     * @param b another guest
     */
    @Override
    public void addMustBeTogether(String a, String b) {
        boolean friendFound = false;
        boolean invalidCombination = false;
        if (guestEnemies.containsKey(a) && guestEnemies.get(a).contains(b)){
            System.out.println("These guests cannot be together, as they have already been declared enemies.");
        }
        else if (a.equals(b)){
            System.out.println("This rule requires that the guests are different people, otherwise it would be strange"); //checks that the user isn't trying to make a guest friends with itself
        }
        else{
            for (Set<String> friendGroup : friendGroups){
                if (friendGroup.contains(a) || friendGroup.contains(b)){
                    if (hasEnemy(friendGroup, a) || hasEnemy(friendGroup, b)) {
                        invalidCombination = true;
                    }
                    else{
                        if (friendGroup.contains(a)){
                            friendGroup.add(b);
                        }
                        else{
                            friendGroup.add(a);
                        }
                        friendFound = true;
                    }
                    break;
                }
            }
            if (invalidCombination){ //this will check to make sure that guests aren't enemies with other guests in
                                        //in that friend group, meaning that the "MUST-HAVE" group doesn't have any contradictions in it
                System.out.println("These people cannot be sat together " +
                        "as they will be sitting with enemies at the same table.");
            }
            else if (!friendFound){ //if these aren't already in friend groups, it makes a new one
                Set<String> newGroup = new HashSet<>(2);
                newGroup.add(a);
                newGroup.add(b);
                friendGroups.add(newGroup);
            }
        }
    }

    /**
     * Method for adding an "enemy" rule, where the guests in question aren't allowed to sit next to eachother.
     * It also checks to make sure that the guests aren't friends first before setting the rule.
     * @param a a guest
     * @param b another guest
     */
    @Override
    public void addMustBeApart(String a, String b) {
        if (checkFriends(a, b)){
            System.out.println("These guests have already been set as friends, therefore cannot be enemies.");
        }
        else if (b.equals(a)) { //if they are trying to make a guest an enemy of itself
            System.out.println("You cannot make a guest an enemy of itself");
        }
        else{
            makeEnemies(a, b);
        }
    }

    /**
     * Method for checking if two guests are explicitly friends
     * @param guestA a guest
     * @param guestB another guest
     * @return true if they are, false if they are not
     * //Runtime = O(F) + O(1) + O(1) = O(F+2) --> O(F) where F is the number of FriendGroup rules
     */
    private boolean checkFriends(String guestA, String guestB){
        for (Set<String> friendGroup: friendGroups){
            if (friendGroup.contains(guestA) && friendGroup.contains(guestB)){
                return true;
            }
        }
        return false;
    }

    /**
     * Method for making two guests enemies.
     * It means updating both of their enemy sets with eachother's name, or creating a new set if they didn't have one already
     * @param guestA a guest
     * @param guestB another guest
     * Runtime O(1) + O(1) + O(1) + O(1) + O(1) + O(1) + O(1) + O(1) = O(8) --> O(1)
     */
    private void makeEnemies(String guestA, String guestB){
        Set<String> currentEnemies = guestEnemies.containsKey(guestA)? guestEnemies.get(guestA): new HashSet<>();
        currentEnemies.add(guestB);
        guestEnemies.put(guestA, currentEnemies);

        currentEnemies = guestEnemies.containsKey(guestB)? guestEnemies.get(guestB): new HashSet<>();
        currentEnemies.add(guestA);
        guestEnemies.put(guestB, currentEnemies);
    }

    /**
     * Method for checking if the current plan obeys the rules of the Rules implementation.
     * It also contains a bit of code that skips the checking process if there are no explicit enemies
     * or friends beforehand, as this would mean that the seating of the guests didn't matter. (Reduces runtime if there are no rules)
     * @param p holds the plan to check
     * @return true if the plan holds up with the rules, false if it doesn't
     */
    @Override
    public boolean isPlanOK(IPlan p) {
        int numTables = p.getNumberOfTables();
        Set<String> guests;
        if (guestEnemies.size() == 0 && friendGroups.size() == 0){
            return true;
        }

        for (int i = 0; i<numTables; i++){
            guests = p.getGuestsAtTable(i);
            for (String guest: guests){
                if (hasEnemy(guests, guest)){
                    return false;
                }
                if ((guests.size() == p.getSeatsPerTable()) && !allFriendsPresent(guests)){
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Method to check if all the friends are present on the table.
     * If there are no explicit friends together on the table, meaning that the current guests aren't enemies
     * but are still fine with eachother, then the table is also accepted.
     * @param table holds the table to check
     * @return true if all friends are present/ everyone is content with their seating, false if otherwise
     * Runtime = O(G) * (O(F) + O(1) + O(1)) = O(G) * O(F+2) = O(GF + 2G) = O(G*F)  where F is the number of friendGroups rules, G is the number of guests
     */
    private boolean allFriendsPresent(Set<String> table){
        for (String guest: table){
            for(Set<String> friendGroup : friendGroups) {
                if (friendGroup.contains(guest) && !table.containsAll(friendGroup)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Method to check if the guest in question has any enemies among the group that in question
     * @param group holds the group of guests
     * @param guestToCheck holds the guest to check if they are someone's enemy
     * @return returns true if they are an enemy of someone, false if not
     * Runtime = O(G) * (O(1) + O(1)) = O(2G) --> O(G) where G is the number of guests
     */
    private boolean hasEnemy(Set<String> group, String guestToCheck){
        for (String guest: group){
            if (guestEnemies.containsKey(guest) && guestEnemies.get(guest).contains(guestToCheck)){
                return true;
            }
        }
        return false;
    }

    //ALTERNATE SOLUTION - TRIED TO MERGE MUST-HAVE COMBINATIONS TOGETHER BASED ON COMMON GUESTS, TO FORM ONE BIG MUST-HAVE GROUP
    //OUTCOME -- SUCCESSFULLY MERGED GROUPS AND RULES TESTS PASSED.
    /*
    The reason why this wasn't implemented:
    This function would've been most efficient if only called once all the rules were created. For this, I would've had to call it in the
    solver class, when the solve() function was called, as it means that all the rules are added and the guests are ready to be seated.
    Since the solver class stores the Rules class as an interface rather than the implementation, I could only call the methods that were defined in that interface,
    so an implementation-specific method wouldn't have been available to call, and we're not allowed to add to the interface methods, ARE we? because that would be oh so bad, wouldn't it? (really jim?) :)

    Without changing the interfaces:
    I COULD have called this method from inside the class, but since there is no way of checking when a rule is the final rule to be inputted, I would have to call this method every
    time a rule was added, drastically increasing the runtime. So it wouldn't have been worth it.

     */

    /*public List<Set<String>> organiseGroups(){ //this would've been the method that would've been called in the solver class
        return mergeGroups(new ArrayList<>(), friendGroups); //kicks off the recursive algorithm with an empty rules list and the list of friend rules in their raw, uncombined state
    }

    private List<Set<String>> mergeGroups (List<Set<String>> finalList, List<Set<String>> leftoverList){
        if (leftoverList.isEmpty()) return finalList; //base case - if there are no more uncombined, raw friend groups to look at, return the final updated list with the lovely, satisfying combined connections

        else{
            Iterator<Set<String>> iterator = leftoverList.iterator();
            Set<String> currentSet = iterator.next(); //the first set in the raw, uncombined list is set to the primary set to compare to the others in the list

            while(iterator.hasNext()){ //looks at each following set in the list
                Set<String> comparedSet = iterator.next();
                if (!Collections.disjoint(currentSet, comparedSet)){ //if they share any common guests, then by logic, they must ALL sit together
                    currentSet.addAll(comparedSet); //add the set to the primary set
                    iterator.remove(); //get rid of the old set from the leftover list, as it's been combined with the primary set and is now redundant
                }
            }
            finalList.add(currentSet); //once all following lists have been compared and added, the primary set now has all the people that must sit together, so it is added to the final list
            leftoverList.remove(currentSet); //the primary set is removed from the leftover list, as we don't need to look at it anymore
        }
        return mergeGroups(finalList, leftoverList); //we call the function again, with the sets that didn't have anything in common with the primary set,
                                                        so we can see if they have anything common amongst themselves and possibly compare them
    } */

}
