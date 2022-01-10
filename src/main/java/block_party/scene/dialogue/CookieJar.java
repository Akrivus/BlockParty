package block_party.scene.dialogue;

import block_party.utils.NBT;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

public class CookieJar {
    private final List<String> cookies = Lists.newArrayList();

    public CookieJar(CompoundTag compound) {
        compound.getList("Cookies", NBT.STRING).forEach((cookie) -> cookies.add(cookie.getAsString()));
    }

    public CompoundTag save() {
        ListTag list = new ListTag();
        this.cookies.forEach((cookie) -> list.add(StringTag.valueOf(cookie)));
        CompoundTag compound = new CompoundTag();
        compound.put("Cookies", list);
        return compound;
    }
}
