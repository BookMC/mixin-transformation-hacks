package org.bookmc.transformer.mixin.asm.lib;

import org.bookmc.transformer.util.InstrumentationUtil;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.ListIterator;

/**
 * A modifier directly to the ASM library.
 */
public class ClassReaderModifier {
    /**
     * This transformation removes the ASM exception thrown when trying to read newer versions.
     *
     * Do we know the implications of removing this? As of now no.
     */
    public static void run()  {
        try {
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
                                break;
                            }
                        }
                    }
                }
            });
        } catch (Exception ignored) {
            // Mixin might not always be available :)
        }
    }
}
