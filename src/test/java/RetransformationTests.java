import org.bookmc.transformer.util.InstrumentationUtil;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class RetransformationTests {

    @Test
    public void retransformationHacks() {
        InstrumentationUtil.retransform(TestHacks.class, (node) -> {
            for (MethodNode methodNode : node.methods) {
                if (methodNode.name.equals("test")) {
                    InsnList list = new InsnList();
                    list.add(new FieldInsnNode(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"));
                    list.add(new LdcInsnNode("Injected!"));
                    list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V"));
                    methodNode.instructions.insert(list);
                    break;
                }
            }
        });

        new TestHacks().test();
    }
}
