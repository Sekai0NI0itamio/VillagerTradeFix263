package itamio.villagertradefix;

import itamio.villagertradefix.config.VillagerTradeFixConfig;
import net.fabricmc.api.ModInitializer;

/**
 * Unofficial full port of VillagerTradeFix by Globox1997 (MIT) to
 * Minecraft 1.21.11 (Fabric). See NOTICE/README for credit.
 * On this version the original trade-memory logic works in full:
 * prepareOffersFor and the NBT trade memory are intact.
 */
public class VillagerTradeFixMod implements ModInitializer {
    @Override
    public void onInitialize() {
        VillagerTradeFixConfig.load();
    }
}
