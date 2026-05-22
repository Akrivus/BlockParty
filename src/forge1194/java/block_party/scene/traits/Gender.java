package block_party.scene.traits;

import block_party.db.BlockPartyDB;
import block_party.entities.BlockPartyNPC;
import block_party.registry.resources.Names;
import block_party.scene.ITrait;
import block_party.utils.Trans;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.stream.Collectors;

public enum Gender implements ITrait<Gender> {
    MALE("kun"),
    FEMALE("chan"),
    NONBINARY("kun");

    private final String honorific;

    Gender(String honorific) {
        this.honorific  = honorific;
    }

    @Override
    public boolean isSharedWith(BlockPartyNPC npc) {
        return npc.getGender() == this;
    }

    @Override
    public String getValue() {
        return this.name();
    }

    @Override
    public Gender fromValue(String key) {
        try {
            return Gender.valueOf(key.toUpperCase());
        } catch (IllegalArgumentException e) {
            return this;
        }
    }

    public String getPronounAsSubject() {
        return Trans.late("trait.block_party.gender." + this.name().toLowerCase() + ".nominative");
    }

    public String getPronounAsObject() {
        return Trans.late("trait.block_party.gender." + this.name().toLowerCase() + ".accusative");
    }

    public String getPossessivePronoun() {
        return Trans.late("trait.block_party.gender." + this.name().toLowerCase() + ".possessive");
    }

    public String getReflexivePronoun() {
        return Trans.late("trait.block_party.gender." + this.name().toLowerCase() + ".reflexive");
    }

    public String getHonorific() {
        return this.honorific;
    }

    public String getUniqueName(Level level) {
        List<String> names = this.getUnclaimedNames(level);
        if (names.isEmpty()) { return null; }
        String name = names.get(level.random.nextInt(names.size()));
        BlockPartyDB.get(level).names.add(name);
        return name;
    }

    protected List<String> getUnclaimedNames(Level level) {
        return Names.get(this).stream().filter((name) -> !BlockPartyDB.get(level).names.contains(name)).collect(Collectors.toList());
    }
}
