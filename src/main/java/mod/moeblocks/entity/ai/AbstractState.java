package mod.moeblocks.entity.ai;

import mod.moeblocks.entity.MoeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;

public abstract class AbstractState implements IState {
    protected MoeEntity moe;

    public boolean onDamage(DamageSource cause, float amount) {
        return true;
    }

    public boolean onInteract(PlayerEntity player, ItemStack stack, Hand hand) {
        return false;
    }

    @Override
    public void start(MoeEntity moe) {
        this.moe = moe;
        this.start();
    }

    @Override
    public IState stop(IState swap) {
        this.stop();
        swap.start(this.moe);
        return swap;
    }

    public void setMoe(MoeEntity moe) {
        this.moe = moe;
    }
}
