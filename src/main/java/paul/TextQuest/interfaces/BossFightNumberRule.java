package paul.NLPTextDungeon.interfaces;

import java.util.List;

/**
 * Created by Paul Dennis on 8/12/2017.
 */
public interface BossFightNumberRule {

    boolean checkRule (List<Integer> inputs, int solution);

    int getSolution (List<Integer> inputs);
}
