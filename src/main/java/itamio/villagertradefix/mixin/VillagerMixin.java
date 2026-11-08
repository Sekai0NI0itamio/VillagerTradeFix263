package itamio.villagertradefix.mixin;

import net.minecraft.world.entity.npc.villager.Villager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Partial port of Globox1997's VillagerEntityMixin: cap the
 * reputation discount at 25 (original maxReputation). The original's
 * trade-memory system targeted prepareOffersFor, which was
 * replaced by updateTrades in 26.3 and is not ported.
 */
@Mixin(Villager.class)
public class VillagerMixin {
    @ModifyVariable(method = "getPlayerReputation", at = @At("HEAD"), argsOnly = true)
    private int villagertradefix263$capReputation(int original) {
        return Math.min(original, 25);
    }
}
