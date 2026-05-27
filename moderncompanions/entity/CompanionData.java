package com.majorbonghits.moderncompanions.entity;

import com.majorbonghits.moderncompanions.ModernCompanions;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionContents;


import java.util.*;

/**
 * Shared data tables (names, skins, foods) brought forward from the original Companions mod.
 */
public class CompanionData {
    public static final Random rand = new Random();

    public static final Item[] ALL_FOODS = new Item[]{
            Items.COOKIE,
            Items.BREAD,
            Items.MELON_SLICE,
            Items.APPLE,
            Items.SWEET_BERRIES,
            Items.CARROT,
            Items.BAKED_POTATO,
            Items.COOKED_SALMON,
            Items.COOKED_COD,
            Items.COOKED_MUTTON,
            Items.COOKED_PORKCHOP,
            Items.COOKED_BEEF,
            Items.COOKED_CHICKEN,
            Items.COOKED_RABBIT
    };

    /** Higher-tier foods/drinks companions can consume for healing but will not request while taming. */
    public static final Item[] EXTRA_HEAL_CONSUMABLES = new Item[]{
            Items.GOLDEN_APPLE,
            Items.ENCHANTED_GOLDEN_APPLE,
            Items.GOLDEN_CARROT,
            Items.HONEY_BOTTLE
    };

    /** Non-food resources companions might demand during taming. */
    public static final Item[] RESOURCE_ITEMS = new Item[] {
            Items.COAL,
            Items.CHARCOAL,
            Items.IRON_INGOT,
            Items.GOLD_INGOT,
            Items.COPPER_INGOT,
            Items.DIAMOND,
            Items.EMERALD,
            Items.LAPIS_LAZULI,
            Items.REDSTONE,
            Items.QUARTZ,
            Items.AMETHYST_SHARD
    };

    private static final Set<Item> DISALLOWED_FOODS = Set.of(
            Items.SPIDER_EYE,
            Items.ROTTEN_FLESH,
            Items.BEEF,
            Items.PORKCHOP,
            Items.CHICKEN,
            Items.MUTTON,
            Items.RABBIT,
            Items.COD,
            Items.SALMON
    );

    public static final MutableComponent[] tameFail = new MutableComponent[]{
            Component.literal("我还需要更多食物。"),
            Component.literal("就这些？"),
            Component.literal("我还是很饿。"),
            Component.literal("能再给点吗？"),
            Component.literal("我还需要一点点。"),
            Component.literal("不够。"),
            Component.literal("你管那叫一顿饭？"),
            Component.literal("我的胃都没感觉到。"),
            Component.literal("不，还是饿。"),
            Component.literal("我就当没发生过。再试一次。"),
            Component.literal("不错的开始，再来大概十个那样的。"),
            Component.literal("心意领了，但分量不够。"),
            Component.literal("你得再投入一点。"),
            Component.literal("那是零食，不是正餐。"),
            Component.literal("想要我的忠诚？那还差得远。"),
            Component.literal("我的饥饿条几乎没动。")
    };

    public static final MutableComponent[] notTamed = new MutableComponent[]{
            Component.literal("你有食物吗？"),
            Component.literal("我饿了。"),
            Component.literal("你在这附近看到食物了吗？"),
            Component.literal("我需要点食物。"),
            Component.literal("真希望我有点吃的。"),
            Component.literal("我快饿死了。"),
            Component.literal("你身上有零食吗？帮朋友问的。我就是那个朋友。"),
            Component.literal("我们可能会成为最好的朋友……如果你有食物的话。"),
            Component.literal("等食物开口说话了我再听。"),
            Component.literal("你看起来像是随身带零食的人。证明我是对的。"),
            Component.literal("没有食物，免谈。"),
            Component.literal("先谈喂食，再谈驯服。"),
            Component.literal("这附近有外卖服务吗？最好是你。"),
            Component.literal("我正在面试人类。要求：必须带食物。"),
            Component.literal("如果你有食物，这场对话会更顺利。"),
            Component.literal("第一步：食物。第二步：我可能会喜欢你。")
    };

    public static final MutableComponent[] WRONG_FOOD = new MutableComponent[]{
            Component.literal("那不是我想要的。"),
            Component.literal("我没要那个。"),
            Component.literal("看来你没明白我要什么。"),
            Component.literal("你忘了我想要什么吗？"),
            Component.literal("我不记得要过那个。"),
            Component.literal("这……错得很勇敢。"),
            Component.literal("你到底有没有在听我说话？"),
            Component.literal("努力值得肯定，但准确性欠佳。"),
            Component.literal("接近了，但又完全不对。"),
            Component.literal("这和我想要的正好相反。"),
            Component.literal("很有创意，但还是错的。"),
            Component.literal("是你手滑了还是故意的？"),
            Component.literal("我挑剔，但不至于饥不择食。"),
            Component.literal("我要的是食物，不是那个东西。"),
            Component.literal("再试一次，这次用用你的记忆力。")
    };

