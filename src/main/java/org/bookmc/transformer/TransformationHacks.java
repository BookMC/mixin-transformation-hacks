package org.bookmc.transformer;

import org.bookmc.transformer.mixin.asm.service.mojang.MixinServiceLaunchWrapperModifier;

public class TransformationHacks {
    public static void hack() {
        MixinServiceLaunchWrapperModifier.run();
    }
}
