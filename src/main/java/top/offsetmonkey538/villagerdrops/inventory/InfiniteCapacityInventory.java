package top.offsetmonkey538.villagerdrops.inventory;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.component.ComponentChanges;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.dynamic.Codecs;

public class InfiniteCapacityInventory extends SimpleInventory {
    private static final Codec<ItemStack> INFINITE_COUNT_ITEM_CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Item.ENTRY_CODEC.fieldOf("id").forGetter(ItemStack::getRegistryEntry),
                    Codecs.rangedInt(1, Integer.MAX_VALUE).fieldOf("count").orElse(1).forGetter(ItemStack::getCount),
                    ComponentChanges.CODEC.optionalFieldOf("components", ComponentChanges.EMPTY).forGetter(stack -> stack.components.getChanges())
            ).apply(instance, ItemStack::new)
    );

    public InfiniteCapacityInventory(final int size) {
        super(size);
    }

    @Override
    public int getMaxCountPerStack() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getMaxCount(ItemStack stack) {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean canInsert(ItemStack stackToInsert) {
        for (final ItemStack existingStack : getHeldStacks()) {
            if (existingStack.isEmpty() || ItemStack.areItemsAndComponentsEqual(existingStack, stackToInsert)) return true;
        }

        return false;
    }

    public void read(final ReadView view, final String key) {
        view.getOptionalTypedListView(key, INFINITE_COUNT_ITEM_CODEC).ifPresent(this::readDataList);
    }

    public void write(final WriteView view, final String key) {
        this.toDataList(view.getListAppender(key, INFINITE_COUNT_ITEM_CODEC));
    }
}
