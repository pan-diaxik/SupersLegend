package com.superworldsun.superslegend.items.armors;

import java.util.HashMap;
import java.util.Map;

import com.superworldsun.superslegend.SupersLegendMain;
import com.superworldsun.superslegend.client.model.armor.DesertVoeBootsModel;
import com.superworldsun.superslegend.client.model.armor.DesertVoeChestplateModel;
import com.superworldsun.superslegend.client.model.armor.DesertVoeHelmetModel;
import com.superworldsun.superslegend.client.model.armor.DesertVoeLeggingsModel;
import com.superworldsun.superslegend.registries.ArmourInit;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;

public class DesertVoeArmor extends ArmorItem
{
	private static final Map<EquipmentSlotType, BipedModel<?>> MODELS_CACHE = new HashMap<>();
	
	public DesertVoeArmor(EquipmentSlotType slot)
	{
		// change armor material if needed
		super(ArmourInit.MAGIC, slot, new Properties().tab(SupersLegendMain.APPAREL));
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <M extends BipedModel<?>> M getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlotType armorSlot, M _default)
	{
		if (MODELS_CACHE.isEmpty())
		{
			MODELS_CACHE.put(EquipmentSlotType.HEAD, new DesertVoeHelmetModel());
			MODELS_CACHE.put(EquipmentSlotType.CHEST, new DesertVoeChestplateModel());
			MODELS_CACHE.put(EquipmentSlotType.LEGS, new DesertVoeLeggingsModel());
			MODELS_CACHE.put(EquipmentSlotType.FEET, new DesertVoeBootsModel());
		}
		
		return (M) MODELS_CACHE.get(armorSlot);
	}
	
	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot, String type)
	{
		return SupersLegendMain.MOD_ID + ":textures/models/armor/desert_voe_armor.png";
	}
}