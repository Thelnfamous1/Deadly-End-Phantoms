package com.infamous.deadlyendphantoms.config;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;


public class GeneralModConfig {
    public static ForgeConfigSpec.BooleanValue DISABLE_OVERWORLD_PHANTOMS;
    public static ForgeConfigSpec.BooleanValue ENABLE_END_PHANTOMS;
    public static ForgeConfigSpec.IntValue END_PHANTOM_SPAWN_WEIGHT;
    public static ForgeConfigSpec.IntValue END_PHANTOM_MIN_GROUP_SIZE;
    public static ForgeConfigSpec.IntValue END_PHANTOM_MAX_GROUP_SIZE;
    public static ForgeConfigSpec.ConfigValue<String> END_PHANTOM_SPAWN_BIOMES;

    public static void init(ForgeConfigSpec.Builder SERVER_BUILDER)
    {

        SERVER_BUILDER.comment("End Phantom Configuration").push("end_phantom_configuration");

        DISABLE_OVERWORLD_PHANTOMS = SERVER_BUILDER.comment("Disable vanilla Phantom spawns in the Overworld [true / false]").define("disableOverworldPhantoms", false);
        ENABLE_END_PHANTOMS = SERVER_BUILDER.comment("Allow Phantoms to spawn [true / false]").define("enableEndPhantoms", true);
        END_PHANTOM_SPAWN_WEIGHT = SERVER_BUILDER.comment("Spawn weight of Phantoms spawning in the End [0-100, default: 5]").defineInRange("endPhantomSpawnWeight", 3, 0, 100);
        END_PHANTOM_MIN_GROUP_SIZE = SERVER_BUILDER.comment("Minimum group size of Phantoms spawning in the End [0-100, default: 4]").defineInRange("endPhantomMinGroupSize", 4, 0, 100);
        END_PHANTOM_MAX_GROUP_SIZE = SERVER_BUILDER.comment("Maximum group size of Phantoms spawning in the End [0-100, default: 4]").defineInRange("endPhantomMaxGroupSize", 4, 0, 100);
        END_PHANTOM_SPAWN_BIOMES = SERVER_BUILDER.comment("Biomes for end Phantoms to spawn in. You can use /end_biomes command to get a list of all End biomes, incuding modded ones.").define("endPhantomsSpawnBiomes", "[minecraft:end_midlands,minecraft:end_highlands]");
        SERVER_BUILDER.pop();
    }

    public static String[] getSpawnBiomes(){
        String s = END_PHANTOM_SPAWN_BIOMES.get();
        s = s.substring(1, s.length() - 1);
        return s.split(",");
    }

    public static boolean shouldSpawnPhantoms(ResourceLocation biomeName){
        String name = biomeName.toString();
        for (String b : getSpawnBiomes()){
            if (b.equals(name))
                return true;
        }
        return false;
    }
}
