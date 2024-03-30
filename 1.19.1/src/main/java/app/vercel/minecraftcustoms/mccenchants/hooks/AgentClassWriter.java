package app.vercel.minecraftcustoms.mccenchants.hooks;

import org.objectweb.asm.ClassWriter;

public class AgentClassWriter extends ClassWriter {

    private final ClassLoader loader;

    public AgentClassWriter(int flags, ClassLoader loader) {
        super(flags);
        this.loader = loader;
    }

    @Override
    protected ClassLoader getClassLoader() {
        return this.loader;
    }

}
