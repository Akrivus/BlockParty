package block_party.scene;

import block_party.entities.Moe;

public interface SceneAction {
    void apply(Moe moe);

    default boolean isComplete(Moe moe) {
        return true;
    }

    default void onComplete(Moe moe) {
    }
}
