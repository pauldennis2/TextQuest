package paul.TextQuest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import paul.TextQuest.entities.Dungeon;
import paul.TextQuest.entities.DungeonGroup;
import paul.TextQuest.entities.DungeonInfo;
import paul.TextQuest.entities.Hero;
import paul.TextQuest.parsing.InputType;
import paul.TextQuest.parsing.TextInterface;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by Paul Dennis on 8/16/2017.
 */
@Controller
public class GameController {

    private InputType requestedInputType = InputType.STD;
    
    private Map<String, String> userMap;
    
    public static final String USERS_FILE = "save_data/users.txt";
    
    public static final List<String> DUNGEON_NAMES = Arrays.asList("Lair of Pihop-pi", "Darklight Dungeon", "The Third Dungeon");

    @RequestMapping(path = "/", method = RequestMethod.GET)
    public String home () {
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
    	String username = (String) session.getAttribute("username");
    	if (username == null) {
    		return "redirect:/login";
    	}
        TextInterface textOut = (TextInterface) session.getAttribute("textInterface");
        if (textOut == null) {
        	Hero hero = (Hero) session.getAttribute("hero");
            textOut = TextInterface.getInstance(hero);
            textOut.start(null);
            session.setAttribute("textInterface", textOut);
        }
        requestedInputType = textOut.show();//Important //<--Useful //<--Sarcastic

        List<String> output = textOut.flush();
        List<String> debug = textOut.flushDebug();
        if (output.size() == 0) {
            debug.add("There was no content in the buffer");
        }
        List<String> tutorial = textOut.flushTutorial();
        if (tutorial.size() > 0 && tutorial.get(0) == null) {
            tutorial = null;
        }

        model.addAttribute("outputText", output);
        model.addAttribute("username", session.getAttribute("username"));

        if (tutorial != null && tutorial.size() > 0) {
            model.addAttribute("tutorial", tutorial);
        }

        if (debug != null && debug.size() > 0) {
            model.addAttribute("debugText", debug);
        }
        model.addAttribute("location", textOut.getRunner().getDungeon().getDungeonName());
        model.addAttribute("roomName", "  " + textOut.getRunner().getHero().getLocation().getName());
        System.err.println(textOut.getRunner().getHero().getLocation());
        return "game";
    }
    
    @RequestMapping(path = "/airship", method = RequestMethod.GET)
    public String airship (Model model, HttpSession session) {
    	TextInterface textOut = (TextInterface) session.getAttribute("textInterface");
        if (textOut == null) {
        	Hero hero = (Hero) session.getAttribute("hero");
            textOut = TextInterface.getInstance(hero);
            textOut.start(null);
            session.setAttribute("textInterface", textOut);
            DungeonGroup dungeonGroup = buildDungeonGroup();
            session.setAttribute("dungeonGroup", dungeonGroup);
        }
		
		String username = (String) session.getAttribute("username");
    	Hero hero = textOut.getRunner().getHero();
    	model.addAttribute("username", username);
    	model.addAttribute("hero", hero);
    	
    	
    	List<String> clearedDungeons = new ArrayList<>();
    	List<String> availableDungeons = new ArrayList<>();
    	List<String> unavailableDungeons = new ArrayList<>();
		
    	DungeonGroup dungeonGroup = (DungeonGroup) session.getAttribute("dungeonGroup");
    	Map<String, DungeonInfo> dungeonInfo = dungeonGroup.getDungeonInfo();
    	List<String> heroClearedDungeons = hero.getClearedDungeons();
    	for (String name : dungeonInfo.keySet()) {
    		if (heroClearedDungeons.contains(name)) {
    			clearedDungeons.add(name + " - Cleared");
    		} else {
    			DungeonInfo info = dungeonInfo.get(name);
    			List<String> prereqs = info.getPrereqs();
    			if (heroClearedDungeons.containsAll(prereqs)) {
    				availableDungeons.add(name);
    			} else {
    				unavailableDungeons.add("?????");
    			}
    		}
    	}
    	
		model.addAttribute("clearedDungeons", clearedDungeons);
		model.addAttribute("availableDungeons", availableDungeons);
		model.addAttribute("unavailableDungeons", unavailableDungeons);
    	
    	return "airship";
    }

