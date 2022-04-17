package com.infamous.deadlyendphantoms.entity;

import com.infamous.deadlyendphantoms.DeadlyEndPhantoms;
import com.infamous.deadlyendphantoms.config.GeneralModConfig;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.monster.PhantomEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(modid = DeadlyEndPhantoms.MODID)
public class SpawnEventHandler {

    @SubscribeEvent
    public static void onEndPhantomSpawn(LivingSpawnEvent.SpecialSpawn event){
        // Check for an end phantom that has spawned in the End and was not spawned by a Spawn Egg
        if(event.getEntityLiving() instanceof SpecterEntity
                && event.getEntityLiving().getEntityWorld().getDimensionKey() == World.THE_END
                && event.getSpawnReason() == SpawnReason.NATURAL){
            SpecterEntity phantom = (SpecterEntity)event.getEntity();

            BlockPos phantomsSkyPostion = phantom.getPosition();
            phantom.setPosition(phantomsSkyPostion.getX(), phantomsSkyPostion.getY() + 20.0D, phantomsSkyPostion.getZ());
        }
    }


    @SubscribeEvent
    public static void onOverworldPhantomSpawnNatural(LivingSpawnEvent.SpecialSpawn event){
        // Check for a vanilla phantom that spawned naturally in the overworld
        if(event.getEntityLiving() instanceof PhantomEntity
                && GeneralModConfig.DISABLE_OVERWORLD_PHANTOMS.get()
                // Finding overworld dimension hopefully
                //&& event.getEntityLiving().getEntityWorld().func_234923_W_() == field_234918_g_
                && event.getSpawnReason() == SpawnReason.NATURAL){
            DeadlyEndPhantoms.LOGGER.info("Prevented natural vanilla Phantom spawn!");
            event.setCanceled(true);
        }
    }
}