    public static final MutableComponent[] ENOUGH_FOOD = new MutableComponent[]{
            Component.literal("那个我已经够了。"),
            Component.literal("我不再想要那个了。"),
            Component.literal("我现在想要别的东西。"),
            Component.literal("再吃一个我就要爆炸了。"),
            Component.literal("换换花样挺好的，你知道的。"),
            Component.literal("我正式宣布，吃腻那个味道了。"),
            Component.literal("别再给那个了，我的味蕾在罢工。"),
            Component.literal("那个我吃够了，身心俱疲。"),
            Component.literal("你有没有别的东西？"),
            Component.literal("谢了，未来一百年我都够了。"),
            Component.literal("我的胃说不要，我的灵魂也说不要。"),
            Component.literal("我懂，你喜欢那个东西，但我不喜欢了。"),
            Component.literal("试试新的，给我惊喜——好的那种。")
    };

    public static final Class<?>[] alertMobs = new Class<?>[]{
            Blaze.class,
            EnderMan.class,
            Endermite.class,
            Ghast.class,
            Giant.class,
            Guardian.class,
            Hoglin.class,
            MagmaCube.class,
            Phantom.class,
            Shulker.class,
            Silverfish.class,
            Slime.class,
            Spider.class,
            Vex.class,
            AbstractSkeleton.class,
            Zoglin.class,
            Zombie.class,
            Raider.class
    };

    public static final Class<?>[] huntMobs = new Class<?>[]{
        Chicken.class,
        Cow.class,
        MushroomCow.class,
        Pig.class,
        Rabbit.class,
        Sheep.class
    };

    // Male (0) / female (1) skins
    public static final ResourceLocation[][] skins = new ResourceLocation[][]{
            new ResourceLocation[]{
                    tex("textures/entities/male/medieval-man-hugh.png"),
                    tex("textures/entities/male/alexandros.png"),
                    tex("textures/entities/male/cyrus.png"),
                    tex("textures/entities/male/diokles.png"),
                    tex("textures/entities/male/dion.png"),
                    tex("textures/entities/male/georgios.png"),
                    tex("textures/entities/male/ioannis.png"),
                    tex("textures/entities/male/medieval-peasant-schwaechlich.png"),
                    tex("textures/entities/male/medieval-peasant-without-vest.png"),
                    tex("textures/entities/male/medieval-peasant-with-vest-on.png"),
                    tex("textures/entities/male/panos.png"),
                    tex("textures/entities/male/viking-blue-tunic.png"),
                    tex("textures/entities/male/cronos-jojo.png"),
                    tex("textures/entities/male/medieval-man-alard.png"),
                    tex("textures/entities/male/peasant-ginger.png"),
                    tex("textures/entities/male/townsman-green-tunic.png"),
                    tex("textures/entities/male/polish-farmer.png"),
                    tex("textures/entities/male/peasant.png"),
                    tex("textures/entities/male/rustic-farmer.png"),
                    tex("textures/entities/male/medieval-villager.png")
            },
            new ResourceLocation[]{
                    tex("textures/entities/female/a-rogue-i-guess.png"),
                    tex("textures/entities/female/deidre-gramville.png"),
                    tex("textures/entities/female/deidre-gramville2.png"),
                    tex("textures/entities/female/eleora-halle.png"),
                    tex("textures/entities/female/fantastic-blue.png"),
                    tex("textures/entities/female/ftu-emma.png"),
                    tex("textures/entities/female/girl-medieval-peasant.png"),
                    tex("textures/entities/female/medieval-barmaid.png"),
                    tex("textures/entities/female/runaway.png"),
                    tex("textures/entities/female/shannon-flux.png"),
                    tex("textures/entities/female/the-traveller.png"),
                    tex("textures/entities/female/x-ayesha.png")
            }
    };

    public static final ResourceLocation[][] maleArmor = new ResourceLocation[][]{
            new ResourceLocation[]{
                    tex("textures/entities/armor/chainmail_arms_layer_2.png"),
                    tex("textures/entities/armor/chainmail_arms_layer_1.png")
            },
            new ResourceLocation[]{
                    tex("textures/entities/armor/iron_arms_layer_2.png"),
                    tex("textures/entities/armor/iron_arms_layer_1.png")
            },
            new ResourceLocation[]{
                    tex("textures/entities/armor/steel_layer_2.png"),
                    tex("textures/entities/armor/steel_layer_1.png")
            }
    };

