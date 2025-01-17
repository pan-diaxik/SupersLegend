package com.superworldsun.superslegend.items.items;

import com.superworldsun.superslegend.events.MaxHealthEvents;
import com.superworldsun.superslegend.registries.SoundInit;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

public class VoidContainer extends Item
{
	public VoidContainer(Properties properties)
	{
		super(properties);
	}

	@Override
	public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand)
	{
		ItemStack itemstack = player.getItemInHand(hand);
		AttributeModifier heartsModifier = player.getAttribute(Attributes.MAX_HEALTH).getModifier(HeartContainer.HEARTS_MODIFIER_ID);
		AttributeModifier baseModifier = player.getAttribute(Attributes.MAX_HEALTH).getModifier(MaxHealthEvents.BASE_HEALTH_MODIFIER_ID);
		double playerBaseHealth = player.getAttribute(Attributes.MAX_HEALTH).getBaseValue();
		
		// Can't use if max health will go below 0 (base + hearts, bonuses from other mods don't count)
		if (heartsModifier != null && playerBaseHealth + baseModifier.getAmount() + heartsModifier.getAmount() <= 2)
		{
			return ActionResult.fail(itemstack);
		}
		else
		{
			if (heartsModifier == null)
			{
				heartsModifier = new AttributeModifier(HeartContainer.HEARTS_MODIFIER_ID, "Hearts", -2.0D, Operation.ADDITION);
			}
			else
			{
				player.getAttribute(Attributes.MAX_HEALTH).removeModifier(heartsModifier);
				heartsModifier = new AttributeModifier(HeartContainer.HEARTS_MODIFIER_ID, "Hearts", heartsModifier.getAmount() - 2.0D, Operation.ADDITION);
			}
			BlockPos currentPos = player.blockPosition();
			world.playSound(null, currentPos.getX(), currentPos.getY(), currentPos.getZ(), SoundInit.HEART.get(), SoundCategory.PLAYERS, 1f, 0.1f);
			itemstack.shrink(1);
			// Take 2 health (1 heart)
			player.getAttribute(Attributes.MAX_HEALTH).addPermanentModifier(heartsModifier);
			// Update current health
			
			if (player.getHealth() > player.getMaxHealth())
			{
				player.setHealth(player.getMaxHealth());
			}
			return ActionResult.consume(itemstack);
		}
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void appendHoverText(ItemStack stack, World world, List<ITextComponent> list, ITooltipFlag flag)
	{
		super.appendHoverText(stack, world, list, flag);
		list.add(new StringTextComponent(TextFormatting.BLACK + "Decreases Maximum Health"));
		list.add(new StringTextComponent(TextFormatting.GREEN + "Right-click to use"));
	}
}
