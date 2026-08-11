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

/**
 * The attachments this mod puts on a player.
 *
 * <p>⚠ The owner-only sync predicate is why this mod needs no packets for data at all. When a reader
 * other than the owner appears, loosen the predicate rather than adding a packet.
 *
 * @author Alex
 * @since 1.0.0
 */
public final class ModAttachments {

    private ModAttachments() {}

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, CustomPlayer.MOD_ID);

    private static final BiPredicate<IAttachmentHolder, ServerPlayer> OWNER_ONLY =
            (holder, viewer) -> holder == viewer;

    public static final Supplier<AttachmentType<BodyPartData>> BODY_PART_DATA = ATTACHMENT_TYPES.register(
            "body_part_data", () -> AttachmentType
                    .builder(() -> BodyPartData.DEFAULT)
                    .serialize(BodyPartData.CODEC)
                    .sync(OWNER_ONLY, BodyPartData.STREAM_CODEC)
                    .build());

    public static final Supplier<AttachmentType<PartStorage>> PART_STORAGE = ATTACHMENT_TYPES.register(
            "part_storage", () -> AttachmentType
                    .builder(() -> PartStorage.DEFAULT)
                    .serialize(PartStorage.CODEC)
                    .build());

    public static final Supplier<AttachmentType<int[]>> BURN_TICKS = ATTACHMENT_TYPES.register(
            "burn_ticks", () -> AttachmentType.<int[]>builder(() -> new int[1]).build());

    public static void register(IEventBus modEventBus) {
        ATTACHMENT_TYPES.register(modEventBus);
    }
}
