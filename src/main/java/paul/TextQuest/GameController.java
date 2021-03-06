package paul.TextQuest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import paul.TextQuest.entities.Dungeon;
import paul.TextQuest.entities.Hero;
import paul.TextQuest.gameplan.DungeonGroup;
import paul.TextQuest.gameplan.DungeonInfo;
import paul.TextQuest.gameplan.GamePlan;
import paul.TextQuest.utils.DefeatExceptionMessage;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by Paul Dennis on 8/16/2017.
 */
@Controller
public class GameController {
    
    private Map<String, String> userMap;
    
    public static final String USERS_FILE = "save_data/users.txt";
    public static final String GAME_PLAN_LOCATION = "content_files/game/default_gameplan.json";
    
    public static final String VERSION_STR = "0.0.12";
    public static final String UNAVAILBLE_STR = "?????";
    
    //Currently Gameplans can have multiple dungeongroups.
    //quick hack to select the first one. TODO remove when expanding
    public static final int FIRST_GROUP = 0;
    
    @RequestMapping(path = "/", method = RequestMethod.GET)
    public String home (Model model, HttpSession session) {
    	String username = (String) session.getAttribute("username");
    	if (username == null) {
    		model.addAttribute("username", "Not logged in");
    	} else {
    		model.addAttribute("username", username);
    	}
    	model.addAttribute("version", VERSION_STR);
        return "index";
    }
    
    @RequestMapping(path = "/login", method = RequestMethod.GET)
    public String loginScreen (HttpSession session) {
    	//TODO: remove temp hack
    	//return "login";
    	
    	session.setAttribute("username", "paul");
    	return "redirect:/load-hero";
    }
    
    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public String login (HttpSession session, String username, String password) {
    	if (userMap == null) {
    		initUserMap();
    	}
    	if (userMap.containsKey(username)) { //User exists
    		if (!userMap.get(username).equals(password)) { //Password incorrect
    			return "badpw";
    		}
    	} else { //User does not exist
    		addNewUser(username, password);
    	}
    	session.setAttribute("username", username);
    	return "redirect:/load-hero";
    }

    @RequestMapping(path = "/game", method = RequestMethod.GET)
    public String game (Model model, HttpSession session) throws IOException {
    	model.addAttribute("version", VERSION_STR);
    	String username = (String) session.getAttribute("username");
    	if (username == null) {
    		return "redirect:/login";
    	}
    	model.addAttribute("username", username);
    	
    	DungeonRunner dungeonRunner = (DungeonRunner) session.getAttribute("dungeonRunner");
    	dungeonRunner.show();
    	addOutputTextToModel(model, dungeonRunner.getTextOut());
    	
        model.addAttribute("location", dungeonRunner.getDungeon().getDungeonName());
        model.addAttribute("roomName", dungeonRunner.getHero().getLocation().getName());
        return "game";
    }
    
    @RequestMapping(path = "/airship", method = RequestMethod.GET)
    public String airship (Model model, HttpSession session) {
    	model.addAttribute("version", VERSION_STR);
		String username = (String) session.getAttribute("username");
		if (username == null) {
    		return "redirect:/login";
    	}
    	Hero hero = (Hero) session.getAttribute("hero");
    	model.addAttribute("username", username);
    	model.addAttribute("hero", hero);
    	
    	int level = hero.getLevel();
    	int exp = hero.getExp();
    	
    	List<Integer> expAmounts = Hero.getLevelUpPlan().getExpAmounts();
    	if (expAmounts.get(level) <= exp) {
    		model.addAttribute("levelUpAvailable", true);
    	}
    	
    	GamePlan gamePlan = (GamePlan) session.getAttribute("gamePlan");
    	//Eventually this would be replaced by the GamePlan that contains a dungeongroup
    	
    	if (gamePlan == null) {
    		gamePlan = buildGamePlan();
    		session.setAttribute("gamePlan", gamePlan);
    	}
    	DungeonGroup dungeonGroup = gamePlan.getDungeonGroups().get(FIRST_GROUP);
    	addDungeonInfoToModel(model, dungeonGroup, hero.getClearedDungeons());
    	
    	return "airship";
    }

