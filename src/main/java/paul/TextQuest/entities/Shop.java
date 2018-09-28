package paul.TextQuest.entities;


import java.util.List;


/**
 * Created by Paul Dennis on 8/10/2017.
 */
public class Shop {

    private String name;
    private List<BackpackItem> itemsForSale;
    private double buyRate = 1.0;
    
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<BackpackItem> getItemsForSale() {
		return itemsForSale;
	}
	public void setItemsForSale(List<BackpackItem> itemsForSale) {
		this.itemsForSale = itemsForSale;
	}
	public double getBuyRate() {
		return buyRate;
	}
	public void setBuyRate(double buyRate) {
		this.buyRate = buyRate;
	}
    
    

}
