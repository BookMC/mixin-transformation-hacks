package org.bookmc.transformer;

import org.bookmc.transformer.mixin.asm.lib.ClassReaderModifier;
import org.bookmc.transformer.mixin.asm.service.mojang.MixinServiceLaunchWrapperModifier;

public class TransformationHacks {
    public static void hack() {
        ClassReaderModifier.run();
        MixinServiceLaunchWrapperModifier.run();
    }
}
