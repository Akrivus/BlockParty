package block_party.commands;

import block_party.BlockParty;
import block_party.entities.Moe;
import block_party.registry.CustomResources;
import block_party.scene.DiagnosticResult;
import block_party.scene.Scene;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

public final class DebugCommands {
    private static final DynamicCommandExceptionType NOT_MOE =
            new DynamicCommandExceptionType(value -> Component.literal("Target is not a Moe: " + value));
    private static final DynamicCommandExceptionType UNKNOWN_SCENE =
            new DynamicCommandExceptionType(value -> Component.literal("Unknown Block Party scene: " + value));
    private static final DynamicCommandExceptionType INVALID_SCENE =
            new DynamicCommandExceptionType(value -> Component.literal("Invalid scene ID: " + value));

    private DebugCommands() {
    }

    public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("bp")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("content")
                        .then(Commands.literal("validate")
                                .executes(DebugCommands::validateContent)))
                .then(Commands.literal("scene")
                        .then(Commands.literal("diagnose")
                                .then(Commands.argument("target", EntityArgument.entity())
                                        .then(Commands.argument("scene_id", StringArgumentType.word())
                                                .suggests((context, builder) -> SharedSuggestionProvider.suggest(
                                                        CustomResources.SCENES.sceneIds().stream()
                                                                .map(ResourceLocation::toString),
                                                        builder))
                                                .executes(DebugCommands::diagnoseScene))))
                        .then(Commands.literal("play")
                                .then(Commands.argument("target", EntityArgument.entity())
                                        .then(Commands.argument("scene_id", StringArgumentType.word())
                                                .suggests((context, builder) -> SharedSuggestionProvider.suggest(
                                                        CustomResources.SCENES.sceneIds().stream()
                                                                .map(ResourceLocation::toString),
                                                        builder))
                                                .executes(DebugCommands::playScene))))));
    }

    private static int validateContent(CommandContext<CommandSourceStack> context) {
        var issues = CustomResources.SCENES.validationIssues();
        CommandSourceStack source = context.getSource();
        source.sendSuccess(() -> Component.literal("[Block Party Content] scene validation"), false);
        if (issues.isEmpty()) {
            source.sendSuccess(() -> Component.literal("pass: no scene validation issues"), false);
            return 1;
        }
        issues.forEach(issue -> source.sendSuccess(() ->
                Component.literal("fail: " + issue.sceneId() + ": " + issue.message()), false));
        return 0;
    }

    private static int diagnoseScene(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Moe moe = targetMoe(context);
        Scene scene = scene(context);
        bindCommandPlayer(context, moe);
        DiagnosticResult result = scene.diagnose(moe);
        ResourceLocation sceneId = sceneId(context);
        CommandSourceStack source = context.getSource();
        source.sendSuccess(() -> Component.literal("[Block Party Content] " + sceneId), false);
        if (result.passed()) {
            source.sendSuccess(() -> Component.literal("pass: all filters satisfied"), false);
            return 1;
        }
        for (String reason : result.reasons()) {
            source.sendSuccess(() -> Component.literal("fail: " + reason), false);
        }
        return 0;
    }

    private static int playScene(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Moe moe = targetMoe(context);
        Scene scene = scene(context);
        bindCommandPlayer(context, moe);
        moe.sceneManager().setAction(null);
        moe.sceneManager().setActions(scene.getActions());
        ResourceLocation sceneId = sceneId(context);
        context.getSource().sendSuccess(() -> Component.literal("playing scene " + sceneId
                + " on " + moe.getGivenName() + " #" + moe.getDatabaseID()), true);
        return 1;
    }

    private static Moe targetMoe(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Entity entity = EntityArgument.getEntity(context, "target");
        if (entity instanceof Moe moe) {
            return moe;
        }
        throw NOT_MOE.create(entity.getDisplayName().getString());
    }

    private static Scene scene(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ResourceLocation id = sceneId(context);
        Scene scene = CustomResources.SCENES.get(id);
        if (scene == null) {
            throw UNKNOWN_SCENE.create(id);
        }
        return scene;
    }

    private static ResourceLocation sceneId(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        String value = StringArgumentType.getString(context, "scene_id");
        ResourceLocation id = ResourceLocation.tryParse(value);
        if (id == null) {
            throw INVALID_SCENE.create(value);
        }
        return "minecraft".equals(id.getNamespace()) ? BlockParty.source(id.getPath()) : id;
    }

    private static void bindCommandPlayer(CommandContext<CommandSourceStack> context, Moe moe) {
        if (context.getSource().getEntity() instanceof ServerPlayer player) {
            moe.setDialogueTarget(player.getUUID());
        }
    }
}
