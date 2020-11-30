package moe.blocks.mod.init;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import moe.blocks.mod.MoeMod;
import moe.blocks.mod.data.conversation.Dialogue;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = MoeMod.ID)
public class MoeDialogues extends JsonReloadListener {
    public static final Map<String, Dialogue> REGISTRY = new HashMap<>();
    private static final Gson GSON = new Gson();
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
