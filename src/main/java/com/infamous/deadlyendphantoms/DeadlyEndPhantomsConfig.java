package com.infamous.deadlyendphantoms;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;

import java.nio.file.Path;

public class DeadlyEndPhantomsConfig {
        private static final ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();

        public static final ForgeConfigSpec SERVER_CONFIG;

        static
        {
            GeneralModConfig.init(SERVER_BUILDER);

            SERVER_CONFIG = SERVER_BUILDER.build();
        }

        public static void loadConfig(ForgeConfigSpec spec, Path path)
        {
            final CommentedFileConfig configData = CommentedFileConfig.builder(path).sync().autosave().writingMode(WritingMode.REPLACE).build();

            configData.load();

            spec.setConfig(configData);
        }
}
