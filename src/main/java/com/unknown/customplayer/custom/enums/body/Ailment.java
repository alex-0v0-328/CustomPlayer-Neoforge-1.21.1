package com.unknown.customplayer.custom.enums.body;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

//  What is wrong with a part, as ONE value on an ordered ladder -- never a set. A leg cannot be both
//  strained and crippled; they are two readings of the same axis, and the worse one is the answer.
//  ⚠ Ordinal order within each scale IS the severity order. worseThan() reads it, and nothing else may.
public enum Ailment implements StringRepresentable {

    //region SENSE -- 眼耳口鼻脑
    //  ⚠ Degree ONE of this scale is deliberately absent. 眩光 / 耳鸣 / 沙哑 / 鼻塞 / 眩晕 are short
    //  timed debuffs, so they are MobEffects: vanilla already ticks them down, syncs them, shows an
    //  icon and clears them on death. Storing a countdown here would re-implement all four badly.
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

    //  ⚠ Only meaningful within one scale. Comparing across them is a bug the caller must not commit;
    //  BodyPart.accepts already makes a cross-scale pair unrepresentable, so it cannot arise from data.
    public boolean worseThan(Ailment other) {
        return other == null || (scale == other.scale && ordinal() > other.ordinal());
    }

    //  ⚠ TWO keying schemes, and the split is the point. A BODY grade is the same word on all nine
    //  parts (劳 is 劳 whether it is a leg or the sinews), so one key serves. A SENSE loss is a
    //  different word on every part -- 盲 / 聋 / 哑 / 痈 / 呆 -- so it is keyed per part.
    public String getTranslationKey(BodyPart part) {
        return scale == PartScale.BODY
                ? KEY_PREFIX + getSerializedName()
                : KEY_PREFIX + part.getSerializedName() + "." + getSerializedName();
    }

    @Override
    public @NotNull String getSerializedName() {return name().toLowerCase();}
}
