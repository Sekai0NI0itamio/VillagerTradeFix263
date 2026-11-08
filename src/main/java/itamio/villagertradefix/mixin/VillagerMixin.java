package itamio.villagertradefix.mixin;

import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Partial port of Globox1997's VillagerEntityMixin: cap the
 * reputation discount at 25 (original maxReputation). The original's
 * trade-memory system targeted prepareOffersFor, which was
 * replaced by updateTrades in 26.3 and is not ported.
 */
@Mixin(Villager.class)
public class VillagerMixin {
    @Inject(method = "getPlayerReputation", at = @At("RETURN"), cancellable = true)
    private void villagertradefix263$capReputation(Player player, CallbackInfoReturnable<Integer> cir) {
        if (cir.getReturnValue() > 25) {
            cir.setReturnValue(25);
        }
    }
}
