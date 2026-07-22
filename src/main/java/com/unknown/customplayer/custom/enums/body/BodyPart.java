package com.unknown.customplayer.custom.enums.body;

import com.mojang.serialization.Codec;
import com.unknown.customplayer.custom.enums.EnumTranslatable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

//  The fourteen addressable parts of a player, on two scales.
//  ⚠ There is NO `HEAD`. The head is what the five sense parts are IN, not a sixth thing that can be
//  hurt on its own -- BRAIN is what the screen draws as the skull. Do not re-add a placeholder part.
//  ⚠ BONE / SKIN / MUSCLE / SINEW are WHOLE-BODY: not sided, not regional, and never drawn on the
//  figure. "The bones" is the state of all of them, not a break in one place.
public enum BodyPart implements StringRepresentable, EnumTranslatable {

    //  眼耳口鼻脑 -- three degrees, of which two are stored.
    EYES     (PartScale.SENSE, true),
    EARS     (PartScale.SENSE, true),
    MOUTH    (PartScale.SENSE, true),
    NOSE     (PartScale.SENSE, true),
    BRAIN    (PartScale.SENSE, true),

    //  身与四肢 -- four grades, named for what undoing them costs.
    TORSO    (PartScale.BODY,  true),
    ARM_LEFT (PartScale.BODY,  true),
    ARM_RIGHT(PartScale.BODY,  true),
    LEG_LEFT (PartScale.BODY,  true),
    LEG_RIGHT(PartScale.BODY,  true),

    //  骨肌肤筋 -- whole-body, and nothing installs into them: something living IN your skin is a
    //  different idea from something set into an eye socket, and it has not been specified.
    BONE     (PartScale.BODY,  false),
    SKIN     (PartScale.BODY,  false),
    MUSCLE   (PartScale.BODY,  false),
    SINEW    (PartScale.BODY,  false);

    public static final Codec<BodyPart> CODEC = StringRepresentable.fromEnum(BodyPart::values);
    private static final String KEY_PREFIX = "customplayer.enum.body.body_part.";

    //  ⚠ The tag is how this mod stays ignorant of what gets installed. A dependent adds its own items
    //  to customplayer:installable/<part>; nothing here ever names an item.
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

    //  Whether this part can hold anything at all. ⚠ Separate from whether it can hold it RIGHT NOW --
    //  that second question is the part's ailment, and the two must not be folded together.
    public boolean installable() {return installable;}
    public TagKey<Item> installTag() {return installTag;}

    //  ⚠ A cross-scale pair is unrepresentable, not merely refused: a leg can never read "blind".
    public boolean accepts(Ailment ailment) {return ailment.scale() == scale;}

    //  Whole-body parts are the ones with no place on the figure. The screen reads this to decide
    //  which parts get a drawn region and which get a labelled square underneath.
    public boolean regional() {return installable || scale == PartScale.SENSE;}

    @Override
    public @NotNull String getSerializedName() {return name().toLowerCase();}
    public String getTranslationKey() {return KEY_PREFIX + name().toLowerCase();}
}
