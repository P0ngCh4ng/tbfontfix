package io.github.p0ngch4ng.tbfontfix.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * Fixes the HUD overlay (tanks and tool slots shown while the backpack is worn).
 * This screen is never initialised, so its textRenderer field is always null.
 *
 * <p>Traveler's Backpack passes this font to {@code ItemRenderer#renderGuiItemOverlay}.
 * Vanilla only reads the font when the stack count is above one, but mods such as
 * Building Wands read it unconditionally and crash with a NullPointerException.
 * When the font is null, the client's default text renderer is passed instead.
 */
@Pseudo
@Mixin(targets = "com.tiviacz.travelersbackpack.client.screen.OverlayHandledScreen")
public abstract class OverlayHandledScreenMixin {
    @ModifyArg(
            method = "drawItemStack",
            at = @At(
                    value = "INVOKE",
                    remap = true,
                    target = "Lnet/minecraft/client/render/item/ItemRenderer;renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;II)V"
            ),
            index = 0,
            remap = false
    )
    private TextRenderer tbfontfix$nonNullFont(TextRenderer font) {
        return font != null ? font : MinecraftClient.getInstance().textRenderer;
    }
}
