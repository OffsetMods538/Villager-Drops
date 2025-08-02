package top.offsetmonkey538.villagerdrops.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.InventoryOwner;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.village.TradeOffer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Group;
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


    // Loading and saving the custom inventory is fun between different versions


    /* Loading */

    // 1.21.6+
    @Group(name = "villagerdrops$loadTradedItemInventory", min = 1, max = 1)
    @Inject(
            method = {
                    // Intermediary
                    "method_5749(Lnet/minecraft/class_11368;)V",
                    // Yarn
                    "readCustomData(Lnet/minecraft/storage/ReadView;)V",
                    // Mojmaps
                    "readAdditionalSaveData(Lnet/minecraft/world/level/storage/ValueInput;)V"
            },
            at = @At("TAIL"),
            remap = false
    )
    private void villagerdrops$readTradedItemInventory1216(Object readView, CallbackInfo ci) {
        villagerdrops$tradedItemInventory.read(readView, villagerdrops$tradedItemInventoryKey);
    }

    // 1.21.5
    @Group(name = "villagerdrops$loadTradedItemInventory", min = 1, max = 1)
    @Inject(
            method = {
                    // Intermediary
                    "method_5749(Lnet/minecraft/class_2487;)V",
                    // Yarn
                    "readCustomDataFromNbt(Lnet/minecraft/nbt/NbtCompound;)V",
                    // Mojmaps
                    "readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V"
            },
            at = @At("TAIL"),
            remap = false
    )
    private void villagerdrops$readTradedItemInventory(NbtCompound nbt, CallbackInfo ci) {
        nbt.getList(villagerdrops$tradedItemInventoryKey).ifPresent(inventory -> villagerdrops$tradedItemInventory.readNbtList(inventory, this.getRegistryManager()));
    }


    /* Saving */

    // 1.21.6+
    @Group(name = "villagerdrops$saveTradedItemInventory", min = 1, max = 1)
    @Inject(
            method = {
                    // Intermediary
                    "method_5652(Lnet/minecraft/class_11372;)V",
                    // Yarn
                    "writeCustomData(Lnet/minecraft/storage/WriteView;)V",
                    // Mojmaps
                    "addAdditionalSaveData(Lnet/minecraft/world/level/storage/ValueOutput;)V"
            },
            at = @At("TAIL"),
            remap = false
    )
    private void villagerdrops$writeTradedItemInventory(Object writeView, CallbackInfo ci) {
        villagerdrops$tradedItemInventory.write(writeView, villagerdrops$tradedItemInventoryKey);
    }

    // 1.21.5
    @Group(name = "villagerdrops$saveTradedItemInventory", min = 1, max = 1)
    @Inject(
            method = {
                    // Intermediary
                    "method_5652(Lnet/minecraft/class_2487;)V",
                    // Yarn
                    "writeCustomDataToNbt(Lnet/minecraft/nbt/NbtCompound;)V",
                    // Mojmaps
                    "addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V"
            },
            at = @At("TAIL"),
            remap = false
    )
    private void villagerdrops$writeTradedItemInventory(NbtCompound nbt, CallbackInfo ci) {
        nbt.put(villagerdrops$tradedItemInventoryKey, villagerdrops$tradedItemInventory.toNbtList(this.getRegistryManager()));
    }
}
