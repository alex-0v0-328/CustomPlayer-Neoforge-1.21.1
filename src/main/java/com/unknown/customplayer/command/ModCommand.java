package com.unknown.customplayer.command;

import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.unknown.customplayer.CustomPlayer;
import com.unknown.customplayer.attachment.service.body.BodyPartService;
import com.unknown.customplayer.attachment.service.body.InjuryService;
import com.unknown.customplayer.attachment.service.body.PartStorageService;
import com.unknown.customplayer.custom.enums.body.Ailment;
import com.unknown.customplayer.custom.enums.body.BodyPart;
import com.mojang.brigadier.Command;
import java.util.Collection;
import java.util.List;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

//  /customplayer -- this mod's own root. ⚠ A dependent mod adds ITS own root; it never hangs a branch
//  here, and this mod never hangs one on somebody else's.
//  Verbs follow one rule so a new leaf is never a guess: raw count -> set/add/sub, set membership ->
//  apply/heal/clear, one-shot -> bare.
@EventBusSubscriber(modid = CustomPlayer.MOD_ID)
public final class ModCommand {

    private ModCommand() {}

    private static final String ROOT = "customplayer";
    private static final String ALIAS = "cpl";

    private static final String ARG_TARGETS = "targets";
    private static final String ARG_PART = "part";
    private static final String ARG_INJURY = "injury";
    private static final String ARG_KEY = "key";
    private static final String ARG_VALUE = "value";

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        //  ⚠ The alias shares the SAME node, so the two can never drift apart.
        var root = event.getDispatcher().register(Commands.literal(ROOT)
                .requires(source -> source.hasPermission(2))
                .then(ailment())
                .then(mark())
                .then(uninstall()));

        event.getDispatcher().register(Commands.literal(ALIAS)
                .requires(source -> source.hasPermission(2))
                .redirect(root));
    }

    //region ailment
    //  /customplayer ailment <part> <grade> [targets]  ·  ... <part> clear  ·  ... clear
    private static ArgumentBuilder<CommandSourceStack, ?> ailment() {
        var node = Commands.literal("ailment");
        for (BodyPart part : BodyPart.values()) {
            var partNode = Commands.literal(part.getSerializedName());
            for (Ailment grade : Ailment.values()) {
                //  ⚠ Only the pairs that can exist are even offered -- a cross-scale pair is not a
                //  refusal to explain, it is a branch that was never built. A leg has no "blind".
                if (!part.accepts(grade)) continue;
                partNode.then(withTargets(Commands.literal(grade.getSerializedName()),
                        context -> apply(context, p -> InjuryService.apply(p, part, grade))));
            }
            //  ⚠ A bare grade SETS, it does not worsen: an operator asking for 劳 on a ruined leg means
            //  劳. worsen() is for injury rules, which fire without anyone deciding.
            partNode.then(withTargets(Commands.literal("clear"),
                    context -> apply(context, p -> InjuryService.heal(p, part))));
            node.then(partNode);
        }
        return node.then(withTargets(Commands.literal("clear"),
                context -> apply(context, InjuryService::healAll)));
    }
    //endregion

    //region mark
    //  ⚠ The key is a free ResourceLocation, not an enum: this mod does not know what marks exist, and
    //  a dependent must be able to use its own without a code change here.
    private static ArgumentBuilder<CommandSourceStack, ?> mark() {
        var node = Commands.literal("mark");
        for (BodyPart part : BodyPart.values()) {
            node.then(Commands.literal(part.getSerializedName())
                    .then(Commands.argument(ARG_KEY, StringArgumentType.string())
                            .then(countNode("mark", part, true))
                            .then(countNode("speck", part, false))));
        }
        return node;
    }

    private static ArgumentBuilder<CommandSourceStack, ?> countNode(
            String literal, BodyPart part, boolean isMark) {
        return Commands.literal(literal)
                .then(countLeaf("set", part, isMark, false))
                .then(countLeaf("add", part, isMark, true))
                .then(countLeaf("sub", part, isMark, true));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> countLeaf(
            String literal, BodyPart part, boolean isMark, boolean relative) {
        boolean negate = "sub".equals(literal);
        return Commands.literal(literal).then(withTargets(
                Commands.argument(ARG_VALUE, LongArgumentType.longArg()),
                context -> {
                    long raw = LongArgumentType.getLong(context, ARG_VALUE);
                    long value = negate ? -raw : raw;
                    ResourceLocation key = ResourceLocation.parse(
                            StringArgumentType.getString(context, ARG_KEY));
                    return apply(context, player -> {
                        if (isMark && relative) BodyPartService.addMark(player, part, key, value);
                        else if (isMark) BodyPartService.setMark(player, part, key, value);
                        else if (relative) BodyPartService.addSpeck(player, part, key, value);
                        else BodyPartService.setSpeck(player, part, key, value);
                    });
                }));
    }
    //endregion

    //  Empties every installed slot. ⚠ Deletes what was in them -- there is no "drop it at your feet"
    //  concept in this mod, and inventing one for an operator command would be the wrong place to.
    private static ArgumentBuilder<CommandSourceStack, ?> uninstall() {
        return withTargets(Commands.literal("uninstall"),
                context -> apply(context, PartStorageService::clear));
    }

    //region plumbing
    //  Every leaf works on self or [targets]. ⚠ The optional-targets node hangs off the LEAF, so a
    //  bare command means "me" without an extra branch to keep in step.
    private static ArgumentBuilder<CommandSourceStack, ?> withTargets(
            ArgumentBuilder<CommandSourceStack, ?> node, Command<CommandSourceStack> action) {
        return node.executes(action).then(
                Commands.argument(ARG_TARGETS, EntityArgument.players()).executes(action));
    }

    private static int apply(CommandContext<CommandSourceStack> context, PlayerOperation operation) {
        Collection<ServerPlayer> targets = targets(context);
        for (ServerPlayer player : targets) operation.apply(player);
        context.getSource().sendSuccess(
                () -> Component.translatable("customplayer.command.done", targets.size()), true);
        return targets.size();
    }

    private static Collection<ServerPlayer> targets(CommandContext<CommandSourceStack> context) {
        try {
            return EntityArgument.getPlayers(context, ARG_TARGETS);
        } catch (Exception ignored) {
            ServerPlayer self = context.getSource().getPlayer();
            return self == null ? List.of() : List.of(self);
        }
    }

    //  ⚠ Spelled out, not PlayerOp -- this project does not abbreviate interface names.
    @FunctionalInterface
    private interface PlayerOperation {
        void apply(ServerPlayer player);
    }
    //endregion
}
