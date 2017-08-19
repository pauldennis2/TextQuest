package paul.NLPTextDungeon;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import paul.NLPTextDungeon.utils.TextInterface;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * Created by Paul Dennis on 8/16/2017.
 */
@Controller
public class GameController {

    @RequestMapping(path = "/", method = RequestMethod.GET)
    public String home () {
        return "index";
    }

    @RequestMapping(path = "/game", method = RequestMethod.GET)
    public String game (Model model, HttpSession session) throws IOException {
        TextInterface textOut = (TextInterface) session.getAttribute("textInterface");
        DungeonRunner runner = null;

        runner.describeRoom();//Fix
        List<String> output = textOut.flush();
        List<String> debug = textOut.flushDebug();
        if (output.size() == 0) {
            debug.add("There was no content in the buffer");
        }
        List<String> tutorial = textOut.flushTutorial();
        model.addAttribute("tutorial", tutorial);
        model.addAttribute("debugText", debug);
        model.addAttribute("outputText", output);
        String location = runner.getDungeon().getDungeonName();
        model.addAttribute("location", location);
        return "game";
    }

    @RequestMapping(path = "/submit-action", method = RequestMethod.POST)
    public String submitAction (@RequestParam String userInput, Model model, HttpSession session) {
        DungeonRunner runner = (DungeonRunner) session.getAttribute("dungeonRunner");
        TextInterface textOut = runner.getTextOut();
        textOut.println("You entered:");
        textOut.println(userInput);
        runner.analyzeAndExecuteStatement(userInput);
        return "redirect:/game";
    }
}
