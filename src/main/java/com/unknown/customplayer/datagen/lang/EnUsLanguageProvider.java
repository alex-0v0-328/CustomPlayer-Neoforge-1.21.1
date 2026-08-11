package com.unknown.customplayer.datagen.lang;

import com.unknown.customplayer.CustomPlayer;
import com.unknown.customplayer.client.ModKeyMappings;
import com.unknown.customplayer.custom.enums.EnumTranslatable;
import com.unknown.customplayer.custom.enums.body.Ailment;
import com.unknown.customplayer.custom.enums.body.BodyPart;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

/**
 * The English strings, written as an aligned table.
 *
 * <p>⚠ Both providers keep the same key order and the same value column, so the two files diff
 * against each other line for line. They take the enum constant, never its key string.
 *
 * @author Alex
 * @since 1.0.0
 * @see ZhCnLanguageProvider
 */
public class EnUsLanguageProvider extends LanguageProvider {

    public EnUsLanguageProvider(PackOutput output) {
        super(output, CustomPlayer.MOD_ID, "en_us");
    }

    private void add(EnumTranslatable value, String text) {add(value.getTranslationKey(), text);}
    private void add(BodyPart part, Ailment grade, String text) {add(grade.getTranslationKey(part), text);}

    @Override
    protected void addTranslations() {
        //region parts
        add(BodyPart.EYES,                                     "Eyes");
        add(BodyPart.EARS,                                     "Ears");
        add(BodyPart.MOUTH,                                    "Mouth");
        add(BodyPart.NOSE,                                     "Nose");
        add(BodyPart.BRAIN,                                    "Brain");
        add(BodyPart.TORSO,                                    "Torso");
        add(BodyPart.ARM_LEFT,                                 "Left Arm");
        add(BodyPart.ARM_RIGHT,                                "Right Arm");
        add(BodyPart.LEG_LEFT,                                 "Left Leg");
        add(BodyPart.LEG_RIGHT,                                "Right Leg");
        add(BodyPart.BONE,                                     "Bones");
        add(BodyPart.SKIN,                                     "Skin");
        add(BodyPart.MUSCLE,                                   "Muscle");
        add(BodyPart.SINEW,                                    "Sinews");
        //endregion

        //region body grades -- one word each, shared by all nine body-scale parts
        add("customplayer.ailment.strain",                     "Strained");
        add("customplayer.ailment.wound",                      "Wounded");
        add("customplayer.ailment.crippled",                   "Crippled");
        add("customplayer.ailment.ruined",                     "Ruined");
        //endregion

        //region sense losses -- a different word on every part, which is why they are keyed per part
        add(BodyPart.EYES, Ailment.LOST,                       "Blind");
        add(BodyPart.EARS, Ailment.LOST,                       "Deaf");
        add(BodyPart.MOUTH, Ailment.LOST,                      "Mute");
        add(BodyPart.NOSE, Ailment.LOST,                       "Anosmic");
        add(BodyPart.BRAIN, Ailment.LOST,                      "Senseless");

        add(BodyPart.EYES, Ailment.DESTROYED,                  "Eyes Destroyed");
        add(BodyPart.EARS, Ailment.DESTROYED,                  "Ears Destroyed");
        add(BodyPart.MOUTH, Ailment.DESTROYED,                 "Mouth Destroyed");
        add(BodyPart.NOSE, Ailment.DESTROYED,                  "Nose Destroyed");
        add(BodyPart.BRAIN, Ailment.DESTROYED,                 "Brain Destroyed");
        //endregion

        //region degree-one debuffs -- MobEffects, not stored on the part
        add("effect.customplayer.glare",                       "Dazzled");
        add("effect.customplayer.tinnitus",                    "Tinnitus");
        add("effect.customplayer.hoarse",                      "Hoarse");
        add("effect.customplayer.congestion",                  "Congested");
        add("effect.customplayer.dizzy",                       "Dizzy");
        //endregion

        add("death.attack.customplayer.brain_destroyed",       "%1$s died of brain damage");

        add("customplayer.menu.body",                          "Body");
        add("customplayer.command.done",                       "Applied to %s player(s)");

        add(ModKeyMappings.CATEGORY,                           "CustomPlayer");
        add("key.customplayer.open_body",                      "Open Body");
    }
}
