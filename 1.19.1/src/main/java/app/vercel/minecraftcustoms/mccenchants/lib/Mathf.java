package app.vercel.minecraftcustoms.mccenchants.lib;

import java.util.concurrent.ThreadLocalRandom;

public final class Mathf {

    public static int randomInt(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);

    }

}
