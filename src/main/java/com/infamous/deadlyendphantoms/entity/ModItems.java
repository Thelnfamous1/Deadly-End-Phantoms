package com.infamous.deadlyendphantoms.entity;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import static com.infamous.deadlyendphantoms.DeadlyEndPhantoms.MODID;


public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static final RegistryObject<ModSpawnEggItem> END_PHANTOM_SPAWN_EGG = ITEMS.register("end_phantom_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityTypes.END_PHANTOM, 0x000000, 0x7ffd04, new Item.Properties().group(ItemGroup.MISC)));
}
