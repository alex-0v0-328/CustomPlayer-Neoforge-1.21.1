package com.unknown.customplayer.custom.enums.body;

import com.mojang.serialization.Codec;
import com.unknown.customplayer.custom.enums.EnumTranslatable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

public enum BodyPart implements StringRepresentable, EnumTranslatable {

    EYES     (PartScale.SENSE, true),
    EARS     (PartScale.SENSE, true),
    MOUTH    (PartScale.SENSE, true),
    NOSE     (PartScale.SENSE, true),
    BRAIN    (PartScale.SENSE, true),

    TORSO    (PartScale.BODY,  true),
    ARM_LEFT (PartScale.BODY,  true),
    ARM_RIGHT(PartScale.BODY,  true),
    LEG_LEFT (PartScale.BODY,  true),
    LEG_RIGHT(PartScale.BODY,  true),

    BONE     (PartScale.BODY,  true),
    SKIN     (PartScale.BODY,  true),
    MUSCLE   (PartScale.BODY,  true),
    SINEW    (PartScale.BODY,  true);

    public static final Codec<BodyPart> CODEC = StringRepresentable.fromEnum(BodyPart::values);
    private static final String KEY_PREFIX = "customplayer.enum.body.body_part.";

    private static final String TAG_PREFIX = "installable/";

    private final PartScale scale;
    private final boolean installable;
    private final TagKey<Item> installTag;

    BodyPart(PartScale scale, boolean installable) {
        this.scale = scale;
        this.installable = installable;
        this.installTag = ItemTags.create(
                ResourceLocation.fromNamespaceAndPath("customplayer", TAG_PREFIX + name().toLowerCase()));
    }

    public PartScale scale() {return scale;}

    public boolean installable() {return installable;}
    public TagKey<Item> installTag() {return installTag;}

    public boolean accepts(Ailment ailment) {return ailment.scale() == scale;}

    public boolean regional() {return this != BONE && this != SKIN && this != MUSCLE && this != SINEW;}

    @Override
    public @NotNull String getSerializedName() {return name().toLowerCase();}
    public String getTranslationKey() {return KEY_PREFIX + name().toLowerCase();}
}
