package app.vercel.minecraftcustoms.mccenchants.hooks;

import java.lang.instrument.Instrumentation;

public class Agent {

    public static void agentmain(String agentArgs, Instrumentation instrumentation) {

        for (Class<?> clazz : instrumentation.getAllLoadedClasses()) {
            if (clazz.getName().startsWith("app.vercel")) System.out.println(clazz.getName());
        }
    }
}
