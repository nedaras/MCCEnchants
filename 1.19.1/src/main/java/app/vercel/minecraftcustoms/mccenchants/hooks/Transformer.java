package app.vercel.minecraftcustoms.mccenchants.hooks;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.Map;

public class Transformer implements ClassFileTransformer {

    @Override
    public byte[] transform(ClassLoader loader, String className, Class classBeingRedefined, ProtectionDomain protectionDomain, byte[] classFileBuffer) throws IllegalClassFormatException {
        if (classBeingRedefined == null) return classFileBuffer;

        Map<String, InsnList> instructions = new HashMap<>();
        ClassNode classNode = Agent.getClassNode();

        for (MethodNode method : classNode.methods) {
            convertInstructions(method.instructions);

            if (method.name.equals("addTagElement")) instructions.put("(Ljava/lang/String;Lnet/minecraft/nbt/NBTBase;)V", method.instructions);
            if (method.name.equals("enchant")) instructions.put("(Lnet/minecraft/world/item/enchantment/Enchantment;I)V", method.instructions);
        }

        try {

            ClassReader reader = new ClassReader(classFileBuffer);
            ClassNode node = new ClassNode(Opcodes.ASM4);

            reader.accept(node, 0);

            for (MethodNode method : node.methods) {
                if (!method.name.equals("a")) continue;
                if (!instructions.containsKey(method.desc)) continue;

                System.out.println("inserted too: " + method.desc);

                method.instructions.insert(getInsertion(method.instructions), instructions.get(method.desc));
            }

            ClassWriter writer = new AgentClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES, loader);
            node.accept(writer);

            return writer.toByteArray();
        } catch (Exception e) {
           e.printStackTrace();
        }

        return classFileBuffer;

    }

    private static void convertInstructions(InsnList instructionsList) {
        for (AbstractInsnNode instruction : instructionsList) {
            if (instruction instanceof VarInsnNode varInstruction) varInstruction.var--;
        }
    }

    private static AbstractInsnNode getInsertion(InsnList instructionList) {
        AbstractInsnNode instruction = instructionList.getLast();

        while (instruction.getPrevious() != null) {
            if (instruction.getOpcode() == Opcodes.RETURN) {
                instruction = instruction.getPrevious();
                break;
            }
            instruction = instruction.getPrevious();
        }
        return instruction;
    }
}