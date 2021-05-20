package org.bookmc.transformer;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Iterator;

public class MixinTransformation implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        // Transforming mixin is a hobby :)

        if (name.equals("org.spongepowered.asm.service.mojang.MixinServiceLaunchWrapper")) {
            ClassNode node = new ClassNode();

            ClassReader reader = new ClassReader(basicClass);
            reader.accept(node, ClassReader.EXPAND_FRAMES);

            // Mixin transformations

            for (MethodNode methodNode : node.methods) {
                if (methodNode.name.equals("getClassBytes")) {
                    InsnList list = methodNode.instructions;
                    Iterator<AbstractInsnNode> iterator = list.iterator();

                    while (iterator.hasNext()) {
                        AbstractInsnNode insnNode = iterator.next();

                        if (insnNode instanceof TypeInsnNode && insnNode.getOpcode() == Opcodes.CHECKCAST) {
                            list.remove(insnNode);
                            break;
                        }
                    }
                }
            }

            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
            node.accept(writer);

            return writer.toByteArray();
        }

        return basicClass;
    }
}
