package org.bookmc.transformer.mixin.asm.service.mojang;

import org.bookmc.transformer.util.InstrumentationUtil;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Iterator;

public class MixinServiceLaunchWrapperModifier {
    public static void run() {
        try {
            InstrumentationUtil.retransform(Class.forName("org.spongepowered.asm.service.mojang.MixinServiceLaunchWrapper"), (node) -> {
                for (MethodNode methodNode : node.methods) {
                    if (methodNode.name.equals("getClassBytes")) {
                        InsnList list = methodNode.instructions;
                        Iterator<AbstractInsnNode> iterator = list.iterator();

                        while (iterator.hasNext()) {
                            AbstractInsnNode insnNode = iterator.next();

                            if (insnNode instanceof TypeInsnNode && insnNode.getOpcode() == Opcodes.CHECKCAST && ((TypeInsnNode) insnNode).desc.equals("java/net/URLClassLoader")) {
                                list.remove(insnNode);
                            }

                            if (insnNode instanceof MethodInsnNode && insnNode.getOpcode() == Opcodes.INVOKEVIRTUAL && ((MethodInsnNode) insnNode).owner.equals("java/net/URLClassLoader")) {
                                ((MethodInsnNode) insnNode).owner = "java/lang/ClassLoader";
                                break;
                            }
                        }

                        for (LocalVariableNode variable : methodNode.localVariables) {
                            if (variable.desc.equals("Ljava/net/URLClassLoader;")) {
                                variable.desc = "Ljava/lang/ClassLoader;";
                                break;
                            }
                        }
                    }
                }
            });
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
