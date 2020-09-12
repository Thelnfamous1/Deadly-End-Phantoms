package com.infamous.deadlyendphantoms.entity;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import static com.infamous.deadlyendphantoms.DeadlyEndPhantoms.MODID;


public class ModEntityTypes {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, MODID);

    public static final String END_PHANTOM_NAME = "end_phantom";
    public static final RegistryObject<EntityType<EndPhantomEntity>> END_PHANTOM = ENTITY_TYPES.register(END_PHANTOM_NAME, () ->
            EntityType.Builder.<EndPhantomEntity>create(EndPhantomEntity::new, EntityClassification.MONSTER)
                    .size(0.9F, 0.5F)
                    .func_233606_a_(8)
                    .setCustomClientFactory((spawnEntity,world) -> new EndPhantomEntity(world))
                    .build(new ResourceLocation(MODID, END_PHANTOM_NAME).toString())
    );
}
