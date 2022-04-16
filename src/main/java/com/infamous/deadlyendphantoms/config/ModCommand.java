package com.infamous.deadlyendphantoms.config;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber
public class ModCommand {
    @SubscribeEvent
    static void regCommands(RegisterCommandsEvent event){
        event.getDispatcher().register(
            Commands.literal("end_biomes")
            .requires((source) -> source.hasPermissionLevel(4))
            .executes(ModCommand::run));
    }

    static int run (CommandContext<CommandSource> ctx){
        List<Biome> endBiomes = ctx.getSource().getServer().getWorld(World.THE_END).getChunkProvider().generator.getBiomeProvider().getBiomes();
        String[] biomeNames = new String[endBiomes.size()];
        int i = 0;
        for (Biome b : endBiomes){
            biomeNames[i++] = b.getRegistryName().toString();
            ctx.getSource().sendFeedback(new StringTextComponent(b.getRegistryName().toString()), true);
        }
        ctx.getSource().sendFeedback(new StringTextComponent(String.join(",", biomeNames)), true);
        return 0;
    }
}
