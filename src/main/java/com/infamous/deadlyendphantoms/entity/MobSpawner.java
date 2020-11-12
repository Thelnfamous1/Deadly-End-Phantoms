package com.infamous.deadlyendphantoms.entity;

import com.infamous.deadlyendphantoms.GeneralModConfig;
import net.minecraft.entity.EntityClassification;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraftforge.registries.ForgeRegistries;

public class MobSpawner {

    public static void setupMobSpawn()
    {

        for(Biome biome : ForgeRegistries.BIOMES)
        {
            if(isEndCityBiome(biome) && GeneralModConfig.ENABLE_END_PHANTOMS.get()){
                // public SpawnListEntry(EntityType<?> entityTypeIn, int weight, int minGroupCountIn, int maxGroupCountIn) {
                //         super(weight);
                //         this.entityType = entityTypeIn;
                //         this.minGroupCount = minGroupCountIn;
                //         this.maxGroupCount = maxGroupCountIn;
                //      }
                biome.getSpawns(EntityClassification.MONSTER).add(new Biome.SpawnListEntry(ModEntityTypes.SPECTER.get(),
                        GeneralModConfig.END_PHANTOM_SPAWN_WEIGHT.get(),
                        GeneralModConfig.END_PHANTOM_MIN_GROUP_SIZE.get(),
                        GeneralModConfig.END_PHANTOM_MAX_GROUP_SIZE.get()));

            }
        }
    }

    public static boolean isEndCityBiome(Biome biome){
        if (biome == Biomes.END_HIGHLANDS || biome == Biomes.END_MIDLANDS){
            return true;
        }
        return false;
    }
}
