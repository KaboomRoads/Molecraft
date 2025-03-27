package com.kaboomroads.molecraft.util;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record PlaceholderContents(ResourceLocation placeholderId) implements ComponentContents {
    public static final MapCodec<PlaceholderContents> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(ResourceLocation.CODEC.fieldOf("placeholder").forGetter(contents -> contents.placeholderId)).apply(instance, PlaceholderContents::new)
    );
    public static final Type<PlaceholderContents> TYPE = new Type<>(CODEC, "placeholder");

    @NotNull
    @Override
    public Type<?> type() {
        return TYPE;
    }
}
