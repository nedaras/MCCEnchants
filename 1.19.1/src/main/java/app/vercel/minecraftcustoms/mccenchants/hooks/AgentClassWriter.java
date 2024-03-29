package app.vercel.minecraftcustoms.mccenchants.hooks;

import org.objectweb.asm.ClassWriter;

public class AgentClassWriter extends ClassWriter {

    private final ClassLoader loader;

    public AgentClassWriter(int flags, ClassLoader loader) {
        super(flags);
        this.loader = loader;
    }

    @Override
    protected String getCommonSuperClass(String type1, String type2) {

        Class<?> a;
        Class<?> b;

        try {
            a = Class.forName(type1.replace('/', '.'), false, loader);
            b = Class.forName(type2.replace('/', '.'), false, loader);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        if (a.isAssignableFrom(b)) return type1;
        if (b.isAssignableFrom(a)) return type2;

        if (a.isInterface() || b.isInterface()) return "java/lang/Object";

        while (!a.isAssignableFrom(b)) {
            a = a.getSuperclass();
        }

        return a.getName().replace('.', '/');

    }

}
