package moe.blocks.mod.init;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.*;
import moe.blocks.mod.MoeMod;
import moe.blocks.mod.data.conversation.Dialogue;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.*;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Unit;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mod.EventBusSubscriber(modid = MoeMod.ID)
public class MoeDialogues extends JsonReloadListener {
    private static final Gson GSON = new Gson();
    public static final Map<String, Dialogue> REGISTRY = new HashMap<>();
    public static MoeDialogues INSTANCE;

    public MoeDialogues() {
        super(GSON, "convos");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> objects, IResourceManager manager, IProfiler profiler) {
        MoeDialogues.REGISTRY.clear();
        for (Map.Entry<ResourceLocation, JsonElement> entry : objects.entrySet()) {
            JsonArray array = entry.getValue().getAsJsonArray();
            for (JsonElement element : array) {
                Dialogue dialogue = GSON.fromJson(element, Dialogue.class);
                MoeDialogues.REGISTRY.put(dialogue.getKey(), dialogue);
            }
        }
    }

    @SubscribeEvent
    public static void register(AddReloadListenerEvent e) {
        e.addListener(MoeDialogues.INSTANCE = new MoeDialogues());
    }
}
