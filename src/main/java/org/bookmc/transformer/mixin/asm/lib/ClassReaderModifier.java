package org.bookmc.transformer.mixin.asm.lib;

import org.bookmc.transformer.util.InstrumentationUtil;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.ListIterator;

public class ClassReaderModifier {
    public static void run() throws ClassNotFoundException {
        InstrumentationUtil.retransform(Class.forName("org.spongepowered.asm.lib.ClassReader"), (node) -> {
            for (MethodNode methodNode : node.methods) {
                InsnList list = methodNode.instructions;

                ListIterator<AbstractInsnNode> nodeIterator = list.iterator();

                while (nodeIterator.hasNext()) {
                    AbstractInsnNode insnNode = nodeIterator.next();


                    if (insnNode instanceof TypeInsnNode && insnNode.getOpcode() == Opcodes.NEW) {
                        if (((TypeInsnNode) insnNode).desc.equals("java/lang/IllegalArgumentException")) {
                            AbstractInsnNode dup = insnNode.getNext();
                            AbstractInsnNode invokeSpecial = dup.getNext();
                            AbstractInsnNode aThrow = invokeSpecial.getNext();

                            list.remove(insnNode);
                            list.remove(dup);
                            list.remove(invokeSpecial);
                            list.remove(aThrow);
                        }
                    }
                }
            }
        });
    }
}
