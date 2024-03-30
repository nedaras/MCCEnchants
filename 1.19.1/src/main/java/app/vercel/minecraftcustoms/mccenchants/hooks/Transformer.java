package app.vercel.minecraftcustoms.mccenchants.hooks;

import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.PluginLogger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class Transformer implements ClassFileTransformer {

    @Override
    public byte[] transform(ClassLoader loader, String className, Class classBeingRedefined, ProtectionDomain protectionDomain, byte[] classFileBuffer) throws IllegalClassFormatException {
        if (classBeingRedefined == null) return classFileBuffer;
        if (!className.equals("org/bukkit/craftbukkit/v1_20_R3/inventory/CraftItemStack")) return classFileBuffer;

        InsnList instructions = new InsnList();

        try {

            ClassReader reader = new ClassReader("app.vercel.minecraftcustoms.mccenchants.hooks.Test");
            ClassNode node = new ClassNode(Opcodes.ASM5);

            reader.accept(node, 0);

            for (MethodNode methodNode : node.methods) {
                if (!methodNode.name.equals("addUnsafeEnchantment")) continue;
                for (AbstractInsnNode insnNode : methodNode.instructions) {
                    if (insnNode instanceof VarInsnNode a) { a.var--; } // we need to move argument by one
                }
                instructions.insert(methodNode.instructions);
                break;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {

            ClassReader reader = new ClassReader(classFileBuffer);
            ClassNode node = new ClassNode(Opcodes.ASM5);

            reader.accept(node, 0);

            for (MethodNode methodNode : node.methods)
            {
                if (!methodNode.name.equals("addUnsafeEnchantment")) continue;
                if (!methodNode.desc.equals("(Lorg/bukkit/enchantments/Enchantment;I)V")) continue;

                // HOW TO INSERT AAA
                methodNode.instructions.insert(instructions);

                break;
            }

            ClassWriter writer = new AgentClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES, loader);
            node.accept(writer);

            byte[] bytes = writer.toByteArray();


            try (FileOutputStream fos = new FileOutputStream("C:\\Users\\nedas\\Desktop\\paper\\plugins\\CraftItemStack.class")) {
                fos.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return bytes;

        } catch (Exception e) {
            System.out.println("oh my gad " + className);
            e.printStackTrace();
        }


        return classFileBuffer;

    }


}