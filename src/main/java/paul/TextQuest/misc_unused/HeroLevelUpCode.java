/**
 * @author Paul Dennis (pd236m)
 * Sep 21, 2018
 */
package paul.TextQuest.misc_unused;

public class HeroLevelUpCode {
	@Deprecated
    public void show () {
    	/*
    	System.err.println("**In hero.show()");
        if (levelUpPlan == null) {
            initLevelUpPlan();
        }
        List<Integer> levelAmts = levelUpPlan.getExpAmounts();
        if (levelAmts.get(level) <= exp) {
            level++;
            if (levelUpPlan.levelingRestoresHealth()) {
            	health = maxHealth;
            }
            if (levelUpPlan.levelingRestoresSpells()) {
            	numSpellsAvailable = maxSpellsPerDay;
            }
            levelUpTodo = levelUpPlan.getLevelUpActions().get(level);
            textOut.println("You are now level " + level + ". You can:");
            levelUpTodo.stream()
                    .map(LevelUpCategory::getPrettyName)
                    .forEach(e -> textOut.println(e));
            //Else if there are remaining level up actions to take
            //return show();

        } else if (levelUpTodo != null && levelUpTodo.size() > 0) {
            LevelUpCategory category = levelUpTodo.get(0);
            textOut.println("Right now you can: " + LevelUpCategory.getPrettyName(category));
            textOut.println(LevelUpCategory.getPrompt(category));

            
        } else {
            System.err.println("Leaving hero.show(), returning FINISHED");
        }*/
    	
    }

    @Deprecated
    public void handleResponse (String response) {
    	/*
    	System.err.println("**In hero.handleResponse() with input " + response);
        LevelUpCategory category = levelUpTodo.get(0);
        response = response.trim().toLowerCase();
        switch (category) {
            case INC_STATS:
                if (response.equals("might") || response.equals("strength")) {
                    might++;
                    textOut.println("Might permanently increased by 1");
                    levelUpTodo.remove(0);
                } else if (response.equals("health") || response.equals("hp") || response.equals("hitpoints")) {
                    maxHealth += 5;
                    health = maxHealth;
                    textOut.println("Max HP increased by 5");
                    levelUpTodo.remove(0);
                } else if (response.startsWith("def")){
                    defense++;
                    textOut.println("Defense increased by 1 permanently");
                    levelUpTodo.remove(0);
                } else {
                    textOut.println("Could not read a stat");
                }
                break;

            case NEW_SKILL:
                if (response.contains("sneak") || response.contains("stealth")) {
                    skillMap.put("sneak", skillMap.get("sneak") + 1);
                    textOut.println("You've learned basic sneaking.");
                    levelUpTodo.remove(0);
                } else {
                    textOut.println("Could not find a skill (only one available is sneak - try that).");
                }
                break;


            case NEW_SPELL:
                MagicUniversity magicUniversity = MagicUniversity.getInstance();
                String spellMatch = magicUniversity.getSpellMatch(response);
                if (spellMatch != null) {
                    spellMap.put(spellMatch, possibleSpellMap.get(spellMatch));
                    textOut.println("You've learned " + StringUtils.addAOrAn(StringUtils.capitalize(spellMatch)) + " spell.");
                    spellbook.add(spellMatch);
                    levelUpTodo.remove(0);
                    maxSpellsPerDay++;
                    numSpellsAvailable = maxSpellsPerDay;
                } else {
                    textOut.println("Could not find the spell you want to learn.");
                }
                break;

        }
        */
    	throw new AssertionError("This method is deprecated. If you call it again, you will be too.");
    }
}
