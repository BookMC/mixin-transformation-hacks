package org.bookmc.transformer;

import org.bookmc.transformer.mixin.asm.lib.ClassReaderModifier;
import org.bookmc.transformer.mixin.asm.service.mojang.MixinServiceLaunchWrapperModifier;

public class TransformationHacks {
    public static void hack() {
        try {
            ClassReaderModifier.run();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        MixinServiceLaunchWrapperModifier.run();
    }
}
