package app.vercel.minecraftcustoms.mccenchants.hooks;

import app.vercel.minecraftcustoms.mccenchants.Main;
import com.sun.tools.attach.VirtualMachine;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;

import java.io.File;
import java.lang.management.ManagementFactory;

public class Hooks {

    public static void init()
    {

        try {

            String pid = ManagementFactory.getRuntimeMXBean().getName();
            pid = pid.substring(0, pid.indexOf("@"));

            VirtualMachine  vm = VirtualMachine.attach(pid);
            File thiz = new File(Hooks.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
            vm.loadAgent(thiz.getAbsolutePath());
            vm.detach();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
