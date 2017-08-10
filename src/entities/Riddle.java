package entities;

import enums.BoatThings;
import utils.SafeNumScanner;

import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import static enums.BoatThings.GOAT;
import static enums.BoatThings.LETTUCE;
import static enums.BoatThings.WOLF;

/**
 * Created by Paul Dennis on 8/9/2017.
 */
public class Riddle {

    //Boat-crossing riddle
    static Set<BoatThings> nearShore;
    static Set<BoatThings> farShore;
    static boolean boatIsNear = true;
    static BoatThings thingOnBoat;
    static boolean boatFull;
    public static void main(String[] args) {
        //Going from the side of the river that is near to the far shore
        nearShore = new HashSet<>();
        boatIsNear = true;
        nearShore.add(GOAT);
        nearShore.add(WOLF);
        nearShore.add(LETTUCE);
        farShore = new HashSet<>();
        thingOnBoat = null;
        boatFull = false;
        SafeNumScanner safeNumScanner = new SafeNumScanner(System.in);

        while (true) {
            System.out.println("What do you want to do?");
            System.out.println("1. Cross the river.");
            if (boatFull) {
                System.out.println("2. Take whatever's in the boat out");
            } else {

                System.out.println("2. Put something in the boat");
            }
            int response = safeNumScanner.getSafeNum(1, 2);
            if (response == 1) {
                if (boatIsNear) {
                    if (stillOk(nearShore)) {
                        boatIsNear = false;
                        System.out.println("Row row row, welcome to the far side.");

                    } else {
                        System.out.println("You lose");
                        break;
                    }
                } else {
                    if (stillOk(farShore)) {
                        boatIsNear = true;
                        System.out.println("Paddle paddle paddle, welcome back to the near side.");
                    } else {

                        System.out.println("You lose");
                        break;
                    }
                }
            }
            if (response == 2) {
                if (boatFull) { //Offload
                    if (boatIsNear) {
                        nearShore.add(thingOnBoat);
                    } else {
                        farShore.add(thingOnBoat);
                    }
                    boatFull = false;
                    thingOnBoat = null;
                } else { //Onload
                    BoatThings chosen;
                    if (boatIsNear) {
                        chosen = chooseThing(nearShore);
                        nearShore.remove(chosen);
                    } else {
                        chosen = chooseThing(farShore);
                        farShore.remove(chosen);
                    }
                    boatFull = true;
                    thingOnBoat = chosen;
                }
            }
            //Check victory
            printGameInfo();
            if (victory()) {
                System.out.println("You win!");
                break;
            }
        }
    }

    private static boolean victory () {
        if (farShore.size() == 3) {
            return true;
        }
        return false;
    }

    public static BoatThings chooseThing (Set<BoatThings> choices) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Which thing do you want to load? Available:");
        choices.stream()
                .map(e -> e.toString().toCharArray()[0])
                .forEach(e -> System.out.print("|" + e + "|"));
        String response = scanner.nextLine().toLowerCase();
        if (response.contains("g")) {
            if (choices.contains(GOAT)) {
                return GOAT;
            } else {
                System.out.println("Goat not available");
                return chooseThing(choices);
            }
        } else if (response.contains("w")) {
            if (choices.contains(WOLF)) {
                return WOLF;
            } else {
                System.out.println("Wolf not available");
                return chooseThing(choices);
            }
        } else if (response.contains("l")) {
            if (choices.contains(LETTUCE)) {
                return LETTUCE;
            } else {
                System.out.println("Lettuce not available");
                return chooseThing(choices);
            }
        } else {
            System.out.println("Response must contain G, W, or L. Try again");
            return chooseThing(choices);
        }
    }

    public static boolean stillOk (Set<BoatThings> unsupervised) {
        if (unsupervised.contains(GOAT) && unsupervised.contains(LETTUCE)) {
            System.out.println("Goat eats lettuce.");
            return false;
        }
        if (unsupervised.contains(GOAT) && unsupervised.contains(WOLF)) {
            System.out.println("Wolf eats goat.");
            return false;
        }
        return true;
    }

    public static void printGameInfo () {
        System.out.println("On the near/starting shore, you have:");
        nearShore.forEach(System.out::println);
        System.out.println("On the far/goal shore, you have:");
        farShore.forEach(System.out::println);
        if (boatFull) {
            System.out.println("On the boat: " + thingOnBoat);
        }
    }

    public static void print () {
        System.out.println("Near \\ R    \\   Far                          ");
        System.out.println("      \\ i    \\                               ");
        System.out.println("       \\ v    \\                             ");
        System.out.println("        \\ e    \\                            ");
        System.out.println("         \\ r    \\                             ");
    }
}
