package moe.blocks.mod.entity.partial;

import moe.blocks.mod.client.Animations;
import moe.blocks.mod.data.Yearbooks;
import moe.blocks.mod.data.dating.Interactions;
import moe.blocks.mod.data.dating.Relationship;
import moe.blocks.mod.data.dating.Tropes;
import moe.blocks.mod.data.yearbook.Book;
import moe.blocks.mod.entity.ai.automata.State;
import moe.blocks.mod.entity.ai.automata.States;
import moe.blocks.mod.entity.ai.automata.state.Emotions;
import moe.blocks.mod.init.MoeItems;
import moe.blocks.mod.init.MoeMessages;
import moe.blocks.mod.message.SOpenYearbook;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public abstract class CharacterEntity extends InteractEntity {
    protected final List<Relationship> relationships = new ArrayList<>();
    public boolean isInYearbook = false;
    protected String givenName;

    protected CharacterEntity(EntityType<? extends CreatureEntity> type, World world) {
        super(type, world);
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        ListNBT relationships = new ListNBT();
        this.relationships.forEach(relationship -> relationships.add(relationship.write(new CompoundNBT())));
        compound.put("Relationships", relationships);
        compound.putString("GivenName", this.getGivenName());
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        ListNBT relationships = compound.getList("Relationships", 10);
        relationships.forEach(relationship -> this.relationships.add(new Relationship(relationship)));
        this.givenName = compound.getString("GivenName");
    }

    @Override
    public void livingTick() {
        super.livingTick();
        this.world.getProfiler().startSection("relationships");
        this.relationships.forEach(relationship -> relationship.tick());
        this.world.getProfiler().endSection();
    }

    @Override
    public void registerStates(HashMap<States, State> states) {
        states.put(States.REACTION, null);
    }

    @Override
    public ActionResultType func_230254_b_(PlayerEntity player, Hand hand) {
        if (this.isLocal() && player.getHeldItem(hand).getItem() == MoeItems.YEARBOOK.get()) {
            Book book = Yearbooks.getBook(player);
            book.setPageIgnorantly(this, player.getUniqueID());
            MoeMessages.send(new SOpenYearbook(book, this.getUniqueID()));
            return ActionResultType.SUCCESS;
        } else {
            return super.func_230254_b_(player, hand);
        }
    }

    @Override
    public ActionResultType onInteract(PlayerEntity player, ItemStack stack, Hand hand) {
        if (this.isRemote() || hand == Hand.OFF_HAND) { return ActionResultType.PASS; }
        Relationship relationship = this.getRelationshipWith(player);
        this.setNextState(States.REACTION, relationship.getReaction(Interactions.HEADPAT));
        if (relationship.can(Relationship.Actions.FOLLOW)) {
            this.setFollowTarget(player.equals(this.getFollowTarget()) ? null : player);
        }
        return ActionResultType.SUCCESS;
    }

    public Relationship getRelationshipWith(PlayerEntity player) {
        return this.getRelationshipWith(player.getUniqueID());
    }

    public Relationship getRelationshipWith(UUID uuid) {
        return this.relationships.stream().filter(relationship -> relationship.isUUID(uuid)).findFirst().orElse(new Relationship(uuid));
    }

    public String getGivenName() {
        if (this.givenName != null) { return this.givenName; }
        return this.givenName = this.getGender().getName();
    }

    public Gender getGender() {
        return Gender.FEMININE;
    }

    public void setYearbookPage(CompoundNBT compound, UUID uuid) {
        compound.putString("GivenName", this.getGivenName());
        compound.putString("FamilyName", this.getFamilyName());
        compound.putString("Animation", Animations.IDLE.name());
        compound.putString("Emotion", Emotions.NORMAL.name());
        compound.putFloat("Health", this.getHealth());
        compound.putFloat("Hunger", this.getHunger());
        compound.putFloat("Stress", this.getStress());
        compound.putFloat("Love", this.getRelationshipWith(uuid).getLove());
        compound.putString("Dere", this.getDere().name());
        compound.putString("Status", this.getRelationshipStatus().name());
        compound.putString("BloodType", this.getBloodType().name());
        compound.putInt("AgeInYears", this.getAgeInYears());
    }

    public Relationship.Status getRelationshipStatus() {
        if (this.getHealth() <= 0.0F) { return Relationship.Status.DEAD; }
        for (Relationship r : this.relationships) { if (r.getPhase() == Relationship.Phases.CONFESSION) { return Relationship.Status.TAKEN; } }
        return Relationship.Status.SINGLE;
    }

    @Override
    public ITextComponent getName() {
        return new TranslationTextComponent("entity.moeblocks.generic", this.getFamilyName(), this.getHonorific());
    }

    @Override
    public boolean hasCustomName() {
        return true;
    }

    public String getFamilyName() {
        return "Chara";
    }

    public String getHonorific() {
        return this.getGender() == Gender.FEMININE ? "chan" : "kun";
    }

    public void syncYearbooks() {
        if (this.isLocal()) { Yearbooks.sync(this); }
    }

    public Tropes getTrope() {
        return Tropes.get(this.getDere(), this.getBloodType());
    }

    @Override
    public void onDeath(DamageSource cause) {
        this.syncYearbooks();
        super.onDeath(cause);
    }

    @Override
    public boolean getAlwaysRenderNameTagForRender() {
        return true;
    }

    public String getFullName() {
        return String.format("%s %s", this.getFamilyName(), this.getGivenName());
    }

    public BlockState getBlockData() {
        return Blocks.AIR.getDefaultState();
    }

    public boolean isDead() {
        return this.getRelationshipStatus() == Relationship.Status.DEAD;
    }

    public enum Gender {
        MASCULINE("Akemi", "Aki", "Akifumi", "Akihisa", "Akihito", "Akinari", "Akitoshi", "Akiya", "Akiyoshi", "Akiyuki", "Arashi", "Arihiro", "Arinaga", "Asahiko", "Asao", "Asayama", "Atomu", "Atsuji", "Azuma", "Banri", "Bunji", "Chikao", "Chikara", "Choei", "Choki", "Daichi", "Daihachi", "Daijiro", "Daikichi", "Daisaku", "Daishin", "Daisuke", "Daizo", "Eiichi", "Eiji", "Eiken", "Eikichi", "Einosuke", "Eishun", "Eita", "Eizo", "Etsuji", "Fumio", "Fusao", "Gakuto", "Genjiro", "Genta", "Gentaro", "Gin", "Go", "Goichi", "Hakaru", "Haruaki", "Haruhisa", "Harunobu", "Harunori", "Haruto", "Hayanari", "Hayao", "Heizo", "Hideharu", "Hidehiko", "Hidehisa", "Hidemaro", "Hidemasa", "Hideo", "Hideomi", "Hideto", "Hideya", "Hideyo", "Hiro", "Hiroaki", "Hirokazu", "Hirokuni", "Hiromori", "Hironari", "Hirooki", "Hirotaka", "Hirotami", "Hirotoki", "Hiroya", "Hiroyasu", "Hisahito", "Hisaichi", "Hisaki", "Hisanobu", "Hisao", "Hisato", "Hisayasu", "Hisayuki", "Hokuto", "Hozumi", "Iehira", "Iemasa", "Iemon", "Iesada", "Ikki", "Inasa", "Isami", "Isao", "Issei", "Itsuo", "Itsuro", "Jiichiro", "Jinpachi", "Jo", "Joji", "Jokichi", "Junpei", "Kagenori", "Kaichi", "Kaisei", "Kamon", "Kanehiro", "Kanesuke", "Kanji", "Katsuki", "Katsuo", "Kazuharu", "Kazuhito", "Kazuki", "Kazuma", "Kazuo", "Kazutoki", "Kazuya", "Keiichi", "Keijiro", "Keiju", "Keisuke", "Keizo", "Ken", "Kenro", "Kenta", "Kentaro", "Kihachi", "Kiichiro", "Kimio", "Kinjiro", "Kinsuke", "Kisaburo", "Kisaku", "Kiyofumi", "Kiyokazu", "Kiyoshi", "Kiyoto", "Konosuke", "Koshiro", "Kozaburo", "Kumataro", "Kunihiko", "Kunitake", "Kyogo", "Kyoji", "Kyosuke", "Mahiro", "Makio", "Mamoru", "Manabu", "Mareo", "Masabumi", "Masahiro", "Masaji", "Masakata", "Masakazu", "Masakuni", "Masanaga", "Masanari", "Masanobu", "Masao", "Masatake", "Masatane", "Masatomi", "Masatomo", "Masayuki", "Masazumi", "Masujiro", "Matsuki", "Matsuo", "Michio", "Mikito", "Mineichi", "Mitsuaki", "Mitsugi", "Mitsugu", "Mitsuo", "Mochiaki", "Morihiko", "Morio", "Moritaka", "Motojiro", "Motokazu", "Motoki", "Motomu", "Motonobu", "Mototada", "Motoyasu", "Motoyuki", "Motozane", "Mukuro", "Munehiro", "Munehisa", "Munetaka", "Musashi", "Nagaharu", "Naganori", "Nagatoki", "Nagayasu", "Namio", "Nankichi", "Naohiko", "Naohiro", "Naohito", "Naotake", "Naoto", "Naoya", "Naritaka", "Nariyasu", "Nobumasa", "Nobunari", "Nobuo", "Nobusada", "Nobusuke", "Nobutada", "Nobuyasu", "Noriaki", "Norifumi", "Norihide", "Norihiko", "Norihiro", "Norihito", "Norimoto", "Norio", "Noriyasu", "Nozomu", "Reizo", "Rentaro", "Rikichi", "Rikio", "Rikiya", "Rinsho", "Risaburo", "Rokuro", "Ryohei", "Ryoichi", "Ryoji", "Ryoki", "Ryota", "Ryotaro", "Ryozo", "Ryu", "Ryuma", "Ryusaku", "Ryusei", "Ryushi", "Ryusuke", "Ryuta", "Ryutaro", "Ryuya", "Sachio", "Sadaharu", "Sadahiro", "Saiichi", "Sanji", "Sanshiro", "Satoshi", "Seigo", "Seiho", "Seijiro", "Seiki", "Seimei", "Seiya", "Sendai", "Setsuji", "Shigemi", "Shigeo", "Shigeto", "Shin", "Shingo", "Shinjo", "Shinta", "Shintaro", "Shoma", "Shota", "Shu", "Shuko", "Shungo", "Shunki", "Shunpei", "Shunsui", "Shunsuke", "Shunta", "Sogen", "Soichiro", "Sosuke", "Sota", "Suenaga", "Suguru", "Sukenobu", "Sukeyuki", "Sumihiro", "Sunao", "Susumu", "Tadaaki", "Tadahiko", "Tadahiro", "Tadamasa", "Tadami", "Tadamori", "Tadanobu", "Tadaoki", "Tadataka", "Tadateru", "Tadayo", "Taichi", "Taichiro", "Taiga", "Taiichi", "Taiji", "Taisei", "Taishin", "Taiyo", "Taizo", "Takaaki", "Takafumi", "Takahide", "Takahira", "Takahiro", "Takaji", "Takaki", "Takamasa", "Takanobu", "Takao", "Takato", "Takatomi", "Takeharu", "Takehisa", "Takehito", "Takeichi", "Takenori", "Takero", "Takeru", "Taketo", "Takuma", "Takumi", "Takumu", "Takuro", "Takuzo", "Tamotsu", "Tasuku", "Tateo", "Tatsuaki", "Tatsuji", "Tatsuma", "Tatsumi", "Tatsuya", "Tatsuzo", "Teizo", "Teruaki", "Teruhiko", "Teruki", "Terumasa", "Tetsu", "Tetsuji", "Tokuji", "Tokuo", "Tomio", "Tomoaki", "Tomoharu", "Tomohisa", "Tomohito", "Tomokazu", "Tomoki", "Tomonori", "Tomoya", "Tomoyasu", "Torahiko", "Toru", "Toshi", "Toshiaki", "Toshio", "Toya", "Toyotaro", "Toyozo", "Tsunemi", "Tsutomu", "Tsutsumi", "Tsuyoshi", "Yahiko", "Yasuharu", "Yasuhide", "Yasuhiro", "Yasuji", "Yasuki", "Yasunari", "Yasunobu", "Yasushi", "Yasutaka", "Yasutaro", "Yasutomo", "Yawara", "Yohei", "Yoichi", "Yoji", "Yoshi", "Yoshinao", "Yoshito", "Yoshiya", "Yozo", "Yugi", "Yuichi", "Yuji", "Yukichi", "Yukihiko", "Yukihiro", "Yukimura", "Yukito", "Yuma", "Yusaku", "Yushi", "Yusuke", "Yutaka", "Yuzo", "Yuzuru", "Zenji", "Zentaro", "Akiho", "Akimi", "Akira", "Anri", "Asuka", "Ayumu", "Chiaki", "Chihiro", "Hajime", "Haru", "Haruka", "Harumi", "Hatsu", "Hayate", "Hazuki", "Hibiki", "Hifumi", "Hikari", "Hikaru", "Hinata", "Hiromi", "Hiromu", "Hisaya", "Hiyori", "Hotaru", "Ibuki", "Iori", "Itsuki", "Izumi", "Jun", "Kagami", "Kaname", "Kaoru", "Katsumi", "Kayo", "Kazu", "Kazumi", "Kei", "Kou", "Kunie", "Kurumi", "Kyo", "Maiko", "Maki", "Mako", "Makoto", "Masaki", "Masami", "Masumi", "Matoi", "Mayumi", "Michi", "Michiru", "Michiyo", "Midori", "Mikoto", "Minori", "Mirai", "Misao", "Mitsue", "Mitsuki", "Mitsuru", "Mitsuyo", "Mizuho", "Mizuki", "Nagisa", "Nao", "Naomi", "Natsu", "Natsuki", "Natsuo", "Nozomi", "Rei", "Ren", "Riku", "Rin", "Rui", "Ryo", "Ryuko", "Sakae", "Satsuki", "Setsuna", "Shigeri", "Shinobu", "Shion", "Shizuka", "Sora", "Subaru", "Takemi", "Tala", "Tamaki", "Tatsuki", "Teru", "Tomo", "Tomoe", "Tomomi", "Toshimi", "Tsubasa", "Tsukasa", "Yoshika", "Yoshimi", "Yosuke", "Yu", "Yuki", "Yuri"),
        FEMININE("Ai", "Aika", "Aiko", "Aimi", "Aina", "Airi", "Akane", "Akari", "Akemi", "Akeno", "Aki", "Akie", "Akiho", "Akiko", "Akimi", "Akina", "Akira", "Akiyo", "Amane", "Ami", "Anri", "Anzu", "Aoi", "Ariko", "Arisa", "Asako", "Asami", "Asuka", "Asumi", "Asuna", "Atsuko", "Atsumi", "Aya", "Ayaka", "Ayako", "Ayame", "Ayami", "Ayana", "Ayane", "Ayano", "Ayu", "Ayuka", "Ayuko", "Ayumi", "Ayumu", "Azumi", "Azura", "Azusa", "Chiaki", "Chidori", "Chie", "Chieko", "Chiemi", "Chigusa", "Chiharu", "Chihiro", "Chiho", "Chika", "Chikage", "Chikako", "Chinami", "Chinatsu", "Chisato", "Chitose", "Chiya", "Chiyako", "Chiyo", "Chiyoko", "Chizuko", "Chizuru", "Eiko", "Eimi", "Emi", "Emika", "Emiko", "Emiri", "Eri", "Erika", "Eriko", "Erina", "Etsuko", "Fujie", "Fujiko", "Fukumi", "Fumi", "Fumie", "Fumika", "Fumiko", "Fumino", "Fumiyo", "Fusako", "Futaba", "Fuyuko", "Fuyumi", "Fuka", "Hajime", "Hana", "Hanae", "Hanako", "Haru", "Harue", "Haruhi", "Haruka", "Haruko", "Harumi", "Haruna", "Haruno", "Haruyo", "Hasumi", "Hatsu", "Hatsue", "Hatsumi", "Hayate", "Hazuki", "Hibiki", "Hideko", "Hidemi", "Hifumi", "Hikari", "Hikaru", "Himawari", "Himeko", "Hina", "Hinako", "Hinata", "Hiroe", "Hiroka", "Hiroko", "Hiromi", "Hiromu", "Hiroyo", "Hisa", "Hisae", "Hisako", "Hisaya", "Hisayo", "Hitomi", "Hiyori", "Honami", "Honoka", "Hotaru", "Ibuki", "Ichiko", "Ikue", "Ikuko", "Ikumi", "Ikuyo", "Io", "Iori", "Itsuki", "Itsuko", "Itsumi", "Izumi", "Jitsuko", "Jun", "Junko", "Juri", "Kagami", "Kaguya", "Kaho", "Kahori", "Kahoru", "Kana", "Kanae", "Kanako", "Kaname", "Kanami", "Kanna", "Kanoko", "Kaori", "Kaoru", "Kaoruko", "Karen", "Karin", "Kasumi", "Katsuko", "Katsumi", "Kawai", "Kaya", "Kayo", "Kayoko", "Kazu", "Kazue", "Kazuha", "Kazuko", "Kazumi", "Kazusa", "Kazuyo", "Kei", "Keiki", "Keiko", "Kiho", "Kiko", "Kikue", "Kikuko", "Kimi", "Kimiko", "Kinuko", "Kira", "Kiyoko", "Koharu", "Komako", "Konomi", "Kotoe", "Kotomi", "Kotono", "Kotori", "Kou", "Kozue", "Kumi", "Kumiko", "Kunie", "Kuniko", "Kurenai", "Kuriko", "Kurumi", "Kyo", "Kyoko", "Maaya", "Machi", "Machiko", "Madoka", "Maho", "Mai", "Maiko", "Maki", "Makiko", "Mako", "Makoto", "Mami", "Mamiko", "Mana", "Manaka", "Manami", "Mao", "Mari", "Marie", "Marika", "Mariko", "Marina", "Masae", "Masaki", "Masako", "Masami", "Masayo", "Masumi", "Matoi", "Matsuko", "Mayako", "Mayo", "Mayu", "Mayuka", "Mayuko", "Mayumi", "Megu", "Megumi", "Mei", "Meiko", "Meisa", "Michi", "Michiko", "Michiru", "Michiyo", "Midori", "Mie", "Mieko", "Miharu", "Miho", "Mihoko", "Miiko", "Mika", "Mikako", "Miki", "Mikiko", "Mikoto", "Miku", "Mikuru", "Mimori", "Mina", "Minae", "Minako", "Minami", "Mineko", "Minori", "Mio", "Miori", "Mira", "Mirai", "Misaki", "Misako", "Misao", "Misato", "Misumi", "Misuzu", "Mitsue", "Mitsuki", "Mitsuko", "Mitsuru", "Mitsuyo", "Miu", "Miwa", "Miwako", "Miya", "Miyabi", "Miyako", "Miyo", "Miyoko", "Miyoshi", "Miyu", "Miyuki", "Miyumi", "Miyu", "Mizue", "Mizuho", "Mizuki", "Mizuko", "Moe", "Moeka", "Moeko", "Momo", "Momoe", "Momoka", "Momoko", "Motoko", "Mutsuko", "Mutsumi", "Nagako", "Nagisa", "Naho", "Nako", "Nami", "Nana", "Nanae", "Nanako", "Nanami", "Nanase", "Nao", "Naoko", "Naomi", "Narumi", "Natsue", "Natsuki", "Natsuko", "Natsume", "Natsumi", "Natsuo", "Noa", "Nobue", "Nobuko", "Nodoka", "Nonoka", "Noriko", "Noriyo", "Nozomi", "Omi", "Otoha", "Otome", "Ran", "Ranko", "Rei", "Reika", "Reiko", "Reina", "Ren", "Rena", "Reona", "Rie", "Rieko", "Riho", "Rika", "Rikako", "Riko", "Riku", "Rin", "Rina", "Rino", "Rio", "Risa", "Risako", "Ritsuko", "Rui", "Rumi", "Rumiko", "Runa", "Ruri", "Ruriko", "Ryo", "Ryoko", "Ryuko", "Ryoka", "Sachi", "Sachie", "Sachiko", "Sadako", "Sae", "Saeko", "Saiko", "Sakae", "Saki", "Sakie", "Sakiko", "Saku", "Sakura", "Sakurako", "Sanae", "Saori", "Sari", "Satoko", "Satomi", "Satsuki", "Sawa", "Sawako", "Saya", "Sayaka", "Sayako", "Sayo", "Sayoko", "Sayumi", "Sayuri", "Seiko", "Setsuko", "Setsuna", "Shigeko", "Shigeri", "Shiho", "Shihori", "Shiina", "Shimako", "Shinako", "Shino", "Shinobu", "Shion", "Shiori", "Shizue", "Shizuka", "Shizuko", "Shizuru", "Shuko", "Shoko", "Sonoko", "Sora", "Subaru", "Sugako", "Sumie", "Sumika", "Sumiko", "Sumire", "Suzue", "Suzuka", "Suzuko", "Taeko", "Takako", "Takayo", "Takeko", "Takemi", "Tala", "Tamaki", "Tamako", "Tamami", "Tamao", "Tamayo", "Tamiko", "Tatsuki", "Tatsuko", "Tazuko", "Teiko", "Teru", "Teruko", "Terumi", "Tokiko", "Tokuko", "Tomie", "Tomiko", "Tomo", "Tomoe", "Tomoka", "Tomoko", "Tomomi", "Tomoyo", "Toshiko", "Toshimi", "Toyoko", "Tsubasa", "Tsukasa", "Tsukiko", "Tsuneko", "Tsuru", "Umeko", "Uta", "Waka", "Wakako", "Wakana", "Yae", "Yaeko", "Yasue", "Yasuko", "Yayoi", "Yoko", "Yoriko", "Yoshika", "Yoshiko", "Yoshimi", "Yoshino", "Yu", "Yui", "Yuika", "Yuiko", "Yuka", "Yukako", "Yukari", "Yuki", "Yukie", "Yukika", "Yukiko", "Yukina", "Yukino", "Yumeko", "Yumi", "Yumie", "Yumika", "Yumiko", "Yuri", "Yuria", "Yurie", "Yurika", "Yuriko", "Yurina", "Yuumi", "Yuuna", "Yuko");

        private final String[] names;

        Gender(String... names) {
            this.names = names;
        }

        public String getName() {
            return this.names[(int) (Math.random() * this.names.length)];
        }
    }
}
