package paul.TextQuest.utils;

/**
 * @author Paul Dennis
 * May 31, 2018
 */

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CountMap<E> {

	private Map<E, Integer> countMap;
	private boolean negativeAllowed = false;
	
	public CountMap () {
		countMap = new HashMap<>();
	}
	
	public void add (E e) {
		if (countMap.containsKey(e)) {
			countMap.put(e, countMap.get(e) + 1);
		} else {
			countMap.put(e, 1);
		}
	}
	
	public void add (E e, int amount) {
		if (amount < 1) {
			throw new AssertionError("Cannot add a negative/zero amount. Use subtract().");
		}
		if (countMap.containsKey(e)) {
			countMap.put(e, countMap.get(e) + amount);
		} else {
			countMap.put(e, amount);
		}
	}
	
	public void remove (E e) {
		if (countMap.containsKey(e)) {
			int amount = countMap.get(e);
			if (amount >= 1) {
				amount--;
			}
		}
	}
	
	public void removeAll (E e) {
		countMap.put(e, 0);
	}
	
	public void subtract (E e, int amount) {
		if (amount < 1) {
			throw new AssertionError("Cannot subtract a negative/zero amount. Use add().");
		}
		if (countMap.containsKey(e)) {
			countMap.put(e, countMap.get(e) - amount);
			if (!negativeAllowed && countMap.get(e) < 0) {
				throw new AssertionError("Negative values not allowed in this CountMap.");
			}
		} else {
			throw new AssertionError("Cannot subtract - don't even have a mapping.");
		}
	}
	
	public void addInitialZeroCount (Collection<E> group) {
		for (E e : group) {
			countMap.put(e, 0);
		}
	}
	
	public Map<E, Integer> getMap () {
		return countMap;
	}
	
	public Integer get (E k) {
		return countMap.get(k);
	}
	
	public int getSum () {
		int sum = 0;
		for (E e : countMap.keySet()) {
			sum += countMap.get(e);
		}
		return sum;
	}
	
	public Set<E> keySet () {
		return countMap.keySet();
	}
	
	@Override
	public String toString () {
		//Class<? extends Object> class1 = countMap.keySet().stream().collect(Collectors.toList()).get(0).getClass();

		//String response = "CountMap for " + class1.getSimpleName() + "s";
		String response = "";
		for (E e : countMap.keySet()) {
			response += "\n\t" + e + ", " + countMap.get(e);
		}
		
		return response;
	}
}
