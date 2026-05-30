package block_party.scene.actions;

import block_party.entities.Moe;
import block_party.scene.SceneAction;
import java.util.Locale;

public record StatAction(Stat stat, Operation operation, float value) implements SceneAction {
    @Override
    public void apply(Moe moe) {
        float current = this.stat.get(moe);
        float updated = switch (this.operation) {
            case ADD -> current + this.value;
            case SUBTRACT -> current - this.value;
            case SET -> this.value;
        };
        this.stat.set(moe, updated);
    }

    public enum Stat {
        HEALTH {
            @Override
            float get(Moe moe) {
                return moe.getHealth();
            }

            @Override
            void set(Moe moe, float value) {
                moe.setHealth(value);
            }
        },
        FOOD_LEVEL {
            @Override
            float get(Moe moe) {
                return moe.getFoodLevel();
            }

            @Override
            void set(Moe moe, float value) {
                moe.setFoodLevel(value);
            }
        },
        LOYALTY {
            @Override
            float get(Moe moe) {
                return moe.getLoyalty();
            }

            @Override
            void set(Moe moe, float value) {
                moe.setLoyalty(value);
            }
        },
        STRESS {
            @Override
            float get(Moe moe) {
                return moe.getStress();
            }

            @Override
            void set(Moe moe, float value) {
                moe.setStress(value);
            }
        };

        abstract float get(Moe moe);

        abstract void set(Moe moe, float value);
    }

    public enum Operation {
        ADD,
        SUBTRACT,
        SET;

        public static Operation fromValue(String value) {
            try {
                return Operation.valueOf(value.toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException exception) {
                return ADD;
            }
        }
    }
}
