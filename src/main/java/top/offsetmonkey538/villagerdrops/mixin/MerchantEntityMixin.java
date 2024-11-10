package top.offsetmonkey538.villagerdrops.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.InventoryOwner;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.village.TradeOffer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MerchantEntity.class)
public abstract class MerchantEntityMixin extends PassiveEntity implements InventoryOwner {
    protected MerchantEntityMixin(EntityType<? extends PassiveEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(
            method = "trade",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/passive/MerchantEntity;afterUsing(Lnet/minecraft/village/TradeOffer;)V"
            )
    )
    private void villagerdrops$addTradedItemsToInventory(TradeOffer offer, CallbackInfo ci) {
        villagerdrops$addTradeItemToInventory(offer.getDisplayedFirstBuyItem());
        villagerdrops$addTradeItemToInventory(offer.getDisplayedSecondBuyItem());
    }

    @Inject(
            method = "onDeath",
            at = @At("TAIL")
    )
    private void villagerdrops$dropInventoryOnDeath(DamageSource damageSource, CallbackInfo ci) {
        for (ItemStack item : getInventory().clearToList()) {
            item = item.copy();
            item.setCount(item.getCount() / 2);
            dropStack(item);
        }
    }

    @Unique
    private void villagerdrops$addTradeItemToInventory(final ItemStack item) {
        if (item.isEmpty()) return;
        if (!getInventory().canInsert(item)) return;

        getInventory().addStack(item);
    }
}
