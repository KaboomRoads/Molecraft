package com.kaboomroads.molecraft.util;

import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ResolvableProfile;
import org.apache.commons.codec.binary.Base64;

import java.util.Optional;
import java.util.UUID;

public class ItemUtils {
    public static ItemStack createPlayerHeadFromBase64(String base64) {
        ItemStack itemStack = new ItemStack(Items.PLAYER_HEAD);
        PropertyMap propertyMap = new PropertyMap();
        propertyMap.put("textures", new Property("textures", base64));
        ResolvableProfile resolvableProfile = new ResolvableProfile(
                Optional.empty(),
                Optional.of(UUID.randomUUID()),
                propertyMap
        );
        itemStack.set(DataComponents.PROFILE, resolvableProfile);
        return itemStack;
    }

    private static ItemStack createPlayerHeadFromUrl(String url) {
        byte[] encodedData = Base64.encodeBase64(("{textures:{SKIN:{url:\"" + "http://textures.minecraft.net/texture/" + url + "\"}}}").getBytes());
        return createPlayerHeadFromBase64(new String(encodedData));
    }
}
