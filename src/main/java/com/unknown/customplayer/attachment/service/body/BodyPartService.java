package com.unknown.customplayer.attachment.service.body;

import com.unknown.customplayer.attachment.data.body.BodyPartData;
import com.unknown.customplayer.attachment.data.body.PartMark;
import com.unknown.customplayer.attachment.data.body.PartState;
import com.unknown.customplayer.custom.enums.body.BodyPart;
import com.unknown.customplayer.registry.ModAttachments;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

//  The only writer of BodyPartData. What a mark KEY means is the caller's business; this stores counts.
//  ⚠ Reads take Player, writes take ServerPlayer -- the project-wide split. See vault.
public final class BodyPartService {

    private BodyPartService() {}

    //  ---- read ----
    public static BodyPartData get(Player p) {return p.getData(ModAttachments.BODY_PART_DATA.get());}
    public static PartState state(Player p, BodyPart part) {return get(p).get(part);}
    public static PartMark mark(Player p, BodyPart part, ResourceLocation key) {return state(p, part).mark(key);}

    //  ---- write ----
    public static void store(ServerPlayer p, BodyPartData data) {
        p.setData(ModAttachments.BODY_PART_DATA.get(), data);
    }

    public static void store(ServerPlayer p, BodyPart part, PartState state) {
        store(p, get(p).with(part, state));
    }

    public static void setMark(ServerPlayer p, BodyPart part, ResourceLocation key, long v) {
        store(p, part, state(p, part).with(key, mark(p, part, key).withMark(v)));
    }

    public static void addMark(ServerPlayer p, BodyPart part, ResourceLocation key, long d) {
        setMark(p, part, key, mark(p, part, key).mark() + d);
    }

    public static void setSpeck(ServerPlayer p, BodyPart part, ResourceLocation key, long v) {
        store(p, part, state(p, part).with(key, mark(p, part, key).withSpeck(v)));
    }

    public static void addSpeck(ServerPlayer p, BodyPart part, ResourceLocation key, long d) {
        setSpeck(p, part, key, mark(p, part, key).speck() + d);
    }

    //  Wipes both counts for one key on one part. ⚠ Not "reset the part" -- injuries are InjuryService's.
    public static void clearMark(ServerPlayer p, BodyPart part, ResourceLocation key) {
        store(p, part, state(p, part).with(key, PartMark.DEFAULT));
    }
}
