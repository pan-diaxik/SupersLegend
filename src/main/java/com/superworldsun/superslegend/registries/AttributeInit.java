package com.superworldsun.superslegend.registries;

import com.superworldsun.superslegend.SupersLegendMain;

import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class AttributeInit
{
	public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, SupersLegendMain.MOD_ID);
	
	public static final RegistryObject<Attribute> HEAT_RESISTANCE = rangedAttribute("heat_resistance", 0.0D, -1.0D, 1.0D);
	public static final RegistryObject<Attribute> COLD_RESISTANCE = rangedAttribute("cold_resistance", 0.0D, -1.0D, 1.0D);
	
	private static RegistryObject<Attribute> rangedAttribute(String name, double defaultValue, double minValue, double maxValue)
	{
		return ATTRIBUTES.register(name, () -> new RangedAttribute(name, defaultValue, minValue, maxValue).setSyncable(true));
	}
}
