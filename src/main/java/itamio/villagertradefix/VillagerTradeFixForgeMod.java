package itamio.villagertradefix;

import itamio.villagertradefix.config.VillagerTradeFixConfig;
import net.minecraftforge.fml.common.Mod;

/**
 * Unofficial full Forge port of VillagerTradeFix by Globox1997 (MIT)
 * to Minecraft 1.20.1. See NOTICE/README for credit.
 * The full original logic is restored on this branch: the
 * updateSpecialPrices redirects (prepareOffersFor), the reputation
 * cap, and the per-job NBT trade memory.
 */
@Mod("villagertradefix")
public class VillagerTradeFixForgeMod {
    public VillagerTradeFixForgeMod() {
        VillagerTradeFixConfig.load();
    }
}
