package top.offsetmonkey538.villagerdrops.mixin;

import net.minecraft.component.MergedComponentMap;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemStack.class)
public interface ItemStackAccessor {

    @Accessor("components")
    MergedComponentMap villagerdrops$getMergedComponentsMap();
}
