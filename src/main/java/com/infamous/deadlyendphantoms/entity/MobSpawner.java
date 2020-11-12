package com.infamous.deadlyendphantoms.entity;

import com.infamous.deadlyendphantoms.DeadlyEndPhantoms;
import com.infamous.deadlyendphantoms.DeadlyEndPhantomsConfig;
import com.infamous.deadlyendphantoms.GeneralModConfig;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.world.MobSpawnInfoBuilder;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.ListIterator;

@Mod.EventBusSubscriber(modid = DeadlyEndPhantoms.MODID)
public class MobSpawner {

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onBiomeLoading(BiomeLoadingEvent event){
        ResourceLocation biomeName = event.getName();
        if(biomeName != null && (biomeName.toString().equals("minecraft:end_midlands") || biomeName.toString().equals("minecraft:end_highlands"))){
            MobSpawnInfoBuilder mobSpawnInfoBuilder = event.getSpawns();
            List<MobSpawnInfo.Spawners> monsterSpawnersList = mobSpawnInfoBuilder.getSpawner(EntityClassification.MONSTER);
            monsterSpawnersList.add(new MobSpawnInfo.Spawners(
                    ModEntityTypes.SPECTER.get(),
                    GeneralModConfig.END_PHANTOM_SPAWN_WEIGHT.get(),
                    GeneralModConfig.END_PHANTOM_MIN_GROUP_SIZE.get(),
                    GeneralModConfig.END_PHANTOM_MAX_GROUP_SIZE.get()));
        }
    }
}
