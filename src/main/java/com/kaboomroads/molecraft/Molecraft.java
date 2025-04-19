package com.kaboomroads.molecraft;

import com.kaboomroads.molecraft.command.MCMCommand;
import com.kaboomroads.molecraft.command.MolecraftCommands;
import com.kaboomroads.molecraft.command.TradeCommand;
import com.mojang.logging.LogUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.slf4j.Logger;

public class Molecraft implements ModInitializer {
    private static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, context, selection) -> {
            MolecraftCommands.register(dispatcher);
            MCMCommand.register(dispatcher, context);
            TradeCommand.register(dispatcher);
        });
    }
}
