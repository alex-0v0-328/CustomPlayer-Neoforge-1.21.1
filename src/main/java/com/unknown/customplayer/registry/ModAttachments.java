package com.unknown.customplayer.registry;

import com.unknown.customplayer.CustomPlayer;
import com.unknown.customplayer.attachment.data.body.BodyPartData;
import com.unknown.customplayer.attachment.data.body.PartStorage;
import java.util.function.BiPredicate;
import java.util.function.Supplier;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

//  Write only through attachment/service.
//  ⚠ .sync() is why this mod has no packets: NeoForge pushes on setData and re-sends the whole set on
//  login, respawn and dimension change. Do not add a payload for player data.
//  ⚠ An id mirrors its record class (body_part_data <-> BodyPartData), never the bare domain word.
public final class ModAttachments {

    private ModAttachments() {}

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, CustomPlayer.MOD_ID);

    //  Without this, NeoForge syncs to everyone who can see the holder. Loosen it only when some other
    //  player is meant to read the body -- a healer, say -- and then say so here.
    private static final BiPredicate<IAttachmentHolder, ServerPlayer> OWNER_ONLY =
            (holder, viewer) -> holder == viewer;

    //  ⚠⚠ NO copyOnDeath(), deliberately. What survives a death is decided in ONE place, the clone
    //  handler -- and here it is not even a plain copy: marks carry over while injuries heal. With
    //  copyOnDeath the copy and the handler would both be writing, and which one lands last is exactly
    //  the kind of thing that works until it does not.
    public static final Supplier<AttachmentType<BodyPartData>> BODY_PART_DATA = ATTACHMENT_TYPES.register(
            "body_part_data", () -> AttachmentType
                    .builder(() -> BodyPartData.DEFAULT)
                    .serialize(BodyPartData.CODEC)
                    .sync(OWNER_ONLY, BodyPartData.STREAM_CODEC)
                    .build());

    //  ⚠⚠ Serialized, NOT synced -- no .sync() line at all, and PartStorage has no STREAM_CODEC to give
    //  one. The menu is how the client sees it, over vanilla's slot channel.
    public static final Supplier<AttachmentType<PartStorage>> PART_STORAGE = ATTACHMENT_TYPES.register(
            "part_storage", () -> AttachmentType
                    .builder(() -> PartStorage.DEFAULT)
                    .serialize(PartStorage.CODEC)
                    .build());

    //  Seconds alight, counted up rather than down. ⚠ Neither synced NOR serialized, deliberately: it
    //  resets the moment the fire goes out, and a relog is indistinguishable from that. It is also the
    //  one thing here MUTATED IN PLACE, because nothing watches it.
    public static final Supplier<AttachmentType<int[]>> BURN_TICKS = ATTACHMENT_TYPES.register(
            "burn_ticks", () -> AttachmentType.<int[]>builder(() -> new int[1]).build());

    public static void register(IEventBus modEventBus) {
        ATTACHMENT_TYPES.register(modEventBus);
    }
}
