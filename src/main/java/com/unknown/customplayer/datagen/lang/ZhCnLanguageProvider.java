package com.unknown.customplayer.datagen.lang;

import com.unknown.customplayer.CustomPlayer;
import com.unknown.customplayer.client.ModKeyMappings;
import com.unknown.customplayer.custom.enums.EnumTranslatable;
import com.unknown.customplayer.custom.enums.body.Ailment;
import com.unknown.customplayer.custom.enums.body.BodyPart;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class ZhCnLanguageProvider extends LanguageProvider {

    public ZhCnLanguageProvider(PackOutput output) {
        super(output, CustomPlayer.MOD_ID, "zh_cn");
    }

    private void add(EnumTranslatable value, String text) {add(value.getTranslationKey(), text);}
    private void add(BodyPart part, Ailment grade, String text) {add(grade.getTranslationKey(part), text);}

    @Override
    protected void addTranslations() {
        //region parts
        add(BodyPart.EYES,                                     "眼");
        add(BodyPart.EARS,                                     "耳");
        add(BodyPart.MOUTH,                                    "口");
        add(BodyPart.NOSE,                                     "鼻");
        add(BodyPart.BRAIN,                                    "脑");
        add(BodyPart.TORSO,                                    "身");
        add(BodyPart.ARM_LEFT,                                 "左臂");
        add(BodyPart.ARM_RIGHT,                                "右臂");
        add(BodyPart.LEG_LEFT,                                 "左腿");
        add(BodyPart.LEG_RIGHT,                                "右腿");
        add(BodyPart.BONE,                                     "骨");
        add(BodyPart.SKIN,                                     "肤");
        add(BodyPart.MUSCLE,                                   "肌");
        add(BodyPart.SINEW,                                    "筋");
        //endregion

        //region body grades -- one word each, shared by all nine body-scale parts
        add("customplayer.ailment.strain",                     "劳");
        add("customplayer.ailment.wound",                      "伤");
        add("customplayer.ailment.crippled",                   "残");
        add("customplayer.ailment.ruined",                     "毁");
        //endregion

        //region sense losses -- a different word on every part, which is why they are keyed per part
        add(BodyPart.EYES, Ailment.LOST,                       "盲");
        add(BodyPart.EARS, Ailment.LOST,                       "聋");
        add(BodyPart.MOUTH, Ailment.LOST,                      "哑");
        add(BodyPart.NOSE, Ailment.LOST,                       "痈");
        add(BodyPart.BRAIN, Ailment.LOST,                      "呆");

        add(BodyPart.EYES, Ailment.DESTROYED,                  "眼部被破坏");
        add(BodyPart.EARS, Ailment.DESTROYED,                  "耳部被破坏");
        add(BodyPart.MOUTH, Ailment.DESTROYED,                 "嘴部被破坏");
        add(BodyPart.NOSE, Ailment.DESTROYED,                  "鼻子被破坏");
        add(BodyPart.BRAIN, Ailment.DESTROYED,                 "脑部被破坏");
        //endregion

        //region degree-one debuffs -- MobEffects, not stored on the part
        add("effect.customplayer.glare",                       "眩光");
        add("effect.customplayer.tinnitus",                    "耳鸣");
        add("effect.customplayer.hoarse",                      "沙哑");
        add("effect.customplayer.congestion",                  "鼻塞");
        add("effect.customplayer.dizzy",                       "眩晕");
        //endregion

        add("death.attack.customplayer.brain_destroyed",       "%1$s 因脑部损毁而死");

        add("customplayer.menu.body",                          "身躯");
        add("customplayer.command.done",                       "已作用于 %s 名玩家");

        add(ModKeyMappings.CATEGORY,                           "自定义玩家");
        add("key.customplayer.open_body",                      "打开身躯");
    }
}
