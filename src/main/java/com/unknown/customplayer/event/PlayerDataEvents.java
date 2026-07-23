package com.unknown.customplayer.event;

import com.unknown.customplayer.CustomPlayer;
import com.unknown.customplayer.attachment.data.body.BodyPartData;
import com.unknown.customplayer.attachment.data.body.PartStorage;
import com.unknown.customplayer.attachment.service.body.BodyPartService;
import com.unknown.customplayer.attachment.service.body.PartStorageService;
import com.unknown.customplayer.custom.enums.body.BodyPart;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.minecraft.world.level.GameRules;

//  ⚠⚠ THE one place that decides what a body carries across a death -- no copyOnDeath on either attachment.
//  ⚠ keepInventory OFF is a COMPLETE reset (ailments heal, items drop, marks go); aligns downstream. Vault.
@EventBusSubscriber(modid = CustomPlayer.MOD_ID)
public final class PlayerDataEvents {

    private PlayerDataEvents() {}

    //  ⚠ The fresh entity is typed Player during Clone, not ServerPlayer, so this re-narrows internally.
    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
        if (!(event.getEntity() instanceof ServerPlayer fresh)) return;

        BodyPartData carried = BodyPartService.get(event.getOriginal());
        PartStorage installed = PartStorageService.get(event.getOriginal());

        //  A dimension change is not a death: everything comes back exactly as it was.
        if (!event.isWasDeath() || keepInventory(fresh)) {
            BodyPartService.store(fresh, carried);
            PartStorageService.store(fresh, installed);
            return;
        }

        //  A full reset. Installed items are NOT deleted here -- onDrops already dropped them at death.
        BodyPartService.store(fresh, BodyPartData.DEFAULT);
        PartStorageService.store(fresh, PartStorage.DEFAULT);
    }

    //  ⚠ Dropping happens HERE, at death, not in onClone: by clone time the old entity's position and drop
    //  window are gone. They fall with the pack. See vault.
    @SubscribeEvent
    public static void onDrops(LivingDropsEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || keepInventory(player)) return;

        PartStorage storage = PartStorageService.get(player);
        if (storage.isEmpty()) return;

        for (BodyPart part : BodyPart.values()) {
            ItemStack stack = storage.get(part);
            if (stack.isEmpty()) continue;
            event.getDrops().add(new ItemEntity(player.level(),
                    player.getX(), player.getY(), player.getZ(), stack));
        }
    }

    //  ⚠ Read off the SERVER, never the level: a level's own gameRules can differ per dimension.
    private static boolean keepInventory(ServerPlayer player) {
        return player.getServer() != null
                && player.getServer().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY);
    }
}
