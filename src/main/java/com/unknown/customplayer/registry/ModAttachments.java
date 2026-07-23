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
//  ⚠ .sync() is why this mod has no packets; an id mirrors its record class (body_part_data). See vault.
public final class ModAttachments {

    private ModAttachments() {}

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, CustomPlayer.MOD_ID);

    //  Without this, NeoForge syncs to everyone who can see the holder. Loosen only for a reader. See vault.
    private static final BiPredicate<IAttachmentHolder, ServerPlayer> OWNER_ONLY =
            (holder, viewer) -> holder == viewer;

    //  ⚠⚠ NO copyOnDeath(): what survives a death is decided in ONE place, the clone handler (marks carry,
    //  injuries heal). A copy and the handler cannot both be the last write. See vault.
    public static final Supplier<AttachmentType<BodyPartData>> BODY_PART_DATA = ATTACHMENT_TYPES.register(
            "body_part_data", () -> AttachmentType
                    .builder(() -> BodyPartData.DEFAULT)
                    .serialize(BodyPartData.CODEC)
                    .sync(OWNER_ONLY, BodyPartData.STREAM_CODEC)
                    .build());

    //  ⚠⚠ Serialized, NOT synced -- no .sync() and PartStorage has no STREAM_CODEC. The menu is how it shows.
    public static final Supplier<AttachmentType<PartStorage>> PART_STORAGE = ATTACHMENT_TYPES.register(
            "part_storage", () -> AttachmentType
                    .builder(() -> PartStorage.DEFAULT)
                    .serialize(PartStorage.CODEC)
                    .build());

    //  Seconds alight, counted up. ⚠ Neither synced NOR serialized: resets when the fire goes out, and a
    //  relog is indistinguishable. The one thing MUTATED IN PLACE, because nothing watches it.
    public static final Supplier<AttachmentType<int[]>> BURN_TICKS = ATTACHMENT_TYPES.register(
            "burn_ticks", () -> AttachmentType.<int[]>builder(() -> new int[1]).build());

    public static void register(IEventBus modEventBus) {
        ATTACHMENT_TYPES.register(modEventBus);
    }
}
