package block_party.scene.actions;

import block_party.entities.Moe;
import block_party.scene.SceneAction;
import block_party.scene.SceneVariables;
import block_party.scene.data.Cookies;
import java.util.Locale;

public record CookieAction(Operation operation, String name, String value) implements SceneAction {
    @Override
    public void apply(Moe moe) {
        Cookies cookies = SceneVariables.get(moe.level()).cookies(moe.getDatabaseID());
        this.operation.apply(cookies, this.name, this.value);
    }

    public enum Operation {
        SET {
            @Override
            void apply(Cookies cookies, String name, String value) {
                cookies.set(name, value);
            }
        },
        DELETE {
            @Override
            void apply(Cookies cookies, String name, String value) {
                cookies.delete(name);
            }
        };

        abstract void apply(Cookies cookies, String name, String value);

        public static Operation fromValue(String value) {
            try {
                return Operation.valueOf(value.toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException exception) {
                return SET;
            }
        }
    }
}
