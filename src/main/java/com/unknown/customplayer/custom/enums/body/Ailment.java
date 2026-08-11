package com.unknown.customplayer.custom.enums.body;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

/**
 * What can be wrong with a part, on either of the two scales.
 *
 * <p>⚠ Ordinal order within a scale IS the severity order, so reordering the constants silently
 * reorders severity. Only {@code worseThan()} may depend on that.
 *
 * @author Alex
 * @since 1.0.0
 */
public enum Ailment implements StringRepresentable {

    //region SENSE -- 眼耳口鼻脑
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

    public boolean worseThan(Ailment other) {
        return other == null || (scale == other.scale && ordinal() > other.ordinal());
    }

    public String getTranslationKey(BodyPart part) {
        return scale == PartScale.BODY
                ? KEY_PREFIX + getSerializedName()
                : KEY_PREFIX + part.getSerializedName() + "." + getSerializedName();
    }

    @Override
    public @NotNull String getSerializedName() {return name().toLowerCase();}
}