    @RequestMapping(path = "/submit-action", method = RequestMethod.POST)
    public String submitAction (@RequestParam String userInput, Model model, HttpSession session) {
        DungeonRunner runner = (DungeonRunner) session.getAttribute("dungeonRunner");
        TextInterface textOut = runner.getTextOut();
        if (!userInput.equals("")) {
            textOut.debug("You entered: \"" + userInput + "\"");
        }
        //TODO - temporary code. Change/remove
        if (userInput.equals("leave")) {
        	System.err.println("~Leave command detected.");
        	textOut.debug("~Leave command detected.");
        	Dungeon dungeon = runner.getDungeon();
        	System.err.println(dungeon);
        	System.err.println("dungeon.isCleared() = " + dungeon.isCleared());
        	if (runner.getDungeon().isCleared()) {
        		textOut.debug("~And dungeon is cleared.");
        		Hero hero = runner.getHero();
        		String username = (String) session.getAttribute("username");
        		Hero.saveHeroToFile(username, hero);
        		return "redirect:/airship";
        	} else {
        		textOut.debug("~But dungeon isn't cleared.");
        	}
        }
        try {
        	runner.handleResponse(userInput);
        } catch (DefeatExceptionMessage ex) {
        	return "dead";
        }
        
         
        return "redirect:/game";
    }
    
    @RequestMapping(path = "/load-hero", method = RequestMethod.GET)
    public String loadHeroView (HttpSession session, Model model) {
    	model.addAttribute("version", VERSION_STR);
    	String username = (String) session.getAttribute("username");
    	List<String> heroList = Hero.getHeroListForUser(username);
    	model.addAttribute("heroList", heroList);
    	model.addAttribute("username", username);
    	return null;
    }
    
    @RequestMapping(path = "/load-hero", method = RequestMethod.POST)
    public String receiveLoadHero (HttpSession session, Model model, String heroName) {
    	System.err.println("!Loading hero: " + heroName);
    	String username = (String) session.getAttribute("username");
    	Hero hero = Hero.loadHeroFromFile(username, heroName);
    	if (hero == null) {
    		GamePlan gamePlan = (GamePlan) session.getAttribute("gamePlan");
    		if (gamePlan == null) {
        		gamePlan = buildGamePlan();
        		session.setAttribute("gamePlan", gamePlan);
        	}
    		hero = new Hero(gamePlan.getHeroStartingInfo(), heroName);
    	}
    	session.setAttribute("hero", hero);
    	return "redirect:/airship";
    }
    
    @RequestMapping(path = "/startDungeon", method = RequestMethod.GET)
    public String startDungeon (@RequestParam String dungeonName, HttpSession session) {
    	System.err.println("!Attempting to start dungeon with name: " + dungeonName);
    	
    	GamePlan gamePlan = (GamePlan) session.getAttribute("gamePlan");
    	DungeonGroup dungeonGroup = gamePlan.getDungeonGroups().get(FIRST_GROUP);
    	String fileName = dungeonGroup.getDungeonInfo().get(dungeonName).getFileLocation();
    	Hero hero = (Hero) session.getAttribute("hero");

    	try {
    		DungeonRunner dungeonRunner = new DungeonRunner(hero, fileName);
    		dungeonRunner.setSpellbook(gamePlan.getSpellbook());
    		session.setAttribute("dungeonRunner", dungeonRunner);
    		return "redirect:/game";
    	} catch (IOException ex) {
    		throw new AssertionError(ex);
    	}
    }
    
    @RequestMapping(path = "/levelup", method = RequestMethod.GET)
    public String levelUp (HttpSession session, Model model) {
    	model.addAttribute("version", VERSION_STR);
    	String username = (String) session.getAttribute("username");
    	if (username == null) {
    		return "redirect:/login";
    	}
    	Hero hero = (Hero) session.getAttribute("hero");
    	model.addAttribute("username", username);
    	model.addAttribute("hero", hero);
    	return "levelup";
    }
    
