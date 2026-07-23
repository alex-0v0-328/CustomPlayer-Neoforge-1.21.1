package com.unknown.customplayer.custom.enums.body;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

//  What is wrong with a part, as ONE value on an ordered ladder -- never a set. The worse reading wins.
//  ⚠ Ordinal order within each scale IS the severity order; worseThan() reads it and nothing else may.
public enum Ailment implements StringRepresentable {

    //region SENSE -- 眼耳口鼻脑
    //  ⚠ Degree one (眩光/耳鸣/沙哑/鼻塞/眩晕) is absent: those are MobEffects, not stored here. See vault.
    LOST(PartScale.SENSE),
    DESTROYED(PartScale.SENSE),
    //endregion

    //region BODY -- 身/四肢/骨肌肤筋, graded by what it takes to undo
    STRAIN(PartScale.BODY),
    WOUND(PartScale.BODY),
    CRIPPLED(PartScale.BODY),
    RUINED(PartScale.BODY);
    //endregion

    public static final Codec<Ailment> CODEC = StringRepresentable.fromEnum(Ailment::values);
    private static final String KEY_PREFIX = "customplayer.ailment.";

    private final PartScale scale;

    Ailment(PartScale scale) {this.scale = scale;}

    public PartScale scale() {return scale;}

    //  ⚠ Only meaningful within one scale; a cross-scale pair is already unrepresentable in the data.
    public boolean worseThan(Ailment other) {
        return other == null || (scale == other.scale && ordinal() > other.ordinal());
    }

    //  ⚠ TWO keying schemes: a BODY grade is one shared word (劳); a SENSE loss differs per part
    //  (盲/聋/哑/痈/呆), so it is keyed per part. See vault.
    public String getTranslationKey(BodyPart part) {
        return scale == PartScale.BODY
                ? KEY_PREFIX + getSerializedName()
                : KEY_PREFIX + part.getSerializedName() + "." + getSerializedName();
    }

    @Override
    public @NotNull String getSerializedName() {return name().toLowerCase();}
}
