package com.unknown.customplayer.attachment.service.body;

import com.unknown.customplayer.attachment.data.body.PartStorage;
import com.unknown.customplayer.custom.enums.body.BodyPart;
import com.unknown.customplayer.registry.ModAttachments;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

//  The only writer of PartStorage. ⚠ Reads take Player (the screen uses them); writes take ServerPlayer.
public final class PartStorageService {

    private PartStorageService() {}

    public static PartStorage get(Player p) {return p.getData(ModAttachments.PART_STORAGE.get());}
    public static ItemStack installed(Player p, BodyPart part) {return get(p).get(part);}

    public static void store(ServerPlayer p, PartStorage storage) {
        p.setData(ModAttachments.PART_STORAGE.get(), storage);
    }

    public static void install(ServerPlayer p, BodyPart part, ItemStack stack) {
        store(p, get(p).with(part, stack));
    }

    //  ⚠ Whether an item MAY go in is the slot's question, not this -- a command or mod writing is trusted.
    public static void clear(ServerPlayer p) {store(p, PartStorage.DEFAULT);}
}
