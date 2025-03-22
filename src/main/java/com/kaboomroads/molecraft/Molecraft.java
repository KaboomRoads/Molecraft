package com.kaboomroads.molecraft;

import com.kaboomroads.molecraft.init.ModAttributes;
import com.kaboomroads.molecraft.init.ModBlocks;
import com.kaboomroads.molecraft.init.ModEntityDataSerializers;
import com.kaboomroads.molecraft.init.ModItems;
import com.mojang.logging.LogUtils;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;

public class Molecraft implements ModInitializer {
    private static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void onInitialize() {
        ModAttributes.init();
        ModEntityDataSerializers.init();
        ModItems.init();
        ModBlocks.init();
//        try {
//            LOGGER.info("Loading init classes from mod \"" + ModConstants.MOD_ID + "\"");
//            for (ClassPath.ClassInfo classInfo : ClassPath.from(ClassLoader.getSystemClassLoader()).getAllClasses()) {
//                if (classInfo.getPackageName().equalsIgnoreCase(getClass().getPackageName() + ".init")) {
//                    classInfo.load();
//                    LOGGER.info(classInfo.getName());
//                }
//            }
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
    }
}
