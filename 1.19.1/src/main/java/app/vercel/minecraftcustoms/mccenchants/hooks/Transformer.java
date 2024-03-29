package app.vercel.minecraftcustoms.mccenchants.hooks;

import org.checkerframework.checker.units.qual.C;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class Transformer implements ClassFileTransformer {

    @Override
    public byte[] transform(ClassLoader loader, String className, Class classBeingRedefined, ProtectionDomain protectionDomain, byte[] classFileBuffer) throws IllegalClassFormatException {
        if (!className.equals("org/bukkit/craftbukkit/v1_20_R3/CraftServer")) return classFileBuffer;
        if (classBeingRedefined == null) return classFileBuffer;

        try {

            ClassReader reader = new ClassReader(classFileBuffer);
            ClassNode node = new ClassNode(Opcodes.ASM5);

            reader.accept(node, 0);

            for (MethodNode methodNode : node.methods)
            {
                if (!methodNode.name.equals("getVersion")) continue;

                InsnList instructions = methodNode.instructions;

                instructions.clear();
                instructions.add(new LdcInsnNode("Hello World!"));
                instructions.add(new InsnNode(Opcodes.ARETURN));
                break;
            }

            ClassWriter writer = new AgentClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES, loader);
            node.accept(writer);

            return writer.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("oh my gad " + className);

        return classFileBuffer;

    }


}