    public static final ResourceLocation[][] femaleArmor = new ResourceLocation[][]{
            new ResourceLocation[]{
                    tex("textures/entities/armor/chainmail_layer_2.png"),
                    tex("textures/entities/armor/chainmail_layer_1.png")
            },
            new ResourceLocation[]{
                    tex("textures/entities/armor/iron_layer_2.png"),
                    tex("textures/entities/armor/iron_layer_1.png")
            },
            new ResourceLocation[]{
                    tex("textures/entities/armor/steel_layer_2.png"),
                    tex("textures/entities/armor/steel_layer_1.png")
            }
    };

    public static int getHealthModifier() {
        float healthFloat = rand.nextFloat();
        if (healthFloat <= 0.03) return -4;
        if (healthFloat <= 0.1) return -3;
        if (healthFloat <= 0.2) return -2;
        if (healthFloat <= 0.35) return -1;
        if (healthFloat <= 0.65) return 0;
        if (healthFloat <= 0.8) return 1;
        if (healthFloat <= 0.9) return 2;
        if (healthFloat <= 0.97) return 3;
        return 4;
    }

    public static ItemStack getSpawnArmor(EquipmentSlot armorType) {
        float materialFloat = rand.nextFloat();
        if (materialFloat <= 0.40F) {
            return ItemStack.EMPTY;
        } else if (materialFloat <= 0.70F) {
            return switch (armorType) {
                case HEAD -> Items.LEATHER_HELMET.getDefaultInstance();
                case CHEST -> Items.LEATHER_CHESTPLATE.getDefaultInstance();
                case LEGS -> Items.LEATHER_LEGGINGS.getDefaultInstance();
                case FEET -> Items.LEATHER_BOOTS.getDefaultInstance();
                default -> ItemStack.EMPTY;
            };
        } else if (materialFloat <= 0.90F) {
            return switch (armorType) {
                case HEAD -> Items.CHAINMAIL_HELMET.getDefaultInstance();
                case CHEST -> Items.CHAINMAIL_CHESTPLATE.getDefaultInstance();
                case LEGS -> Items.CHAINMAIL_LEGGINGS.getDefaultInstance();
                case FEET -> Items.CHAINMAIL_BOOTS.getDefaultInstance();
                default -> ItemStack.EMPTY;
            };
        } else {
            return switch (armorType) {
                case HEAD -> Items.IRON_HELMET.getDefaultInstance();
                case CHEST -> Items.IRON_CHESTPLATE.getDefaultInstance();
                case LEGS -> Items.IRON_LEGGINGS.getDefaultInstance();
                case FEET -> Items.IRON_BOOTS.getDefaultInstance();
                default -> ItemStack.EMPTY;
            };
        }
    }

    public static String getRandomName(int sex) {
        String firstName = firstNames[sex][rand.nextInt(firstNames[sex].length)];
        String lastName = lastNames[rand.nextInt(lastNames.length)];
        return firstName + " " + lastName;
    }

    public static boolean isArmorSlot(EquipmentSlot slot) {
        return slot == EquipmentSlot.HEAD || slot == EquipmentSlot.CHEST || slot == EquipmentSlot.LEGS || slot == EquipmentSlot.FEET;
    }

    public static boolean isArmorSlot(ItemStack stack) {
        if (stack.getItem() instanceof ArmorItem armor) {
            return isArmorSlot(armor.getEquipmentSlot());
        }
        return false;
    }

    public static boolean isBetterArmor(ItemStack candidate, ItemStack current) {
        if (!(candidate.getItem() instanceof ArmorItem candArmor) || !(current.getItem() instanceof ArmorItem curArmor)) {
            return current.isEmpty();
        }
        if (candArmor.getEquipmentSlot() != curArmor.getEquipmentSlot()) {
            return current.isEmpty();
        }
        if (candArmor.getMaterial() == ArmorMaterials.NETHERITE && curArmor.getMaterial() != ArmorMaterials.NETHERITE) {
            return true;
        }
        return candArmor.getDefense() > curArmor.getDefense();
    }

    public static Map<Item, Integer> getRandomFoodRequirement(Random random) {
        Map<Item, Integer> food = new HashMap<>();
        Item foodItem = pickAllowedFood(random);
        Item resourceItem = pickResource(random);
        // 2–5 food, 2–6 resource
        food.put(foodItem, random.nextInt(4) + 2);
        food.put(resourceItem, random.nextInt(5) + 2);
        return food;
    }

