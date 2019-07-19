package distributed.tasks.lifecycle;

import java.security.SecureRandom;

/**
 * Created by mwalsh on 8/18/14.
 */
public class Randomizer implements IRandomizer {
    @Override
    public int getRandomInt(int size) {
        return new SecureRandom().nextInt(size);
    }
}
