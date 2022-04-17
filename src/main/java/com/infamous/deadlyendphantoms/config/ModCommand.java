package com.infamous.deadlyendphantoms.config;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.impl.LocateCommand;
import net.minecraft.command.impl.SeedCommand;
import net.minecraft.command.impl.TeleportCommand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
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
        }
        String text = String.join(",", biomeNames);
        ITextComponent msg = new StringTextComponent(text).modifyStyle(style -> style
            .setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, text))
            .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslationTextComponent("chat.copy.click")))
            .setInsertion(text)
        );
        ctx.getSource().sendFeedback(msg, true);
        return 0;
    }
}
