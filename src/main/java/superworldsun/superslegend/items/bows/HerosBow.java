package superworldsun.superslegend.items.bows;

import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;

import net.minecraft.item.Item.Properties;

public class HerosBow extends BowItem {
    public HerosBow(Properties builder) {
        super(builder);
    }

    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return false;
    }
}
