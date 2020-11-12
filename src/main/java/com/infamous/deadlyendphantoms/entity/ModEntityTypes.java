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

    public static final String SPECTER_NAME = "specter";
    public static final RegistryObject<EntityType<SpecterEntity>> SPECTER = ENTITY_TYPES.register(SPECTER_NAME, () ->
            EntityType.Builder.<SpecterEntity>create(SpecterEntity::new, EntityClassification.MONSTER)
                    .size(0.9F, 0.5F)
                    .func_233606_a_(8)
                    .setCustomClientFactory((spawnEntity,world) -> new SpecterEntity(world))
                    .build(new ResourceLocation(MODID, SPECTER_NAME).toString())
    );
}
