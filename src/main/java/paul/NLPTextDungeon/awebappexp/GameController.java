package paul.NLPTextDungeon.awebappexp;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Created by Paul Dennis on 8/16/2017.
 */
@Controller
public class GameController {

    BufferedOutputTextStream textOut = new BufferedOutputTextStream();

    @RequestMapping(path = "/", method = RequestMethod.GET)
    public String home () {
        return "index";
    }

    @RequestMapping(path = "/game", method = RequestMethod.GET)
    public String game (Model model) {
        List<String> lines  = textOut.flush();
        String output = "";
        for (String s : lines) {
            output += s + "\n";
        }
        if (lines.size() == 0) {
            output = "There was nothing in the buffer.";
        }
        model.addAttribute("outputText", output);
        return "game";
    }

    @RequestMapping(path = "/submit-action", method = RequestMethod.POST)
    public String submitAction (@RequestParam String userInput, Model model) {
        textOut.println("You entered:");
        textOut.println(userInput);
        List<String> lines  = textOut.flush();
        String output = "";
        for (String s : lines) {
            output += s + "\n";
        }
        if (lines.size() == 0) {
            output = "There was nothing in the buffer.";
        }
        model.addAttribute("outputText", output);
        return "game";
    }
}
