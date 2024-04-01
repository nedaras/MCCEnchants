package app.vercel.minecraftcustoms.mccenchants.hooks;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;

public class Agent {

    public static void premain(String agentArgs, Instrumentation instrumentation) {
        System.out.println("Premain executed...");
       instrumentation.addTransformer(new Transformer());
    }

    public static void agentmain(String agentArgs, Instrumentation instrumentation) {
        System.out.println("Agentmain executed...");
        instrumentation.addTransformer(new Transformer(), true);

        for (Class<?> clazz : instrumentation.getAllLoadedClasses())
        {
            if (!clazz.getName().equals("net.minecraft.world.item.ItemStack")) continue;
            if (!instrumentation.isModifiableClass(clazz)) {
                // TODO: add some idk report link and it would be nice to link a MCCEnchants logger
                System.out.println("Trying to modify not modifiable class: " + clazz.getName());
                continue;
            };

            System.out.println(clazz.getClassLoader());

            try {
                instrumentation.retransformClasses(clazz);
            } catch (UnmodifiableClassException e) {
                e.printStackTrace();
                throw new RuntimeException();
            }

            System.out.println("Dont forget to break here and remove modifier");

            break;

        }
    }
}
