package com.superworldsun.superslegend.items.weapons;

import java.util.function.Predicate;

import com.superworldsun.superslegend.SupersLegendMain;
import com.superworldsun.superslegend.entities.projectiles.magic.IceballEntity;
import com.superworldsun.superslegend.mana.ManaProvider;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class IceRod extends Item
{
	public IceRod()
	{
		super(new Item.Properties().stacksTo(1).tab(SupersLegendMain.RESOURCES));
	}
	
	@Override
	public ActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand)
	{
		if (playerEntity.isCrouching())
		{
			if (!world.isClientSide)
			{
				// per use
				float manacost = 2F;
				
				if (ManaProvider.get(playerEntity).getMana() >= manacost || playerEntity.abilities.instabuild)
				{
					float iceballSpeed = 0.5F;
					Vector3d playerLookVec = playerEntity.getLookAngle();
					Vector3d iceballPosition = playerEntity.getEyePosition(1F).add(playerLookVec);
					Vector3d iceballMotion = playerLookVec.multiply(iceballSpeed, iceballSpeed, iceballSpeed);
					IceballEntity iceballEntity = new IceballEntity(iceballPosition, iceballMotion, world, playerEntity);
					world.addFreshEntity(iceballEntity);
					ManaProvider.get(playerEntity).spendMana(manacost);
					// we need to sync mana after spending it because of server side check
					ManaProvider.sync((ServerPlayerEntity) playerEntity);
					playerEntity.getCooldowns().addCooldown(this, 16);
					world.playSound(null, playerEntity.position().x, playerEntity.position().y, playerEntity.position().z, SoundEvents.SNOW_BREAK, SoundCategory.PLAYERS, 1F, 1F);
				}
			}
		}
		else
		{
			playerEntity.startUsingItem(hand);
		}
		
		return ActionResult.consume(playerEntity.getItemInHand(hand));
	}
	
	@Override
	public UseAction getUseAnimation(ItemStack itemStack)
	{
		return UseAction.BOW;
	}
	
	@Override
	public int getUseDuration(ItemStack itemStack)
	{
		return 72000;
	}
	
	@Override
	public void onUseTick(World world, LivingEntity livingEntity, ItemStack itemStack, int timeInUse)
	{
		if (livingEntity instanceof PlayerEntity)
		{
			// per tick
			float manacost = 0.025F;
			PlayerEntity player = (PlayerEntity) livingEntity;
			
			if (ManaProvider.get(player).getMana() < manacost && !player.abilities.instabuild)
			{
				// no effect in not enough mana and not in creative mod
				return;
			}
			
			int particlesDensity = 2;
			float particlesSpread = 0.3F;
			float particlesSpeed = 1F;
			float effectRange = 6F;
			float damage = 1F;
			
			Vector3d playerLookVec = player.getLookAngle();
			Vector3d effectStart = player.getEyePosition(1F).add(playerLookVec);
			Vector3d effectEnd = effectStart.add(playerLookVec.multiply(effectRange, effectRange, effectRange));
			Vector3d particlesMotionVec = playerLookVec.multiply(particlesSpeed, particlesSpeed, particlesSpeed);
			
			for (int i = 0; i < particlesDensity; i++)
			{
				double particleX = effectStart.x + (player.getRandom().nextFloat() * 2 - 1) * particlesSpread;
				double particleY = effectStart.y + (player.getRandom().nextFloat() * 2 - 1) * particlesSpread;
				double particleZ = effectStart.z + (player.getRandom().nextFloat() * 2 - 1) * particlesSpread;
				double particleMotionX = particlesMotionVec.x + (player.getRandom().nextFloat() * 2 - 1) * particlesSpread / 5F;
				double particleMotionY = particlesMotionVec.y + (player.getRandom().nextFloat() * 2 - 1) * particlesSpread / 5F;
				double particleMotionZ = particlesMotionVec.z + (player.getRandom().nextFloat() * 2 - 1) * particlesSpread / 5F;
				world.addParticle(ParticleTypes.SPIT, particleX, particleY, particleZ, particleMotionX, particleMotionY, particleMotionZ);
			}
			
			BlockRayTraceResult blockRayTraceResult = world.clip(new RayTraceContext(effectStart, effectEnd, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.ANY, null));
			
			if (blockRayTraceResult.getType() != RayTraceResult.Type.MISS)
			{
				// if we hit block, area of effect ends at the hit location
				effectEnd = blockRayTraceResult.getLocation();
				// blocks effect
				// once per second
				if (timeInUse % 20 == 0)
				{
					BlockPos hitPos = blockRayTraceResult.getBlockPos();
					
					if (world.getFluidState(hitPos).is(FluidTags.WATER))
					{
						world.setBlock(hitPos, Blocks.ICE.defaultBlockState(), 3);
					}
					else if (world.getFluidState(hitPos).is(FluidTags.LAVA))
					{
						world.setBlock(hitPos, Blocks.OBSIDIAN.defaultBlockState(), 3);
						world.playSound(null, hitPos, SoundEvents.FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1F, 1F);
					}
					else
					{
						BlockPos snowPos = hitPos.relative(blockRayTraceResult.getDirection());
						
						if (Blocks.SNOW.defaultBlockState().canSurvive(world, snowPos))
						{
							BlockState snowBlockState = Blocks.SNOW.defaultBlockState();
							world.setBlock(snowPos, snowBlockState, 3);
						}
					}
				}
			}
			
			// we want to only attack living entities
			Predicate<Entity> canHit = e -> e instanceof LivingEntity;
			EntityRayTraceResult entityRayTraceResult = ProjectileHelper.getEntityHitResult(world, player, effectStart, effectEnd, new AxisAlignedBB(effectStart, effectEnd).inflate(1.0D), canHit);
			
			// if we hit entity
			if (entityRayTraceResult != null)
			{
				DamageSource damageSource = DamageSource.playerAttack(player);
				entityRayTraceResult.getEntity().hurt(damageSource, damage);
			}
			
			// only spend mana in survival mode
			if (!player.abilities.instabuild)
			{
				ManaProvider.get(player).spendMana(manacost);
			}
			
			// plays sound 4 times per second
			if (timeInUse % 5 == 0)
			{
				world.playSound(null, player.position().x, player.position().y, player.position().z, SoundEvents.SNOW_BREAK, SoundCategory.PLAYERS, 1F, 1F);
			}
		}
	}
}
