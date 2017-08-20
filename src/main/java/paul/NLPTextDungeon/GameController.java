package paul.NLPTextDungeon;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import paul.NLPTextDungeon.utils.InputType;
import paul.NLPTextDungeon.utils.TextInterface;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * Created by Paul Dennis on 8/16/2017.
 */
@Controller
public class GameController {

    InputType requestedInputType = InputType.STD;

    @RequestMapping(path = "/", method = RequestMethod.GET)
    public String home () {
        return "index";
    }

    @RequestMapping(path = "/game", method = RequestMethod.GET)
    public String game (Model model, HttpSession session) throws IOException {
        TextInterface textOut = (TextInterface) session.getAttribute("textInterface");
        if (textOut == null) {
            textOut = new TextInterface();
            session.setAttribute("textInterface", textOut);
        }
        System.out.println("Break");
        requestedInputType = textOut.show();//Important

        List<String> output = textOut.flush();
        List<String> debug = textOut.flushDebug();
        if (output.size() == 0) {
            debug.add("There was no content in the buffer");
        }
        List<String> tutorial = textOut.flushTutorial();
        model.addAttribute("tutorial", tutorial);
        model.addAttribute("debugText", debug);
        model.addAttribute("outputText", output);
        model.addAttribute("location", textOut.getRunner().getDungeon().getDungeonName());
        return "game";
    }

    @RequestMapping(path = "/submit-action", method = RequestMethod.POST)
    public String submitAction (@RequestParam String userInput, Model model, HttpSession session) {
        TextInterface textOut = (TextInterface) session.getAttribute("textInterface");

        switch (requestedInputType) {
            case STD:
                break;
            case NUMBER: //Number means integer for now
                try {
                    int value = Integer.parseInt(userInput);
                } catch (NumberFormatException ex) {
                    textOut.println("You were supposed to enter a whole number. Please try again.");
                    textOut.debug("You broke it by not entering a number.");
                }
        }
        textOut.println("You entered:");
        textOut.println(userInput);
        textOut.processResponse(userInput);
        //runner.analyzeAndExecuteStatement(userInput);
        return "redirect:/game";
    }
}
