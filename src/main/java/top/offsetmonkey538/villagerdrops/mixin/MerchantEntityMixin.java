package top.offsetmonkey538.villagerdrops.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.InventoryOwner;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.village.TradeOffer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.offsetmonkey538.villagerdrops.inventory.InfiniteCapacityInventory;

@Mixin(MerchantEntity.class)
public abstract class MerchantEntityMixin extends PassiveEntity implements InventoryOwner {

    protected MerchantEntityMixin(EntityType<? extends PassiveEntity> entityType, World world) {
        super(entityType, world);
    }

    @Unique
    private final InfiniteCapacityInventory villagerdrops$tradedItemInventory = new InfiniteCapacityInventory(9);
    @Unique
    private static final String villagerdrops$tradedItemInventoryKey = "VillagerDropsTradedItemInventory";

    @Inject(
            method = "readCustomData",
            at = @At("TAIL")
    )
    private void villagerdrops$readTradedItemInventory(ReadView view, CallbackInfo ci) {
        villagerdrops$tradedItemInventory.read(view, villagerdrops$tradedItemInventoryKey);
    }

    @Inject(
            method = "writeCustomData",
            at = @At("TAIL")
    )
    private void villagerdrops$writeTradedItemInventory(WriteView view, CallbackInfo ci) {
        villagerdrops$tradedItemInventory.write(view, villagerdrops$tradedItemInventoryKey);
    }

    @Inject(
            method = "onDeath",
            at = @At("TAIL")
    )
    private void villagerdrops$dropInventoryStacksOnDeath(DamageSource damageSource, CallbackInfo ci) {
        if (!(this.getWorld() instanceof final ServerWorld world)) return;

        this.getInventory().clearToList().forEach(stack -> this.dropStack(world, stack));
        villagerdrops$tradedItemInventory.clearToList().forEach(stack -> {
            stack.setCount(stack.getCount() / 2);
            this.dropStack(world, stack);
        });
    }

    @Inject(
            method = "trade",
            at = @At("TAIL")
    )
    private void villagerdrops$addHalfTradeItemsToInventory(TradeOffer offer, CallbackInfo ci) {
        villagerdrops$handleAddingItemStackToInventory(offer.getDisplayedFirstBuyItem());
        villagerdrops$handleAddingItemStackToInventory(offer.getDisplayedSecondBuyItem());
    }

    @Unique
    private void villagerdrops$handleAddingItemStackToInventory(ItemStack stack) {
        final SimpleInventory inventory = villagerdrops$tradedItemInventory;
        stack = stack.copy();

        if (inventory.canInsert(stack)) inventory.addStack(stack);
    }
}
