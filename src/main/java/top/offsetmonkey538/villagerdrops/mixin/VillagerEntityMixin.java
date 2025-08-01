package top.offsetmonkey538.villagerdrops.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.village.TradeOffer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VillagerEntity.class)
public abstract class VillagerEntityMixin extends MerchantEntity {
    public VillagerEntityMixin(EntityType<? extends MerchantEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(
            method = "onDeath",
            at = @At("TAIL")
    )
    private void villagerdrops$dropInventoryStacksOnDeath(DamageSource damageSource, CallbackInfo ci) {
        if (!(this.getWorld() instanceof final ServerWorld world)) return;

        this.getInventory().clearToList().forEach(stack -> this.dropStack(world, stack));
    }

    @Inject(
            method = "afterUsing",
            at = @At("TAIL")
    )
    private void villagerdrops$addHalfTradeItemsToInventory(TradeOffer offer, CallbackInfo ci) {
        villagerdrops$handleAddingItemStackToInventory(offer.getDisplayedFirstBuyItem());
        villagerdrops$handleAddingItemStackToInventory(offer.getDisplayedSecondBuyItem());
    }

    @Unique
    private void villagerdrops$handleAddingItemStackToInventory(ItemStack stack) {
        final SimpleInventory inventory = this.getInventory();
        stack = stack.copy();

        stack.setCount(stack.getCount() / 2);

        if (inventory.canInsert(stack)) inventory.addStack(stack);
    }
}
