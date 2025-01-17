package com.superworldsun.superslegend.items;

import java.util.List;
import java.util.Set;

import com.superworldsun.superslegend.SupersLegendMain;
import com.superworldsun.superslegend.SupersLegendRegistries;
import com.superworldsun.superslegend.songs.LearnedSongsProvider;
import com.superworldsun.superslegend.songs.OcarinaSong;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class AllSongsSheet extends Item
{
	public AllSongsSheet()
	{
		super(new Item.Properties().tab(SupersLegendMain.RESOURCES).stacksTo(1));
	}

	@Override
	public void appendHoverText(ItemStack stack, World world, List<ITextComponent> list, ITooltipFlag flag)
	{
		super.appendHoverText(stack, world, list, flag);
		list.add(new StringTextComponent(TextFormatting.YELLOW + "[Creative Item Only]"));
		list.add(new StringTextComponent(TextFormatting.GOLD + "Right click to Learn All Songs"));
	}

	//TODO add a sound when item is used
	@Override
	public ActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand)
	{
		Set<OcarinaSong> learnedSongs = LearnedSongsProvider.get(playerEntity).getLearnedSongs();
		
		if (!world.isClientSide)
		{
			SupersLegendRegistries.OCARINA_SONGS.forEach(song ->
			{
				if (!learnedSongs.contains(song))
				{
					learnedSongs.add(song);
				}
			});
			
			LearnedSongsProvider.sync((ServerPlayerEntity) playerEntity);
		}

		return ActionResult.success(playerEntity.getItemInHand(hand));
	}
}
