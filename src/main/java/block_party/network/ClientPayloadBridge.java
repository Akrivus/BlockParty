package block_party.network;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;

public final class ClientPayloadBridge {
    private static final String CLIENT_ACTIONS = "block_party.client.ClientPayloadActions";

    private ClientPayloadBridge() {
    }

    public static void handle(String methodName, Class<?> payloadType, Object payload) {
        if (FMLEnvironment.dist != Dist.CLIENT) {
            return;
        }
        try {
            Class<?> actions = Class.forName(CLIENT_ACTIONS);
            actions.getMethod(methodName, payloadType).invoke(null, payload);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to dispatch client payload handler " + methodName, exception);
        }
    }
}
