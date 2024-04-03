package app.vercel.minecraftcustoms.mccenchants.hooks;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;

public class Agent {

    private static ClassNode classNode;

    public static void premain(String agentArgs, Instrumentation instrumentation) {
        System.out.println("Premain executed...");
       instrumentation.addTransformer(new Transformer());
    }

    public static void agentmain(String agentArgs, Instrumentation instrumentation) {
        System.out.println("Agentmain executed...");

        Transformer transformer = new Transformer();

        try {
            load();
            instrumentation.addTransformer(transformer, true);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        for (Class<?> clazz : instrumentation.getAllLoadedClasses())
        {
            if (!(clazz.getName().equals("net.minecraft.world.item.ItemStack") || clazz.getName().equals("org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack"))) continue;
            if (!instrumentation.isModifiableClass(clazz)) {
                // TODO: add some idk report link and it would be nice to link a MCCEnchants logger
                System.out.println("Trying to modify not modifiable class: " + clazz.getName());
                continue;
            };

            try {
                instrumentation.retransformClasses(clazz);
            } catch (UnmodifiableClassException e) {
                e.printStackTrace();
                instrumentation.removeTransformer(transformer);
                throw new RuntimeException();
            }

        }

        instrumentation.removeTransformer(transformer);

    }


    private static void load() throws IOException {

        ClassReader reader = new ClassReader("app.vercel.minecraftcustoms.mccenchants.hooks.Test");
        ClassNode node = new ClassNode(Opcodes.ASM5);

        reader.accept(node, 0);

        for (MethodNode methodNode : node.methods) {
            convertInstructions(methodNode.instructions);
        }

        classNode = node;

    }


    private static void convertInstructions(InsnList instructionsList) {
        for (AbstractInsnNode instruction : instructionsList) {
            if (instruction instanceof VarInsnNode varInstruction) varInstruction.var--;
        }
    }
    public static @NotNull ClassNode getClassNode() {
        return classNode;
    }
}
