package block_party.scene.filters.traits;

import block_party.db.BlockPartyDB;
import block_party.entities.BlockPartyNPC;
import block_party.scene.ITrait;
import block_party.utils.Trans;
import net.minecraft.world.level.Level;
import org.apache.commons.compress.utils.Lists;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Gender implements ITrait<Gender> {
    MALE("kun"),
    FEMALE("chan"),
    NONBINARY("kun");

    private final String nominative;
    private final String accusative;
    private final String possessive;
    private final String reflexive;
    private final String honorific;
    private final List<String> names;

    Gender(String honorific) {
        String prefix = "trait.block_party.gender." + this.name().toLowerCase() + ".";
        this.nominative = Trans.late(prefix + "nominative");
        this.accusative = Trans.late(prefix + "accusative");
        this.possessive = Trans.late(prefix + "possessive");
        this.reflexive  = Trans.late(prefix + "reflexive");
        this.honorific  = honorific;
        this.names = this.readNamesFromJar();
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
        return this.nominative;
    }

    public String getPronounAsObject() {
        return this.accusative;
    }

    public String getPossessivePronoun() {
        return this.possessive;
    }

    public String getReflexivePronoun() {
        return this.reflexive;
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
        return this.names.stream().filter((name) -> !BlockPartyDB.get(level).names.contains(name)).collect(Collectors.toList());
    }

    protected List<String> readNamesFromJar() {
        String path = String.format("data/block_party/names/%s.txt", this.name().toLowerCase());
        InputStream stream = Gender.class.getResourceAsStream(path);
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        return reader.lines().collect(Collectors.toList());
    }
}
