package paul.NLPTextDungeon.interfaces;

import paul.NLPTextDungeon.utils.InputType;

/**
 * Created by pauldennis on 8/19/17.
 */
public interface UserInterface extends TextOuter {

    void start ();
    InputType show ();
    InputType processResponse (String response);

}