    @RequestMapping(path = "/savegame", method = RequestMethod.POST)
    public String saveGame (HttpSession session) {
    	return "redirect:/airship";
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleError(HttpServletRequest req, Exception ex, HttpSession session) {
        ModelAndView mav = new ModelAndView();
        mav.addObject("exception", ex);
        mav.addObject("stackTrace", ex.getStackTrace());
        mav.addObject("version", VERSION_STR);
        String username = (String) session.getAttribute("username");
        mav.addObject("username", username);
        mav.setViewName("error");
        ex.printStackTrace();
        return mav; //Queen of Air and Darkness
    }
    
    private void initUserMap () {
    	userMap = new HashMap<>();
    	try (Scanner fileScanner = new Scanner(new File(USERS_FILE))) {
    		while (fileScanner.hasNextLine()) {
    			String[] split = fileScanner.nextLine().split(" ");
    			userMap.put(split[0], split[1]);
    		}
    	} catch (FileNotFoundException ex) {
    		throw new AssertionError("User data missing");
    	} catch (IndexOutOfBoundsException ex) {
    		throw new AssertionError("User data corrupt");
    	}
    }
    
    private void addNewUser (String username, String password) {
    	userMap.put(username, password);
    	try {
    		Files.write(Paths.get(USERS_FILE), ("\n" + username + " " + password).getBytes(), StandardOpenOption.APPEND);
    	} catch (FileNotFoundException ex) {
    		throw new AssertionError("User data missing");
    	} catch (IOException ex) {
    		throw new AssertionError(ex);
    	}
    }
    
    private static GamePlan buildGamePlan () {
    	try {
    		return GamePlan.buildFromFile(GAME_PLAN_LOCATION);
    	} catch (IOException ex) {
    		throw new AssertionError(ex);
    	}
    }
    
    /**
     * Helper method for airship endpoint.
     * @param dungeonGroup
     * @param heroClearedDungeons
     */
    private static void addDungeonInfoToModel (Model model, DungeonGroup dungeonGroup, List<String> heroClearedDungeons) {
    	List<String> clearedDungeons = new ArrayList<>();
    	List<String> availableDungeons = new ArrayList<>();
    	List<String> unavailableDungeons = new ArrayList<>();
		
    	Map<String, DungeonInfo> dungeonInfo = dungeonGroup.getDungeonInfo();
    	for (String name : dungeonInfo.keySet()) {
    		if (heroClearedDungeons.contains(name)) {
    			clearedDungeons.add(name + " - Cleared");
    		} else {
    			DungeonInfo info = dungeonInfo.get(name);
    			List<String> prereqs = info.getPrereqs();
    			if (heroClearedDungeons.containsAll(prereqs)) {
    				availableDungeons.add(name);
    			} else {
    				unavailableDungeons.add(UNAVAILBLE_STR);
    			}
    		}
    	}
    	
		model.addAttribute("clearedDungeons", clearedDungeons);
		model.addAttribute("availableDungeons", availableDungeons);
		model.addAttribute("unavailableDungeons", unavailableDungeons);
    }
    
    /**
     * Helper method for main game endpoint.
     * @param textOut
     */
    private static void addOutputTextToModel (Model model, TextInterface textOut) {
    	List<String> output = textOut.flush();
        List<String> debug = textOut.flushDebug();
        if (output.size() == 0) {
            debug.add("There was no content in the buffer");
        }
        List<String> tutorial = textOut.flushTutorial();
        model.addAttribute("outputText", output);
        

        if (tutorial != null && tutorial.size() > 0) {
            model.addAttribute("tutorial", tutorial);
        }

        if (debug != null && debug.size() > 0) {
            model.addAttribute("debugText", debug);
        }
    }
}
