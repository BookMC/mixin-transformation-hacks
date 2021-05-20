package org.bookmc.transformer.util;

import net.bytebuddy.agent.ByteBuddyAgent;
import net.minecraft.launchwrapper.Launch;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import sun.misc.Launcher;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.util.function.Consumer;

/**
 * Taken from https://github.com/Devan-Kerman/GrossFabricHacks/blob/master/src/main/java/net/devtech/grossfabrichacks/instrumentation/InstrumentationApi.java
 *
 * Licensed as MPL-2.0
 */

public class InstrumentationUtil {
    private static final Instrumentation instrumentation;

    /**
     * retransform the class represented by {@code cls} by {@code transformer}.
     *
     * @param cls         the class to retransform.
     * @param transformer the class transformer.
     */
    public static void retransform(Class<?> cls, Consumer<ClassNode> transformer) {
        try {
            ClassFileTransformer fileTransformer = (loader, className, classBeingRedefined, protectionDomain, classfileBuffer) -> {
                if (cls == classBeingRedefined) {
                    ClassNode node = new ClassNode();
                    new ClassReader(classfileBuffer).accept(node, ClassReader.EXPAND_FRAMES);
                    transformer.accept(node);
                    ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
                    node.accept(writer);
                    return writer.toByteArray();
                }

                return classfileBuffer;
            };

            instrumentation.addTransformer(fileTransformer, true);
            instrumentation.retransformClasses(cls);
            instrumentation.removeTransformer(fileTransformer);
        } catch (UnmodifiableClassException e) {
            throw new RuntimeException(e);
        }
    }

    static {
        try {
            final String name = ManagementFactory.getRuntimeMXBean().getName();
            final File jar = new File(System.getProperty("user.home"), "agent.jar");

            try (InputStream is = InstrumentationUtil.class.getClassLoader().getResourceAsStream("jars/agent.jar")) {
                try (FileOutputStream fos = new FileOutputStream(jar)) {
                    if (is != null) {
                        int read;
                        byte[] buffer = new byte[1024];

                        while ((read = is.read(buffer, 0, buffer.length)) != -1) {
                            fos.write(buffer, 0, read);
                        }
                    }
                }
            }
            ByteBuddyAgent.attach(jar, name.substring(0, name.indexOf('@')));

            final Field field = Class.forName("org.bookmc.agent.Agent", false, Launch.class.getClassLoader()).getDeclaredField("instrumentation");

            field.setAccessible(true);

            instrumentation = (Instrumentation) field.get(null);
        } catch (final Throwable throwable) {
            throwable.printStackTrace();
            throw new RuntimeException("An error occurred during an attempt to attach an instrumentation agent, which might be due to spaces in the path of the game's installation.", throwable);
        }
    }
}