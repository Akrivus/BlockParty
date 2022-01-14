package block_party.scene;

import block_party.utils.NBT;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

public class Cookies {
    private final List<String> cookies = Lists.newArrayList();

    public Cookies() { }

    public Cookies(CompoundTag compound) {
        compound.getList("Cookies", NBT.STRING).forEach((cookie) -> this.cookies.add(cookie.getAsString()));
    }

    public CompoundTag save(CompoundTag compound) {
        ListTag list = new ListTag();
        this.cookies.forEach((cookie) -> list.add(StringTag.valueOf(cookie)));
        compound.put("Cookies", list);
        return compound;
    }

    public CompoundTag save() {
        return this.save(new CompoundTag());
    }

    public boolean has(String cookie) {
        return this.cookies.contains(cookie);
    }

    public void add(String cookie) {
        this.cookies.add(cookie);
    }

    public void eat(String cookie) {
        this.cookies.remove(cookie);
    }
}