    public static boolean isFood(ItemStack stack) {
        Item item = stack.getItem();
        if (DISALLOWED_FOODS.contains(item)) return false;
        for (Item food : ALL_FOODS) {
            if (food.equals(item)) return true;
        }
        for (Item bonus : EXTRA_HEAL_CONSUMABLES) {
            if (bonus.equals(item)) return true;
        }
        return isHealingPotion(stack);
    }

    /** Allow only regen/healing potions (no splash/harmful mixes) as valid consumables. */
    public static boolean isHealingPotion(ItemStack stack) {
        if (!(stack.getItem() instanceof PotionItem)) return false;

        PotionContents contents = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
        boolean hasHealingEffect = false;
        for (MobEffectInstance effect : contents.getAllEffects()) {
            if (effect.getEffect().value().getCategory() == MobEffectCategory.HARMFUL) {
                return false;
            }
            if (effect.getEffect().is(MobEffects.HEAL) || effect.getEffect().is(MobEffects.REGENERATION)) {
                hasHealingEffect = true;
            }
        }
        return hasHealingEffect;
    }

    private static Item pickAllowedFood(Random random) {
        Item candidate;
        do {
            candidate = ALL_FOODS[random.nextInt(ALL_FOODS.length)];
        } while (DISALLOWED_FOODS.contains(candidate));
        return candidate;
    }

    private static Item pickResource(Random random) {
        return RESOURCE_ITEMS[random.nextInt(RESOURCE_ITEMS.length)];
    }

    private static ResourceLocation tex(String path) {
        return ResourceLocation.fromNamespaceAndPath(ModernCompanions.MOD_ID, path);
    }

    // ========== 中文名称映射表 ==========
    // 物品ID -> 中文名
    private static final Map<String, String> CN_ITEM_NAMES = new HashMap<>();
    static {
        cnItemName(Items.COOKIE, "饼干");
        cnItemName(Items.BREAD, "面包");
        cnItemName(Items.MELON_SLICE, "西瓜片");
        cnItemName(Items.APPLE, "苹果");
        cnItemName(Items.SWEET_BERRIES, "甜浆果");
        cnItemName(Items.CARROT, "胡萝卜");
        cnItemName(Items.BAKED_POTATO, "烤马铃薯");
        cnItemName(Items.COOKED_SALMON, "熟鲑鱼");
        cnItemName(Items.COOKED_COD, "熟鳕鱼");
        cnItemName(Items.COOKED_MUTTON, "熟羊肉");
        cnItemName(Items.COOKED_PORKCHOP, "熟猪排");
        cnItemName(Items.COOKED_BEEF, "熟牛排");
        cnItemName(Items.COOKED_CHICKEN, "熟鸡肉");
        cnItemName(Items.COOKED_RABBIT, "熟兔肉");
        cnItemName(Items.GOLDEN_APPLE, "金苹果");
        cnItemName(Items.ENCHANTED_GOLDEN_APPLE, "附魔金苹果");
        cnItemName(Items.GOLDEN_CARROT, "金胡萝卜");
        cnItemName(Items.HONEY_BOTTLE, "蜂蜜瓶");
        cnItemName(Items.COAL, "煤炭");
        cnItemName(Items.CHARCOAL, "木炭");
        cnItemName(Items.IRON_INGOT, "铁锭");
        cnItemName(Items.GOLD_INGOT, "金锭");
        cnItemName(Items.COPPER_INGOT, "铜锭");
        cnItemName(Items.DIAMOND, "钻石");
        cnItemName(Items.EMERALD, "绿宝石");
        cnItemName(Items.LAPIS_LAZULI, "青金石");
        cnItemName(Items.REDSTONE, "红石");
        cnItemName(Items.QUARTZ, "下界石英");
        cnItemName(Items.AMETHYST_SHARD, "紫水晶碎片");
    }

    private static void cnItemName(Item item, String cnName) {
        ResourceLocation key = BuiltInRegistries.ITEM.getKey(item);
        if (key != null) {
            CN_ITEM_NAMES.put(key.toString(), cnName);
        }
    }

    public static String getChineseItemName(Item item) {
        ResourceLocation key = BuiltInRegistries.ITEM.getKey(item);
        if (key != null && CN_ITEM_NAMES.containsKey(key.toString())) {
            return CN_ITEM_NAMES.get(key.toString());
        }
        return item.getDescription().getString();
    }

