package itamio.villagertradefix.mixin;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import itamio.villagertradefix.config.VillagerTradeFixConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;

/**
 * Full port of the original VillagerEntityMixin (Fabric/Yarn) to
 * Minecraft 1.20.1 (Forge, Mojang official mappings).
 *
 * Mapping notes:
 * - Yarn VillagerEntity -> Mojang Villager (net.minecraft.world.entity.npc.Villager)
 * - Yarn MerchantEntity -> Mojang AbstractVillager (offers field owner)
 * - Yarn TradeOffer/TradeOfferList -> Mojang MerchantOffer/MerchantOffers
 * - Yarn TradeOffer.getOriginalFirstBuyItem() -> getBaseCostA()
 * - Yarn TradeOffer.increaseSpecialPrice(int) -> addToSpecialPriceDiff(int)
 * - Yarn prepareOffersFor(PlayerEntity) -> updateSpecialPrices(Player)
 * - Yarn getReputation(PlayerEntity) -> getPlayerReputation(Player)
 * - Yarn readCustomDataFromNbt/writeCustomDataToNbt(NbtCompound)
 *     -> readAdditionalSaveData/addAdditionalSaveData(CompoundTag)
 * - Yarn TradeOfferList.CODEC + registry ops -> MerchantOffers(CompoundTag)
 *     / MerchantOffers.createTag() (the 1.20.1 "Recipes" NBT format)
 * - VillagerProfession is a record in 1.20.1; its record toString()
 *     embeds predicate hashes, so the stable name() component is used
 *     as the per-job memory key (persists correctly across restarts).
 */
@Mixin(Villager.class)
public abstract class VillagerEntityMixin extends AbstractVillager {

    private List<MerchantOffers> villagertradefix$offerList = new ArrayList<MerchantOffers>();
    private List<String> villagertradefix$jobList = new ArrayList<String>();
    private boolean villagertradefix$professionChanged = false;

    public VillagerEntityMixin(EntityType<? extends AbstractVillager> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    public void readAdditionalSaveDataMixin(CompoundTag nbt, CallbackInfo info) {
        for (int i = 0; i < nbt.getInt("JobCount"); ++i) {
            String jobString = "OldOffer" + i;
            villagertradefix$jobList.add(nbt.getString(jobString + "OldWork"));
            if (nbt.contains(jobString, 10)) {
                villagertradefix$offerList.add(new MerchantOffers(nbt.getCompound(jobString)));
            }
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    public void addAdditionalSaveDataMixin(CompoundTag nbt, CallbackInfo info) {
        for (int i = 0; i < villagertradefix$jobList.size(); ++i) {
            String jobString = "OldOffer" + i;
            nbt.putString(jobString + "OldWork", villagertradefix$jobList.get(i));
            nbt.put(jobString, villagertradefix$offerList.get(i).createTag());
        }
        nbt.putInt("JobCount", villagertradefix$jobList.size());
    }

    @Redirect(method = "updateSpecialPrices", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/trading/MerchantOffer;addToSpecialPriceDiff(I)V", ordinal = 0))
    private void updateSpecialPricesRedirect(MerchantOffer offer, int amount) {
        if (offer.getBaseCostA().getCount() * VillagerTradeFixConfig.get().maxReputationDiscount < Math.abs(amount)) {
            offer.addToSpecialPriceDiff(-Mth.floor((float) VillagerTradeFixConfig.get().maxReputationDiscount * offer.getBaseCostA().getCount()));
        } else {
            offer.addToSpecialPriceDiff(amount);
        }
    }

    @Redirect(method = "updateSpecialPrices", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/trading/MerchantOffer;addToSpecialPriceDiff(I)V", ordinal = 1))
    private void updateSpecialPricesRedirect(MerchantOffer offer, int amount, Player player) {
        double d = VillagerTradeFixConfig.get().baseVillagerOfTheHeroDiscount
                + VillagerTradeFixConfig.get().extraVillagerOfTheHeroDiscount * player.getEffect(MobEffects.HERO_OF_THE_VILLAGE).getAmplifier();
        int k = (int) Math.floor(d * (double) offer.getBaseCostA().getCount());
        offer.addToSpecialPriceDiff(-Math.max(k, 0));
    }

    @Inject(method = "setVillagerData", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/entity/npc/Villager;getVillagerData()Lnet/minecraft/world/entity/npc/VillagerData;"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void setVillagerDataMixin(VillagerData villagerData, CallbackInfo info, VillagerData villagerData2) {
        if (!villagertradefix$offerList.contains(this.getOffers())) {
            villagertradefix$offerList.add(this.getOffers());
            villagertradefix$jobList.add(villagerData2.getProfession().name());
        }
        villagertradefix$professionChanged = villagerData2.getProfession() != villagerData.getProfession();
    }

    @ModifyVariable(method = "updateSpecialPrices", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/entity/npc/Villager;getPlayerReputation(Lnet/minecraft/world/entity/player/Player;)I"), ordinal = 0)
    private int updateSpecialPricesReputationMixin(int i) {
        if (i > VillagerTradeFixConfig.get().maxReputation) {
            i = VillagerTradeFixConfig.get().maxReputation;
        }
        return i;
    }

    @Override
    public MerchantOffers getOffers() {
        String profession = ((Villager) (Object) this).getVillagerData().getProfession().name();
        if (villagertradefix$jobList != null && villagertradefix$jobList.contains(profession)) {
            MerchantOffers memorized = villagertradefix$offerList.get(villagertradefix$jobList.indexOf(profession));
            if (this.offers != memorized && villagertradefix$professionChanged) {
                this.offers = memorized;
                villagertradefix$professionChanged = false;
            }
        }
        if (this.offers == null) {
            if (villagertradefix$jobList != null && villagertradefix$jobList.contains(profession)) {
                this.offers = villagertradefix$offerList.get(villagertradefix$jobList.indexOf(profession));
            } else {
                this.offers = new MerchantOffers();
                this.updateTrades();
            }
        }
        return this.offers;
    }
}
