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

//  ⚠⚠ THE one place that decides what a body carries across a death. There is deliberately no
//  copyOnDeath() on either attachment: a copy and a handler cannot both be the last write.
//  ⚠ keepInventory OFF is a COMPLETE reset -- ailments heal, installed items drop, marks go too. The
//  reason is not internal: the dependent mod's keepInventory-off branch runs its own full reset, and a
//  body that kept its marks would tell a different story about the same death.
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

        //  A full reset. The installed items are NOT deleted here -- onDrops already put them on the
        //  ground at the moment of death, which is a different instant, not a second writer.
        BodyPartService.store(fresh, BodyPartData.DEFAULT);
        PartStorageService.store(fresh, PartStorage.DEFAULT);
    }

    //  ⚠ Dropping has to happen HERE, at the death, not in onClone: by clone time the old entity's
    //  position and the drop window are both gone. They fall with the pack, because the body obeys the
    //  same rule the pack does.
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