    @RequestMapping(path = "/submit-action", method = RequestMethod.POST)
    public String submitAction (@RequestParam String userInput, Model model, HttpSession session) {
        TextInterface textOut = (TextInterface) session.getAttribute("textInterface");
        if (requestedInputType == InputType.NUMBER) {
            try {
                Integer.parseInt(userInput);
            } catch (NumberFormatException ex) {
                textOut.debug("You broke it by not entering a number. Thanks");
            }
        }
        if (!userInput.equals("")) {
            textOut.debug("You entered: \"" + userInput + "\"");
        }
        if (userInput.equals("leave")) {
        	textOut.debug("~Leave command detected.");
        	Dungeon dungeon = textOut.getRunner().getDungeon();
        	System.err.println(dungeon);
        	System.err.println("dungeon.isCleared() = " + dungeon.isCleared());
        	if (textOut.getRunner().getDungeon().isCleared()) {
        		textOut.debug("~And dungeon is cleared.");
        		Hero hero = textOut.getRunner().getHero();
        		String username = (String) session.getAttribute("username");
        		Hero.saveHeroToFile(username, hero);
        		return "airship";
        	} else {
        		textOut.debug("~But dungeon isn't cleared.");
        	}
        }
        
        textOut.processResponse(userInput);
        /* Old impl 9/14
        try {
            textOut.processResponse(userInput);
        } catch (DefeatException ex) {
            textOut.println(ex.getMessage());
            textOut.println("You died. GAME OVER.");
        } catch (VictoryException ex) {
            textOut.println("You won! Awesome!");
            String username = (String) session.getAttribute("username");
            textOut.println("Saving hero data for user " + username);
            textOut.println(ex.getMessage());
            
            Dungeon dungeon = textOut.getRunner().getDungeon();
            Hero hero = textOut.getRunner().getHero();
            dungeon.setCleared(true);
            hero.addClearedDungeon(dungeon.getDungeonName());
            Hero.saveHeroToFile(username, hero);
            requestedInputType = InputType.NONE;
        }
        */
         
        return "redirect:/game";
    }
    
    @RequestMapping(path = "/load-hero", method = RequestMethod.GET)
    public String loadHeroView (HttpSession session, Model model) {
    	String username = (String) session.getAttribute("username");
    	List<String> heroList = Hero.getHeroListForUser(username);
    	model.addAttribute("heroList", heroList);
    	return null;
    }
    
    @RequestMapping(path = "/load-hero", method = RequestMethod.POST)
    public String receiveLoadHero (HttpSession session, Model model, String heroName) {
    	System.out.println("Loading hero: " + heroName);
    	String username = (String) session.getAttribute("username");
    	Hero hero = Hero.loadHeroFromFile(username, heroName);
    	if (hero == null) {
    		hero = new Hero(heroName);
    	}
    	session.setAttribute("hero", hero);
    	return "redirect:/airship";
    }
    
    @RequestMapping(path = "/startDungeon", method = RequestMethod.GET)
    public String startDungeon (@RequestParam String dungeonName, HttpSession session) {
    	System.out.println("Attempting to start dungeon with name: " + dungeonName);
    	TextInterface textOut = (TextInterface) session.getAttribute("textInterface");
    	
    	DungeonGroup dungeonGroup = (DungeonGroup) session.getAttribute("dungeonGroup");
    	Map<String, DungeonInfo> dungeonInfo = dungeonGroup.getDungeonInfo();
    	String fileName = dungeonInfo.get(dungeonName).getFileLocation();
    	try {
    		textOut.newDungeon(fileName);
    		return "redirect:/game";
    	} catch (IOException ex) {
    		throw new AssertionError(ex);
    	}
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleError(HttpServletRequest req, Exception ex) {
        ModelAndView mav = new ModelAndView();
        mav.addObject("exception", ex);
        mav.addObject("stackTrace", ex.getStackTrace());
        mav.setViewName("error");
        ex.printStackTrace();
        return mav; //Queen of Air and Darkness
    }
    
    private void initUserMap () {
    	userMap = new HashMap<>();
    	try (Scanner fileScanner = new Scanner(new File(USERS_FILE))) {
    		while (fileScanner.hasNextLine()) {
    			String line = fileScanner.nextLine();
    			String[] split = line.split(" ");
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
    
    public static final String DUNGEON_GROUP_LOCATION = "content_files/test_dungeon_group.json";
    private static DungeonGroup buildDungeonGroup () {
    	try {
    		return DungeonGroup.buildGroupFromFile(DUNGEON_GROUP_LOCATION);
    	} catch (IOException ex) {
    		throw new AssertionError(ex);
    	}
    }
}
