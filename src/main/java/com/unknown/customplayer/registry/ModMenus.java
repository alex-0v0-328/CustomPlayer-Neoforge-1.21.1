package com.unknown.customplayer.registry;

import com.unknown.customplayer.CustomPlayer;
import com.unknown.customplayer.menu.BodyPartMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModMenus {

    private ModMenus() {}

    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, CustomPlayer.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<BodyPartMenu>> BODY_PART_MENU =
            MENUS.register("body_part_menu", () -> IMenuTypeExtension.create(
                    (id, inventory, buf) -> new BodyPartMenu(id, inventory)));

    public static void register(IEventBus modEventBus) {
        MENUS.register(modEventBus);
    }
}