    // ========== 人名（音译）==========
    // male=0, female=1
    public static final String[][] firstNames = new String[][]{
            new String[]{
                    "阿伦", "亚伯", "亚伯拉罕", "亚当", "阿德里安", "艾丹", "艾登", "阿尔伯特",
                    "阿尔弗雷德", "安德鲁", "安东尼", "亚瑟", "亚瑟", "奥斯汀", "巴雷特", "巴里",
                    "博", "本杰明", "布莱克", "博比", "布拉德", "布拉德利", "布兰登", "布伦特",
                    "布雷特", "布莱恩", "布罗迪", "布莱恩", "凯莱布", "卡尔文", "卡梅伦", "卡尔",
                    "卡洛斯", "凯西", "卡特", "塞德里克", "查德", "查尔斯", "查理", "克里斯蒂安",
                    "克里斯托弗", "克拉克", "克莱顿", "克利福德", "科迪", "科尔比", "科尔", "科林",
                    "科林", "康纳", "康拉德", "科里", "克雷格", "达米安", "达米安", "达蒙",
                    "丹尼尔", "达伦", "达里尔", "大卫", "迪恩", "德克兰", "丹尼斯", "德里克",
                    "德里克", "德斯蒙德", "德文", "迭戈", "多米尼克", "唐纳德", "多诺万", "道格拉斯",
                    "德鲁", "达斯汀", "迪伦", "爱德华", "埃德温", "伊莱", "伊莱亚斯", "伊利亚",
                    "埃利奥特", "埃利奥特", "伊桑", "尤金", "埃文", "埃弗雷特", "费利克斯", "费尔南多",
                    "芬利", "芬恩", "弗朗西斯", "弗朗西斯科", "弗兰克", "富兰克林", "加布里埃尔", "盖奇",
                    "加雷斯", "加文", "乔治", "杰拉尔德", "吉尔伯特", "格伦", "格伦", "戈登",
                    "格雷厄姆", "格兰特", "格雷森", "格雷格", "格雷戈里", "哈雷", "哈罗德", "哈里森",
                    "哈里", "哈维", "海登", "希斯", "赫克托", "亨利", "哈德森", "休",
                    "雨果", "亨特", "伊恩", "艾萨克", "以赛亚", "伊斯雷尔", "杰克", "杰克逊",
                    "雅各布", "杰登", "杰克", "詹姆斯", "杰米", "贾里德", "杰森", "贾斯珀",
                    "哈维尔", "杰夫", "杰弗里", "耶利米", "杰里米", "杰罗姆", "杰西", "耶稣",
                    "乔尔", "约翰", "约翰尼", "约拿", "乔纳森", "乔丹", "豪尔赫", "何塞",
                    "约瑟夫", "约书亚", "约西亚", "胡安", "裘德", "朱利安", "胡里奥", "贾斯汀",
                    "卡登", "凯", "卡莱布", "卡尔", "凯登", "基思", "凯尔文", "肯尼斯",
                    "凯文", "基兰", "凯尔", "兰登", "拉里", "劳伦斯", "李", "利奥",
                    "莱昂", "伦纳德", "勒罗伊", "利亚姆", "洛根", "朗尼", "路易斯", "卢卡",
                    "卢卡斯", "路易斯", "卢克", "马尔科姆", "曼努埃尔", "马库斯", "马里奥", "马克",
                    "马歇尔", "马丁", "梅森", "马特奥", "马修", "莫里斯", "马克斯", "马克西米利安",
                    "麦克斯韦", "米卡", "迈克尔", "米格尔", "迈尔斯", "米切尔", "摩根", "内特",
                    "内森", "拿但业", "尼尔", "纳尔逊", "尼古拉斯", "尼科", "诺兰", "诺亚",
                    "诺曼", "奥利弗", "奥马尔", "奥斯卡", "欧文", "帕克", "帕特里克", "保罗",
                    "彼得", "菲利普", "菲利普", "普雷斯顿", "昆汀", "奎因", "拉斐尔", "拉尔夫",
                    "雷蒙", "兰德尔", "兰迪", "拉斐尔", "雷", "雷蒙德", "里斯", "里德",
                    "里德", "里斯", "里卡多", "理查德", "里克", "里基", "莱利", "罗伯托",
                    "罗伯特", "罗德尼", "罗杰", "罗兰", "罗曼", "罗纳德", "罗尼", "罗斯",
                    "罗伊", "拉塞尔", "瑞安", "塞缪尔", "斯科特", "肖恩", "塞巴斯蒂安", "塞尔吉奥",
                    "塞斯", "肖恩", "肖恩", "肖恩", "塞拉斯", "西蒙", "斯宾塞", "斯坦利",
                    "斯蒂芬", "史蒂文", "斯图尔特", "特伦斯", "西奥多", "托马斯", "蒂莫西", "托德",
                    "汤姆", "特拉维斯", "特雷弗", "特里斯坦", "特洛伊", "泰勒", "蒂龙", "维克多",
                    "文森特", "沃伦", "韦恩", "韦斯利", "韦斯顿", "威尔弗雷德", "威尔", "威廉",
                    "怀亚特", "泽维尔", "扎克", "撒迦利亚", "扎卡里"
            },
            new String[]{
                    "阿比盖尔", "艾达", "阿德莱德", "阿德琳", "艾米", "亚历克莎", "亚历山德拉", "亚历克西斯",
                    "爱丽丝", "艾丽西亚", "艾莉森", "艾莉森", "艾丽莎", "阿米莉亚", "阿梅莉", "艾米",
                    "阿纳斯塔西娅", "安德烈娅", "安吉拉", "安杰丽卡", "安吉丽娜", "安娜", "安娜贝尔", "安妮",
                    "安妮", "阿普里尔", "阿里亚娜", "阿里安娜", "阿莉娅", "阿什莉", "奥布里", "奥德丽",
                    "奥特姆", "阿娃", "贝莉", "芭芭拉", "比阿特丽斯", "贝琳达", "贝拉", "贝丝",
                    "贝萨妮", "比安卡", "布伦达", "布丽安娜", "布里奇特", "布兰妮", "布鲁克", "凯特琳",
                    "卡米拉", "卡米尔", "坎迪斯", "卡拉", "卡拉", "卡莉", "卡门", "卡罗琳",
                    "卡罗琳", "卡桑德拉", "凯瑟琳", "凯茜", "塞西莉亚", "塞莱斯特", "香奈儿", "夏洛特",
                    "切尔西", "克洛伊", "克里斯蒂娜", "克里斯蒂娜", "克莱尔", "克拉拉", "克拉丽莎", "考特尼",
                    "克里斯特尔", "辛西娅", "黛西", "达科塔", "丹妮拉", "丹妮尔", "达琳", "唐恩",
                    "黛博拉", "德布拉", "德利拉", "黛安娜", "黛安", "唐娜", "多萝西", "伊登",
                    "伊迪丝", "艾琳", "埃莉诺", "埃琳娜", "埃莉安娜", "埃莉诺", "埃莉萨", "埃莉斯",
                    "伊丽莎", "伊丽莎白", "埃拉", "埃伦", "埃莉", "埃洛伊丝", "艾尔莎", "艾米莉",
                    "艾玛", "埃丽卡", "埃琳", "埃斯米", "埃斯特尔", "埃丝特", "伊娃", "伊夫林",
                    "费丝", "费伊", "费利西蒂", "费恩", "菲奥娜", "弗洛伦斯", "弗朗西丝", "弗朗西斯卡",
                    "弗蕾亚", "加布里埃拉", "加布里埃拉", "盖尔", "乔治娅", "乔治娜", "吉莉安", "格洛丽亚",
                    "格蕾丝", "格温", "格温多琳", "海莉", "汉娜", "哈珀", "黑兹尔", "希瑟",
                    "海蒂", "海伦", "海伦娜", "霍莉", "霍普", "伊莫金", "英格丽", "艾琳",
                    "艾瑞丝", "伊莎贝尔", "伊莎贝拉", "伊斯拉", "艾薇", "杰奎琳", "杰德", "杰米",
                    "简", "珍妮特", "贾尼丝", "贾丝明", "吉恩", "珍娜", "珍妮弗", "杰茜卡",
                    "吉莉安", "琼", "乔安娜", "乔迪", "乔丹", "约瑟芬", "乔西", "乔伊",
                    "朱迪丝", "朱迪", "朱莉娅", "朱莉安娜", "朱莉", "朱丽叶", "琼", "贾斯汀",
                    "凯伦", "凯瑟琳", "凯瑟琳", "卡特里娜", "凯拉", "凯拉", "凯莉", "凯尔茜",
                    "金伯利", "基尔斯滕", "克里斯汀", "克里斯汀", "莱茜", "拉娜", "劳拉", "劳拉",
                    "劳伦", "莱娅", "琳恩", "莉娜", "莱斯莉", "莉拉", "莉莲", "莉莉",
                    "琳达", "琳赛", "莉萨", "洛拉", "洛蕾塔", "洛蒂", "路易莎", "路易丝",
                    "卢西亚", "露西尔", "露西", "卢娜", "莉迪亚", "麦肯齐", "梅西", "玛德琳",
                    "麦迪逊", "梅", "梅芙", "玛吉", "梅茜", "曼迪", "玛格丽特", "玛戈",
                    "玛丽亚", "玛丽亚", "玛丽亚姆", "玛丽安", "玛丽莲", "玛丽娜", "玛莎", "玛丽",
                    "玛蒂尔达", "玛雅", "梅根", "梅拉妮", "梅利莎", "米娅", "米歇尔", "米拉",
                    "莫莉", "莫妮卡", "摩根", "娜奥米", "纳塔利娅", "纳塔莉", "娜塔莎", "尼芙",
                    "妮可", "妮可拉", "尼娜", "诺埃尔", "诺拉", "诺拉", "奥利维娅", "佩奇",
                    "帕梅拉", "帕特里夏", "葆拉", "佩内洛普", "菲比", "波比", "普丽西拉", "雷切尔",
                    "丽贝卡", "里斯", "赖利", "丽塔", "罗宾", "罗莎", "罗莎莉", "罗丝",
                    "罗茜", "鲁比", "鲁思", "萨布丽娜", "萨曼莎", "桑德拉", "萨拉", "萨拉",
                    "萨凡纳", "斯嘉丽", "塞莱娜", "塞雷娜", "香农", "莎伦", "希拉", "谢尔比",
                    "西耶娜", "塞拉", "西蒙娜", "索菲娅", "索菲娅", "索菲", "斯泰茜", "斯特拉",
                    "斯蒂芬妮", "萨默", "苏珊", "苏珊娜", "悉尼", "塔拉", "特莎", "特蕾莎",
                    "蒂法尼", "特蕾西", "特丽尼蒂", "瓦伦蒂娜", "瓦莱丽", "瓦内萨", "薇拉", "维罗妮卡",
                    "维多利亚", "维奥莱特", "维维安", "温迪", "惠特尼", "威洛", "亚斯明", "伊冯娜",
                    "扎拉", "佐伊", "佐伊"
            }
    };

