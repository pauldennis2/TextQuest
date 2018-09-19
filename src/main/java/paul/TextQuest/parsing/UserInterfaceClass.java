package paul.TextQuest.parsing;

import java.util.List;

import paul.TextQuest.entities.TickTock;

/**
 * Created by pauldennis on 8/20/17.
 */
public abstract class UserInterfaceClass extends TickTock {

    protected List<UserInterfaceClass> children;
    protected UserInterfaceClass requester;
    protected UserInterfaceClass defaultRequester;
    //Using a default requester means the class foregoes the option to handle inputs itself

    protected TextInterface textOut;

    public abstract void start (TextInterface textOut);

    public abstract InputType show ();

    public final InputType processResponse (String response) {
        if (defaultRequester != null && requester == null) {
            requester = defaultRequester;
        }
        if (requester != null) {
            return requester.processResponse(response);
        } else {
            return handleResponse(response);
        }
    }

    protected InputType handleResponse (String response) {
    	System.out.println("===");
    	System.out.println(this);
    	System.out.println("===");
        throw new AssertionError("This class doesn't handle responses. Method called in error");
    }
}
