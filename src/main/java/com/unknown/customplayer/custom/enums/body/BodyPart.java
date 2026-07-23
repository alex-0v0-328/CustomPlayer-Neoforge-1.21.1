package com.unknown.customplayer.custom.enums.body;

import com.mojang.serialization.Codec;
import com.unknown.customplayer.custom.enums.EnumTranslatable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

//  The fourteen addressable parts, on two scales. There is NO HEAD -- BRAIN is drawn as the skull.
//  ⚠ BONE/SKIN/MUSCLE/SINEW are WHOLE-BODY: not sided, not regional, never drawn on the figure. See vault.
public enum BodyPart implements StringRepresentable, EnumTranslatable {

    //  Senses [眼耳口鼻脑] -- three degrees, upper two stored.
    EYES     (PartScale.SENSE, true),
    EARS     (PartScale.SENSE, true),
    MOUTH    (PartScale.SENSE, true),
    NOSE     (PartScale.SENSE, true),
    BRAIN    (PartScale.SENSE, true),

    //  Trunk and limbs [身与四肢] -- four grades, named for what undoing them costs.
    TORSO    (PartScale.BODY,  true),
    ARM_LEFT (PartScale.BODY,  true),
    ARM_RIGHT(PartScale.BODY,  true),
    LEG_LEFT (PartScale.BODY,  true),
    LEG_RIGHT(PartScale.BODY,  true),

    //  Whole-body tissues [骨肌肤筋] -- not sided, not regional, not on the figure; still host.
    BONE     (PartScale.BODY,  true),
    SKIN     (PartScale.BODY,  true),
    MUSCLE   (PartScale.BODY,  true),
    SINEW    (PartScale.BODY,  true);

    public static final Codec<BodyPart> CODEC = StringRepresentable.fromEnum(BodyPart::values);
    private static final String KEY_PREFIX = "customplayer.enum.body.body_part.";

    //  ⚠ Tag lets this mod stay ignorant of what installs; a dependent fills installable/<part>.
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

    //  Whether it can host anything at ALL -- separate from whether it can RIGHT NOW (that is the ailment).
    public boolean installable() {return installable;}
    public TagKey<Item> installTag() {return installTag;}

    //  ⚠ A cross-scale pair is unrepresentable, not merely refused: a leg can never read "blind".
    public boolean accepts(Ailment ailment) {return ailment.scale() == scale;}

    //  ⚠ Whether the part has a PLACE on the body (bone/skin are everywhere, so no spot) -- see vault.
    public boolean regional() {return this != BONE && this != SKIN && this != MUSCLE && this != SINEW;}

    @Override
    public @NotNull String getSerializedName() {return name().toLowerCase();}
    public String getTranslationKey() {return KEY_PREFIX + name().toLowerCase();}
}
