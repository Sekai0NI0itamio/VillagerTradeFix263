package itamio.villagertradefix.mixin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import org.spongepowered.asm.mixin.injection.At;

import itamio.villagertradefix.config.VillagerTradeFixConfig;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.MathHelper;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import net.minecraft.village.VillagerData;
import net.minecraft.world.World;

@Mixin(VillagerEntity.class)
public abstract class VillagerEntityMixin extends MerchantEntity {

    private List<TradeOfferList> offerList = new ArrayList<TradeOfferList>();
    private List<String> jobList = new ArrayList<String>();

    public VillagerEntityMixin(EntityType<? extends MerchantEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "readCustomData", at = @At("TAIL"))
    public void readCustomDataMixin(ReadView view, CallbackInfo info) {
        for (int i = 0; i < view.getInt("JobCount", 0); ++i) {
            String jobString = "OldOffer" + i;
            jobList.add(view.getString(jobString + "OldWork", ""));
            view.read(jobString, TradeOfferList.CODEC).ifPresent(offers -> {
                this.offerList.add(offers);
            });
        }
    }

    @Inject(method = "writeCustomData", at = @At("TAIL"))
    public void writeCustomDataMixin(WriteView view, CallbackInfo info) {
        for (int i = 0; i < this.jobList.size(); ++i) {
            String jobString = "OldOffer" + i;
            view.putString(jobString + "OldWork", this.jobList.get(i));
            view.put(jobString, TradeOfferList.CODEC, this.offerList.get(i));
        }
        view.putInt("JobCount", this.jobList.size());
    }

    @Redirect(method = "prepareOffersFor", at = @At(value = "INVOKE", target = "Lnet/minecraft/village/TradeOffer;increaseSpecialPrice(I)V", ordinal = 0))
    private void prepareOffersForRedirect(TradeOffer tradeOffer, int amount) {
        if (tradeOffer.getOriginalFirstBuyItem().getCount() * VillagerTradeFixConfig.get().maxReputationDiscount < MathHelper.abs(amount)) {
            tradeOffer.increaseSpecialPrice(-MathHelper.floor((float) VillagerTradeFixConfig.get().maxReputationDiscount * tradeOffer.getOriginalFirstBuyItem().getCount()));
        } else {
            tradeOffer.increaseSpecialPrice(amount);
        }
    }

    @Redirect(method = "prepareOffersFor", at = @At(value = "INVOKE", target = "Lnet/minecraft/village/TradeOffer;increaseSpecialPrice(I)V", ordinal = 1))
    private void prepareOffersForRedirect(TradeOffer tradeOffer, int amount, PlayerEntity playerEntity) {
        double d = VillagerTradeFixConfig.get().baseVillagerOfTheHeroDiscount
                + VillagerTradeFixConfig.get().extraVillagerOfTheHeroDiscount * playerEntity.getStatusEffect(StatusEffects.HERO_OF_THE_VILLAGE).getAmplifier();
        int k = (int) Math.floor(d * (double) tradeOffer.getOriginalFirstBuyItem().getCount());
        tradeOffer.increaseSpecialPrice(-Math.max(k, 0));
    }

    @Inject(method = "setVillagerData", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/entity/passive/VillagerEntity;getVillagerData()Lnet/minecraft/village/VillagerData;"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void setVillagerDataMixin(VillagerData villagerData, CallbackInfo info, VillagerData villagerData2) {
        if (!offerList.contains(this.getOffers())) {
            offerList.add(this.getOffers());
            jobList.add(villagerData2.profession().toString());
        }
    }

    @ModifyVariable(method = "prepareOffersFor", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/entity/passive/VillagerEntity;getReputation(Lnet/minecraft/entity/player/PlayerEntity;)I"), ordinal = 0)
    private int prepareOffersForReputationMixin(int i) {
        if (i > VillagerTradeFixConfig.get().maxReputation) {
            i = VillagerTradeFixConfig.get().maxReputation;
        }
        return i;
    }

    @Override
    public TradeOfferList getOffers() {
        if (this.offers == null) {
            if (this.jobList != null && this.jobList.contains(((VillagerEntity) (Object) this).getVillagerData().profession().toString())) {
                this.offers = this.offerList.get(this.jobList.indexOf(((VillagerEntity) (Object) this).getVillagerData().profession().toString()));
            } else {
                if (this.getEntityWorld() instanceof ServerWorld serverWorld) {
                    this.fillRecipes(serverWorld);
                } else {
                    this.offers = new TradeOfferList(Collections.emptyList());
                }
            }
        }
        return this.offers;
    }

}