    public static final String[] lastNames = new String[]{
            "亚当斯", "安斯沃思", "亚历山大", "艾伦", "安德森", "安德鲁斯", "阿姆斯特朗", "阿诺德",
            "阿特金斯", "阿特金森", "奥斯汀", "贝利", "贝克", "鲍尔", "班克斯", "巴伯",
            "巴克", "巴恩斯", "巴尼特", "巴雷特", "巴里", "贝茨", "巴克斯特", "贝克",
            "贝尔", "贝内特", "本森", "本特利", "贝里", "布莱克", "布莱克", "布斯",
            "鲍恩", "博伊德", "布拉德利", "布雷迪", "布鲁尔", "布里奇斯", "布里格斯", "布鲁克斯",
            "布朗", "布赖恩特", "巴克利", "布洛克", "伯克", "伯内特", "伯恩斯", "伯顿",
            "布什", "巴特勒", "伯恩", "坎贝尔", "卡尔森", "卡彭特", "卡尔", "卡罗尔",
            "卡特", "凯西", "钱伯斯", "查普曼", "钱德勒", "克里斯滕森", "克拉克", "克拉克",
            "克莱顿", "科布", "科恩", "科尔", "科尔曼", "柯林斯", "康纳", "库克",
            "库珀", "柯蒂斯", "考克斯", "克雷格", "克劳福德", "克罗斯", "克鲁兹", "坎宁安",
            "柯蒂斯", "道尔顿", "丹尼尔", "丹尼尔斯", "戴维森", "戴维斯", "道森", "戴",
            "迪恩", "德莱尼", "丹尼斯", "狄克逊", "道格拉斯", "多伊尔", "邓肯", "邓恩",
            "爱德华兹", "埃利奥特", "埃利斯", "埃里克森", "埃里克森", "埃文斯", "法雷尔", "弗格森",
            "费尔南德斯", "费希尔", "菲茨杰拉德", "弗莱明", "弗莱彻", "弗洛雷斯", "福特", "福斯特",
            "福勒", "福克斯", "弗朗西斯", "富兰克林", "弗里曼", "加拉格尔", "加德纳", "加纳",
            "加西亚", "加里森", "乔治", "吉布斯", "吉布森", "吉尔伯特", "吉尔", "格洛弗",
            "冈萨雷斯", "古德曼", "戈登", "格雷厄姆", "格兰特", "格雷夫斯", "格雷", "格林",
            "格林", "格雷戈里", "格里芬", "格里菲斯", "霍尔", "汉密尔顿", "汉森", "汉森",
            "哈珀", "哈里斯", "哈里森", "哈特", "哈维", "霍金斯", "海斯", "海恩斯",
            "亨德森", "亨利", "埃尔南德斯", "希克斯", "希尔", "海因斯", "霍奇斯", "霍夫曼",
            "霍兰", "霍姆斯", "霍尔特", "霍普金斯", "霍顿", "霍华德", "豪", "哈德森",
            "休斯", "亨特", "亨特", "英格拉姆", "杰克逊", "雅各布斯", "詹姆斯", "贾维斯",
            "詹金斯", "詹宁斯", "詹森", "约翰逊", "约翰斯顿", "琼斯", "乔丹", "凯恩",
            "凯勒", "凯利", "凯利", "肯尼迪", "汗", "金", "柯克", "克莱因",
            "奈特", "兰伯特", "莱恩", "朗", "劳伦斯", "劳森", "利奇", "李",
            "刘易斯", "利特尔", "劳埃德", "洛根", "朗", "洛佩斯", "洛", "卢卡斯",
            "林奇", "莱昂斯", "麦克唐纳", "马登", "曼宁", "马克斯", "马什", "马歇尔",
            "马丁", "马丁内斯", "梅森", "马修斯", "麦克斯韦", "梅", "麦克布莱德", "麦卡锡",
            "麦考密克", "麦克唐纳", "麦吉", "麦格拉思", "麦格雷戈", "麦肯齐", "麦克莱恩", "麦克米伦",
            "梅迪纳", "门德斯", "迈耶", "米勒", "米尔斯", "米切尔", "穆迪", "穆尔",
            "莫拉莱斯", "摩根", "莫里斯", "莫里森", "莫顿", "莫斯", "墨菲", "默里",
            "迈尔斯", "纳尔逊", "纽曼", "牛顿", "尼科尔斯", "尼科尔森", "尼克松", "诺兰",
            "诺曼", "诺里斯", "奥布莱恩", "奥康纳", "奥尼尔", "奥利弗", "奥尔森", "奥尔蒂斯",
            "欧文斯", "佩奇", "帕尔默", "帕克", "帕特尔", "帕特里克", "帕特森", "佩恩",
            "皮尔斯", "皮尔逊", "佩纳", "佩雷斯", "珀金斯", "佩里", "彼得斯", "彼得森",
            "菲利普斯", "皮尔斯", "普尔", "波特", "波特", "鲍威尔", "鲍尔斯", "普赖斯",
            "奎因", "拉米雷斯", "拉莫斯", "兰德尔", "雷", "里德", "里斯", "里斯",
            "里德", "雷耶斯", "雷诺兹", "罗兹", "赖斯", "理查兹", "理查森", "莱利",
            "里弗斯", "罗宾斯", "罗伯茨", "罗伯逊", "罗宾逊", "罗杰斯", "罗德里格斯", "罗杰斯",
            "罗斯", "罗斯", "罗", "鲁伊斯", "拉塞尔", "瑞安", "萨拉查", "桑德斯",
            "桑德森", "桑多瓦尔", "圣地亚哥", "桑德斯", "施密特", "斯科特", "夏普", "肖",
            "谢菲尔德", "谢尔顿", "肖特", "席尔瓦", "西蒙斯", "辛普森", "辛格", "斯隆",
            "史密斯", "斯奈德", "斯宾塞", "斯坦利", "斯蒂芬斯", "史蒂文斯", "斯图尔特", "斯通",
            "沙利文", "萨默斯", "萨顿", "泰勒", "特里", "托马斯", "汤普森", "桑顿",
            "托德", "托雷斯", "汤森", "陈", "塔克", "特纳", "泰勒", "巴斯克斯",
            "沃恩", "巴斯克斯", "韦德", "瓦格纳", "沃克", "华莱士", "沃尔什", "沃尔特斯",
            "沃德", "沃伦", "华盛顿", "沃特斯", "沃特金斯", "沃森", "沃茨", "韦弗",
            "韦布", "韦伯", "韦尔奇", "韦尔斯", "韦斯特", "惠勒", "怀特", "惠特克",
            "怀特黑德", "惠特菲尔德", "威廉姆斯", "威廉森", "威利斯", "威尔逊", "怀斯", "沃尔夫",
            "黄", "伍德", "伍兹", "赖特", "怀亚特", "扬", "齐默尔曼"
    };
}