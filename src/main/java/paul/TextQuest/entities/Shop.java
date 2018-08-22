package paul.TextQuest.entities;

import paul.TextQuest.parsing.TextInterface;
import paul.TextQuest.utils.SafeNumScanner;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Created by Paul Dennis on 8/10/2017.
 */
public class Shop extends Location {

    private String name;
    private List<BackpackItem> itemsForSale;

    private List<BackpackItem> itemsBoughtByShop;

    private double buyRate = 1.0;

    private TextInterface textOut;

    public Shop () {

    }

    public Shop(String name, List<BackpackItem> itemsForSale) {
        this.name = name;
        this.itemsForSale = itemsForSale;
    }

    public int sellItems (List<BackpackItem> itemsToSell) {
        itemsBoughtByShop.addAll(itemsToSell);
        return itemsToSell.stream()
                .mapToInt(e -> (int)(e.getValue() * buyRate))
                .sum();
    }

    public BackpackItem buyItem (String itemName) {
        BackpackItem desired = null;
        try {
            desired = itemsForSale.stream()
                    .filter(e -> e.getName().equals(itemName))
                    .findFirst().get();
        } catch (NoSuchElementException ex) {}
        if (desired != null) {
            itemsForSale.remove(desired);
        }
        return desired;
    }

    public int sellSomeItems (List<BackpackItem> items) {
        Scanner scanner = new Scanner(System.in);

        List<BackpackItem> itemsToSell = items.stream()
                .filter (e -> {
                    textOut.println("Do you want to sell " + e + "?");
                    String response = scanner.nextLine().toLowerCase();
                    if (response.contains("y")) {
                        return true;
                    }
                    return false;
                })
                .collect(Collectors.toList());

        return sellItems(itemsToSell);
    }

    public void browseItems () {
        int yourGold = 10000;
        SafeNumScanner safeNumScanner = new SafeNumScanner(System.in);

        while (true) {
            textOut.println("You have " + yourGold + " gold.");
            textOut.println("Items for sale:");
            for (int i = 1; i <= itemsForSale.size(); i++) {
                textOut.println(i + ". " + itemsForSale.get(i));
            }
            textOut.println("Which item number do you want?");
            int itemNum = safeNumScanner.getSafeNum(1, itemsForSale.size());
            BackpackItem item = itemsForSale.get(itemNum);
            if (yourGold >= item.getValue()) {
                itemsForSale.remove(item);
                textOut.println("You bought " + item.getName() + " for " + item.getValue() + " gold.");
                yourGold -= item.getValue();
            }
        }
    }

    public static void main(String[] args) {
        Shop shop = new Shop();
        List<BackpackItem> itemsForSale = new ArrayList<>();
        itemsForSale.add(new BackpackItem("Sword of 1,000 Truths", 1000));
        itemsForSale.add(new BackpackItem("Potion", 50));
        itemsForSale.add(new BackpackItem("Potion", 50));
        itemsForSale.add(new BackpackItem("Potion", 50));


        shop.browseItems();
    }

    public void setTextOut (TextInterface textOut) {
        this.textOut = textOut;
    }
}
