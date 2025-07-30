package io.github.offsetmonkey538.villager_drops.mixin;

import net.fabricmc.api.Environment;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradedItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(VillagerEntity.class)
public class VillagerEntityMixin {
    @Inject(method = {"afterUsing"}, at = {@At("TAIL")})
    protected void afterUsing(TradeOffer offer, CallbackInfo ci) {
        SimpleInventory inventory = ((VillagerEntity)(Object) this).getInventory();

        TradedItem firstBuyTradedItem = offer.getFirstBuyItem();
        Optional<TradedItem> secondBuyTradedItem = offer.getSecondBuyItem();

        ItemStack firstBuyItem = null;

        if (firstBuyItem != null) {
            firstBuyItem.setCount(firstBuyItem.getCount() / 2);

            if (inventory.canInsert(firstBuyItem)) {
                inventory.addStack(firstBuyItem);
            }
        }

        if (secondBuyTradedItem.isPresent()) {
            ItemStack secondBuyItem = secondBuyTradedItem.orElse(null).itemStack();

            secondBuyItem.setCount(secondBuyItem.getCount() / 2);

            if (inventory.canInsert(secondBuyItem)) {
                inventory.addStack(secondBuyItem);
            }
        }
    }

    @Inject(method = {"onDeath"}, at = {@At("TAIL")})
    public void onDeath(DamageSource damageSource, CallbackInfo ci) {
        VillagerEntity villager = (VillagerEntity) (Object) this;

        if (villager.getWorld().isClient())
            return;

        for (ItemStack stack : villager.getInventory().clearToList()) {
            villager.dropStack((ServerWorld) villager.getWorld(), stack);
        }
    }
}
