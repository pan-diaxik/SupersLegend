package com.superworldsun.superslegend.songs.songs;

import com.superworldsun.superslegend.registries.SoundInit;
import com.superworldsun.superslegend.songs.OcarinaSong;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class SonataOfAwakening extends OcarinaSong
{
	public SonataOfAwakening()
	{
		super("ululara");
	}

	@Override
	public SoundEvent getPlayingSound()
	{
		return SoundInit.SONATA_OF_AWAKENING.get();
	}
	
	@Override
	public void onSongPlayed(PlayerEntity player, World level)
	{
		int radius = 5;
		level.getEntities(player, player.getBoundingBox().inflate(radius)).forEach(entity ->
		{
			if (entity instanceof PlayerEntity && ((PlayerEntity) entity).isSleeping())
			{
				((PlayerEntity) entity).stopSleeping();
			}
		});
	}
}
