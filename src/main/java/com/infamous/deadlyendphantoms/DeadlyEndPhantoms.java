package com.infamous.deadlyendphantoms;

import com.infamous.deadlyendphantoms.config.DeadlyEndPhantomsConfig;
import com.infamous.deadlyendphantoms.entity.*;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("deadlyendphantoms")
public class DeadlyEndPhantoms
{
    // Directly reference a log4j logger.
    public static final String MODID = "deadlyendphantoms";
    public static final Logger LOGGER = LogManager.getLogger();

    public DeadlyEndPhantoms() {

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, DeadlyEndPhantomsConfig.SERVER_CONFIG);

        DeadlyEndPhantomsConfig.loadConfig(DeadlyEndPhantomsConfig.SERVER_CONFIG, FMLPaths.CONFIGDIR.get().resolve("deadlyendphantoms-general.toml"));

        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the enqueueIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        // Register the processIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
        // Register the doClientStuff method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        //MinecraftForge.EVENT_BUS.register(new PhantomSpawnEvent());

        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModEntityTypes.ENTITY_TYPES.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);

    }

    private void setup(final FMLCommonSetupEvent event)
    {
        DeferredWorkQueue.runLater(() -> {
            GlobalEntityTypeAttributes.put(ModEntityTypes.SPECTER.get(), SpecterEntity.setCustomAttributes().create());
            EntitySpawnPlacements.initSpawnPlacements();
        });

    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(ModEntityTypes.SPECTER.get(), SpecterRenderer::new);
    }

    private void enqueueIMC(final InterModEnqueueEvent event){
    }

    private void processIMC(final InterModProcessEvent event){}
    {
    }
}
