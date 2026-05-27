package com.majorbonghits.moderncompanions.entity;

import com.majorbonghits.moderncompanions.entity.CompanionData;
import com.majorbonghits.moderncompanions.core.ModConfig;
import com.majorbonghits.moderncompanions.core.ModMenuTypes;
import com.majorbonghits.moderncompanions.entity.ai.*;
import com.majorbonghits.moderncompanions.entity.personality.CompanionPersonality;
import com.majorbonghits.moderncompanions.menu.CompanionMenu;
import com.majorbonghits.moderncompanions.core.ModItems;
import com.majorbonghits.moderncompanions.core.ModEnchantments;
import com.majorbonghits.moderncompanions.item.ResurrectionScrollItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.entity.item.ItemEntity;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import com.majorbonghits.moderncompanions.entity.SummonedWitherSkeleton;
import com.majorbonghits.moderncompanions.core.TagsInit;

/**
 * Port of the original AbstractHumanCompanionEntity with taming, leveling,
 * patrol/guard logic, and inventory handling.
 */
public abstract class AbstractHumanCompanionEntity extends TamableAnimal {
    private static final EntityDataAccessor<Integer> SKIN_VARIANT = SynchedEntityData
            .defineId(AbstractHumanCompanionEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> SEX = SynchedEntityData
            .defineId(AbstractHumanCompanionEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> BASE_HEALTH = SynchedEntityData
            .defineId(AbstractHumanCompanionEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> EXP_LVL = SynchedEntityData
            .defineId(AbstractHumanCompanionEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<String> CUSTOM_SKIN_URL = SynchedEntityData
            .defineId(AbstractHumanCompanionEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Integer> STR = SynchedEntityData
            .defineId(AbstractHumanCompanionEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DEX = SynchedEntityData
            .defineId(AbstractHumanCompanionEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> INTL = SynchedEntityData
            .defineId(AbstractHumanCompanionEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> END = SynchedEntityData
            .defineId(AbstractHumanCompanionEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> KILL_COUNT = SynchedEntityData
            .defineId(AbstractHumanCompanionEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> EATING = SynchedEntityData
            .defineId(AbstractHumanCompanionEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> LAST_SWING_TICK = SynchedEntityData
            .defineId(AbstractHumanCompanionEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> ALERT = SynchedEntityData
            .defineId(AbstractHumanCompanionEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> HUNTING = SynchedEntityData
            .defineId(AbstractHumanCompanionEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> PATROLLING = SynchedEntityData
            .defineId(AbstractHumanCompanionEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> FOLLOWING = SynchedEntityData
            .defineId(AbstractHumanCompanionEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> GUARDING = SynchedEntityData
            .defineId(AbstractHumanCompanionEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SPRINT_ENABLED = SynchedEntityData
            .defineId(AbstractHumanCompanionEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> PICKUP_ITEMS = SynchedEntityData
            .defineId(AbstractHumanCompanionEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Optional<BlockPos>> PATROL_POS = SynchedEntityData
            .defineId(AbstractHumanCompanionEntity.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
    private static final EntityDataAccessor<Integer> PATROL_RADIUS = SynchedEntityData
            .defineId(AbstractHumanCompanionEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<String> FOOD1 = SynchedEntityData
            .defineId(AbstractHumanCompanionEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> FOOD2 = SynchedEntityData
            .defineId(AbstractHumanCompanionEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Integer> FOOD1_AMT = SynchedEntityData
            .defineId(AbstractHumanCompanionEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> FOOD2_AMT = SynchedEntityData
            .defineId(AbstractHumanCompanionEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> EXP_PROGRESS = SynchedEntityData
            .defineId(AbstractHumanCompanionEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> SPECIALIST = SynchedEntityData
            .defineId(AbstractHumanCompanionEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<String> PRIMARY_TRAIT = SynchedEntityData
            .defineId(AbstractHumanCompanionEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> SECONDARY_TRAIT = SynchedEntityData
            .defineId(AbstractHumanCompanionEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Integer> BOND_LEVEL = SynchedEntityData
            .defineId(AbstractHumanCompanionEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> BOND_XP = SynchedEntityData
            .defineId(AbstractHumanCompanionEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<String> BACKSTORY_ID = SynchedEntityData
            .defineId(AbstractHumanCompanionEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Float> MORALE = SynchedEntityData
            .defineId(AbstractHumanCompanionEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> RESURRECT_COUNT = SynchedEntityData
            .defineId(AbstractHumanCompanionEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Long> FIRST_TAMED_TIME = SynchedEntityData
            .defineId(AbstractHumanCompanionEntity.class, EntityDataSerializers.LONG);
    private static final EntityDataAccessor<Long> DIST_TRAVELED = SynchedEntityData
            .defineId(AbstractHumanCompanionEntity.class, EntityDataSerializers.LONG);
    private static final EntityDataAccessor<Integer> MAJOR_KILLS = SynchedEntityData
            .defineId(AbstractHumanCompanionEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> AGE_YEARS = SynchedEntityData
            .defineId(AbstractHumanCompanionEntity.class, EntityDataSerializers.INT);
    private static final ResourceLocation MOD_MORALE_DAMAGE = ResourceLocation.fromNamespaceAndPath(
            com.majorbonghits.moderncompanions.ModernCompanions.MOD_ID, "morale_damage");
    private static final ResourceLocation MOD_MORALE_ARMOR = ResourceLocation.fromNamespaceAndPath(
            com.majorbonghits.moderncompanions.ModernCompanions.MOD_ID, "morale_armor");
    private static final ResourceLocation MOD_TRAIT_QUICKSTEP = ResourceLocation.fromNamespaceAndPath(
            com.majorbonghits.moderncompanions.ModernCompanions.MOD_ID, "trait_quickstep_speed");
    private static final ResourceLocation MOD_TRAIT_STALWART = ResourceLocation.fromNamespaceAndPath(
            com.majorbonghits.moderncompanions.ModernCompanions.MOD_ID, "trait_stalwart_kb");
    private static final ResourceLocation MOD_TRAIT_RECKLESS = ResourceLocation.fromNamespaceAndPath(
            com.majorbonghits.moderncompanions.ModernCompanions.MOD_ID, "trait_reckless_speed");
    private static final ResourceLocation MOD_TRAIT_BRAVE = ResourceLocation.fromNamespaceAndPath(
            com.majorbonghits.moderncompanions.ModernCompanions.MOD_ID, "trait_brave_damage");
    private static final ResourceLocation MOD_TRAIT_GUARDIAN = ResourceLocation.fromNamespaceAndPath(
            com.majorbonghits.moderncompanions.ModernCompanions.MOD_ID, "trait_guardian_armor");
    private static final ResourceLocation MOD_TRAIT_DEVOTED = ResourceLocation.fromNamespaceAndPath(
            com.majorbonghits.moderncompanions.ModernCompanions.MOD_ID, "trait_devoted_armor");
    private static final ResourceLocation MOD_TRAIT_NIGHT_OWL_DAMAGE = ResourceLocation.fromNamespaceAndPath(
            com.majorbonghits.moderncompanions.ModernCompanions.MOD_ID, "trait_night_owl_damage");
    private static final ResourceLocation MOD_TRAIT_NIGHT_OWL_SPEED = ResourceLocation.fromNamespaceAndPath(
            com.majorbonghits.moderncompanions.ModernCompanions.MOD_ID, "trait_night_owl_speed");
    private static final ResourceLocation MOD_TRAIT_SUN_DAMAGE = ResourceLocation.fromNamespaceAndPath(
            com.majorbonghits.moderncompanions.ModernCompanions.MOD_ID, "trait_sun_damage");
    private static final ResourceLocation MOD_TRAIT_SUN_SPEED = ResourceLocation.fromNamespaceAndPath(
            com.majorbonghits.moderncompanions.ModernCompanions.MOD_ID, "trait_sun_speed");
    private static final ResourceLocation MOD_TRAIT_MELANCHOLIC = ResourceLocation.fromNamespaceAndPath(
            com.majorbonghits.moderncompanions.ModernCompanions.MOD_ID, "trait_melancholic_penalty");
    private static final int FOOD_REQUEST_COOLDOWN_TICKS = 600; // ~30s between requests

    protected final SimpleContainer inventory = new SimpleContainer(54);
    protected final Map<Item, Integer> foodRequirements = new HashMap<>();
    protected final Random rand = new Random();

    public PatrolGoal patrolGoal;
    public MoveBackToPatrolGoal moveBackGoal;
    private int lastFoodRequestTick = -200;
    private int specialistAttr = -1; // 0=STR,1=DEX,2=INT,3=END; -1 none

    private int totalExperience;
    private float experienceProgress;
    private int lastLevelUpTime;

    private final CompanionPersonality personality = new CompanionPersonality();
    private int bondTickCounter = 0;
    private int lastNearDeathTick = -200;
    private int personalityRefreshTicker = 0;
    private double lastTrackX;
    private double lastTrackY;
    private double lastTrackZ;
    private double distanceAccumulator;
    private static final long AGE_INTERVAL_TICKS = 90L * 24000L; // 90 in-game days (~3 months) per year

    private int equipmentStrengthBonus;
    private int equipmentDexterityBonus;
    private int equipmentIntelligenceBonus;
    private int equipmentEnduranceBonus;

    // Client-side tracking of the last swing tick we already applied locally.
    private int lastAppliedSwingTick = -1;

    private static final ResourceLocation PREFERRED_WEAPON_MOD = ResourceLocation.fromNamespaceAndPath(
            com.majorbonghits.moderncompanions.ModernCompanions.MOD_ID, "preferred_weapon_bonus");

    protected AbstractHumanCompanionEntity(EntityType<? extends TamableAnimal> type, Level level) {
        super(type, level);
        this.setTame(false, false);
        if (this.getNavigation() instanceof GroundPathNavigation nav) {
            nav.setCanOpenDoors(true);
            nav.setCanFloat(true);
        }
    }

    /* ---------- Registration ---------- */

    public static AttributeSupplier.Builder createAttributes() {
        double baseHealth = ModConfig.BASE_HEALTH != null ? ModConfig.safeGet(ModConfig.BASE_HEALTH).doubleValue()
                : 20.0D;
        return TamableAnimal.createMobAttributes()
                .add(Attributes.FOLLOW_RANGE, 20.0D)
                .add(Attributes.MAX_HEALTH, baseHealth)
                .add(Attributes.ATTACK_DAMAGE, 1.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.32D)
                .add(Attributes.ATTACK_SPEED, 1.6D)
                .add(Attributes.ATTACK_KNOCKBACK, 0.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.0D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SKIN_VARIANT, 0);
        builder.define(SEX, 0);
        builder.define(BASE_HEALTH, ModConfig.safeGet(ModConfig.BASE_HEALTH));
        builder.define(EXP_LVL, 0);
        builder.define(EATING, false);
        builder.define(ALERT, false);
        builder.define(HUNTING, false);
        builder.define(PATROLLING, false);
        builder.define(FOLLOWING, false);
        builder.define(GUARDING, false);
        builder.define(SPRINT_ENABLED, false);
        builder.define(PICKUP_ITEMS, true);
        builder.define(PATROL_POS, Optional.empty());
        builder.define(PATROL_RADIUS, 10);
        builder.define(FOOD1, "");
        builder.define(FOOD2, "");
        builder.define(FOOD1_AMT, 0);
        builder.define(FOOD2_AMT, 0);
        builder.define(EXP_PROGRESS, 0.0F);
        builder.define(STR, 4);
        builder.define(DEX, 4);
        builder.define(INTL, 4);
        builder.define(END, 4);
        builder.define(SPECIALIST, -1);
        builder.define(KILL_COUNT, 0);
        builder.define(PRIMARY_TRAIT, "");
        builder.define(SECONDARY_TRAIT, "");
        builder.define(BOND_LEVEL, 0);
        builder.define(BOND_XP, 0);
        builder.define(BACKSTORY_ID, "");
        builder.define(MORALE, 0.0F);
        builder.define(RESURRECT_COUNT, 0);
        builder.define(FIRST_TAMED_TIME, -1L);
        builder.define(DIST_TRAVELED, 0L);
        builder.define(MAJOR_KILLS, 0);
        builder.define(AGE_YEARS, 0);
        builder.define(CUSTOM_SKIN_URL, "");
        builder.define(LAST_SWING_TICK, 0);
    }

    @Override
    public void swing(InteractionHand hand) {
        swing(hand, true);
    }

    @Override
    public void swing(InteractionHand hand, boolean updateSelf) {
        // Bypass the vanilla guard that ignores swings if an earlier one hasn't reached
        // mid-animation.
        // Reset state first so rapid consecutive hits always restart the swing
        // animation locally and in packets.
        this.swinging = false;
        this.swingTime = -1;
        super.swing(hand, true);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(0, new EatGoal(this));
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2, new AvoidCreeperGoal(this, 1.5D, 1.5D));
        this.goalSelector.addGoal(3, new MoveBackToGuardGoal(this));
        this.goalSelector.addGoal(3, new CustomFollowOwnerGoal(this, followSpeed(), followStartDistance(), followStopDistance(), true));
        this.goalSelector.addGoal(4, new CustomWaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(7, new OpenDoorGoal(this, true));
        this.goalSelector.addGoal(8, new LowHealthGoal(this));
        patrolGoal = new PatrolGoal(this, 60, getPatrolRadius());
        moveBackGoal = new MoveBackToPatrolGoal(this, getPatrolRadius());
        this.goalSelector.addGoal(3, moveBackGoal);
        this.goalSelector.addGoal(3, patrolGoal);

        this.targetSelector.addGoal(1, new CustomOwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new CustomOwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new CustomHurtByTargetGoal(this));
        this.targetSelector.addGoal(4, new HuntGoal(this));
        this.targetSelector.addGoal(5, new AlertGoal(this));
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (level().isClientSide) {
            if (key.equals(PRIMARY_TRAIT) || key.equals(SECONDARY_TRAIT) || key.equals(BOND_XP)
                    || key.equals(BOND_LEVEL) || key.equals(BACKSTORY_ID) || key.equals(MORALE)
                    || key.equals(RESURRECT_COUNT) || key.equals(FIRST_TAMED_TIME) || key.equals(DIST_TRAVELED)
                    || key.equals(MAJOR_KILLS) || key.equals(AGE_YEARS)) {
                personality.setPrimaryTrait(this.entityData.get(PRIMARY_TRAIT));
                personality.setSecondaryTrait(this.entityData.get(SECONDARY_TRAIT));
                personality.setBondXp(this.entityData.get(BOND_XP));
                personality.setBondLevel(this.entityData.get(BOND_LEVEL));
                personality.setBackstoryId(this.entityData.get(BACKSTORY_ID));
                personality.setMorale(this.entityData.get(MORALE));
                personality.setFirstTamedGameTime(this.entityData.get(FIRST_TAMED_TIME));
                personality.addDistanceTraveled(this.entityData.get(DIST_TRAVELED) - personality.getDistanceTraveledWithOwner());
                personality.setMajorKills(this.entityData.get(MAJOR_KILLS));
                personality.setAgeYears(this.entityData.get(AGE_YEARS));
                // sync resurrection count (monotonic)
                int target = this.entityData.get(RESURRECT_COUNT);
                while (personality.getTimesResurrected() < target) {
                    personality.noteResurrection();
                }
            }
        }
    }

    /* ---------- Flags & helpers ---------- */

    public boolean isFollowing() {
        return this.entityData.get(FOLLOWING);
    }

    public void setFollowing(boolean value) {
        this.entityData.set(FOLLOWING, value);
    }

    public boolean isPatrolling() {
        return this.entityData.get(PATROLLING);
    }

    public void setPatrolling(boolean value) {
        this.entityData.set(PATROLLING, value);
    }

    public boolean isGuarding() {
        return this.entityData.get(GUARDING);
    }

    public void setGuarding(boolean value) {
        this.entityData.set(GUARDING, value);
    }

    public boolean isPickupEnabled() {
        return this.entityData.get(PICKUP_ITEMS);
    }

    public void setPickupEnabled(boolean value) {
        this.entityData.set(PICKUP_ITEMS, value);
    }

    public boolean isSprintEnabled() {
        return this.entityData.get(SPRINT_ENABLED);
    }

    public void setSprintEnabled(boolean value) {
        this.entityData.set(SPRINT_ENABLED, value);
    }

    public boolean isAlert() {
        return this.entityData.get(ALERT);
    }

    public void setAlert(boolean value) {
        this.entityData.set(ALERT, value);
    }

    public boolean isHunting() {
        return this.entityData.get(HUNTING);
    }

    public void setHunting(boolean value) {
        this.entityData.set(HUNTING, value);
    }

    public Optional<BlockPos> getPatrolPos() {
        return this.entityData.get(PATROL_POS);
    }

    public void setPatrolPos(@Nullable BlockPos pos) {
        this.entityData.set(PATROL_POS, Optional.ofNullable(pos));
    }

    public int getPatrolRadius() {
        return this.entityData.get(PATROL_RADIUS);
    }

    /**
     * Human-readable class label derived from the entity registry name.
     */
    public String getClassDisplayName() {
        var key = BuiltInRegistries.ENTITY_TYPE.getKey(this.getType());
        if (key == null)
            return "Companion";
        String path = key.getPath().replace('_', ' ');
        StringBuilder builder = new StringBuilder();
        for (String part : path.split(" ")) {
            if (part.isEmpty())
                continue;
            builder.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1)).append(' ');
        }
        return builder.toString().trim();
    }

    public void setPatrolRadius(int radius) {
        this.entityData.set(PATROL_RADIUS, Mth.clamp(radius, 1, 64));
        if (patrolGoal != null)
            patrolGoal.radius = radius;
        if (moveBackGoal != null)
            moveBackGoal.radius = radius;
    }

    public void clearPatrol() {
        setPatrolPos(null);
        setPatrolling(false);
        setPatrolRadius(4);
    }

 public String getFoodStatus() {
    String f1 = entityData.get(FOOD1_AMT) > 0 ? entityData.get(FOOD1_AMT) + "x " + prettyItemName(entityData.get(FOOD1)) : "已完成";
    String f2 = entityData.get(FOOD2_AMT) > 0 ? entityData.get(FOOD2_AMT) + "x " + prettyItemName(entityData.get(FOOD2)) : "已完成";
    return "需要： " + f1 + " 和 " + f2;
}

    public String getFoodStatusForGui() {
       if (!this.isTame()) {
        return getWantedFoodsCompact(); // 调用下方汉化的方法
    }
    if (this.getHealth() < this.getMaxHealth() - 0.5F) {
        return hasFoodInInventory() ? "治疗中..." : "需要食物来治愈";
    }
    return "";
}

    public String getWantedFoodsCompact() {
        int amt1 = entityData.get(FOOD1_AMT);
        int amt2 = entityData.get(FOOD2_AMT);
        String id1 = entityData.get(FOOD1);
        String id2 = entityData.get(FOOD2);
        String first = amt1 > 0 ? amt1 + "x " + prettyItemName(id1) : "";
        String second = amt2 > 0 ? amt2 + "x " + prettyItemName(id2) : "";
        if (first.isEmpty() && second.isEmpty())
            return "";
        if (!first.isEmpty() && !second.isEmpty())
            return first + ", " + second;
        return first + second;
    }

    public SimpleContainer getInventory() {
        return inventory;
    }

    public Map<Item, Integer> getFoodRequirements() {
        return foodRequirements;
    }

    public int getSkinIndex() {
        return this.entityData.get(SKIN_VARIANT);
    }

    public void setSkinIndex(int index) {
        int sex = getSex();
        int max = CompanionData.skins[sex].length;
        this.entityData.set(SKIN_VARIANT, Mth.clamp(index, 0, Math.max(0, max - 1)));
    }

    public ResourceLocation getDefaultSkinTexture() {
        int sex = Mth.clamp(getSex(), 0, CompanionData.skins.length - 1);
        ResourceLocation[] variants = CompanionData.skins[sex];
        int idx = Mth.clamp(getSkinIndex(), 0, variants.length - 1);
        return variants[idx];
    }

    public Optional<String> getCustomSkinUrl() {
        String raw = this.entityData.get(CUSTOM_SKIN_URL);
        return raw == null || raw.isBlank() ? Optional.empty() : Optional.of(raw);
    }

    public void setCustomSkinUrl(@Nullable String url) {
        // Store URL as a synced string so clients can fetch/download the texture on
        // demand.
        this.entityData.set(CUSTOM_SKIN_URL, url == null ? "" : url.trim());
    }

    public int getSex() {
        return this.entityData.get(SEX);
    }

    public void setSex(int value) {
        this.entityData.set(SEX, Mth.clamp(value, 0, CompanionData.skins.length - 1));
    }

    public int getBaseHealth() {
        return this.entityData.get(BASE_HEALTH);
    }

    public void setBaseHealth(int health) {
        this.entityData.set(BASE_HEALTH, health);
    }

    public boolean isEating() {
        return this.entityData.get(EATING);
    }

    public void setEating(boolean eating) {
        this.entityData.set(EATING, eating);
    }

    public int getExpLvl() {
        return this.entityData.get(EXP_LVL);
    }

    public void setExpLvl(int lvl) {
        this.entityData.set(EXP_LVL, Math.max(lvl, 0));
    }

    public float getExperienceProgress() {
        return this.level().isClientSide ? this.entityData.get(EXP_PROGRESS) : this.experienceProgress;
    }

    public int getTotalExperience() {
        return this.totalExperience;
    }

    public int getKillCount() {
        return this.entityData.get(KILL_COUNT);
    }

    public void setKillCount(int kills) {
        this.entityData.set(KILL_COUNT, Math.max(0, kills));
    }

    public void incrementKillCount() {
        if (!this.level().isClientSide) {
            setKillCount(getKillCount() + 1);
            personality.incrementTotalKills(false);
        }
    }

    public void recordKill(LivingEntity victim) {
        boolean major = isMajorKill(victim);
        incrementKillCount();
        personality.incrementTotalKills(major);
        if (major) {
            this.entityData.set(MAJOR_KILLS, personality.getMajorKills());
        }
        syncPersonalityToData();
    }

    private boolean isMajorKill(LivingEntity victim) {
        var type = victim.getType();
        return type == EntityType.ENDER_DRAGON
                || type == EntityType.WITHER
                || type == EntityType.WARDEN
                || type == EntityType.ELDER_GUARDIAN;
    }

    public CompanionPersonality getPersonality() {
        return personality;
    }

    public String getPrimaryTraitId() {
        return this.entityData.get(PRIMARY_TRAIT);
    }

    public String getSecondaryTraitId() {
        return this.entityData.get(SECONDARY_TRAIT);
    }

    public String getBackstoryId() {
        return this.entityData.get(BACKSTORY_ID);
    }

    public int getBondLevel() {
        return this.entityData.get(BOND_LEVEL);
    }

    public int getBondXp() {
        return this.entityData.get(BOND_XP);
    }

    public float getMorale() {
        return this.entityData.get(MORALE);
    }

    public String getMoraleDescriptorKey() {
        return personality.moraleDescriptorKey();
    }

    public int getTimesResurrected() {
        return this.entityData.get(RESURRECT_COUNT);
    }

    public long getFirstTamedGameTime() {
        return this.entityData.get(FIRST_TAMED_TIME);
    }

    public long getDistanceTraveledWithOwner() {
        return this.entityData.get(DIST_TRAVELED);
    }

    public int getMajorKills() {
        return this.entityData.get(MAJOR_KILLS);
    }

    public int getAgeYears() {
        return this.entityData.get(AGE_YEARS);
    }

    public void setPrimaryTraitId(String trait) {
        personality.setPrimaryTrait(trait);
        this.entityData.set(PRIMARY_TRAIT, trait == null ? "" : trait);
    }

    public void setSecondaryTraitId(String trait) {
        personality.setSecondaryTrait(trait);
        this.entityData.set(SECONDARY_TRAIT, trait == null ? "" : trait);
    }

    public void setBackstoryId(String id) {
        personality.setBackstoryId(id);
        this.entityData.set(BACKSTORY_ID, id == null ? "" : id);
    }

    public void setBondXp(int xp) {
        personality.setBondXp(xp);
        this.entityData.set(BOND_XP, personality.getBondXp());
        this.entityData.set(BOND_LEVEL, personality.getBondLevel());
    }

    public void awardBondXp(int amount) {
        if (!ModConfig.safeGet(ModConfig.BOND_ENABLED) || amount <= 0) return;
        int before = personality.getBondLevel();
        personality.awardBondXp(amount);
        this.entityData.set(BOND_XP, personality.getBondXp());
        this.entityData.set(BOND_LEVEL, personality.getBondLevel());
        if (personality.getBondLevel() > before && ModConfig.safeGet(ModConfig.MORALE_ENABLED)) {
            adjustMorale(ModConfig.safeGet(ModConfig.MORALE_BOND_LEVEL_DELTA).floatValue());
        }
    }

    public void setMorale(float morale) {
        personality.setMorale(morale);
        this.entityData.set(MORALE, personality.getMorale());
    }

    public void adjustMorale(float delta) {
        float adjusted = delta;
        if (delta < 0) {
            if (hasTrait("trait_disciplined")) adjusted *= 0.7F;
            if (hasTrait("trait_jokester")) adjusted *= 0.7F;
        }
        personality.adjustMorale(adjusted);
        float floor = getMoraleFloor();
        if (personality.getMorale() < floor) {
            personality.setMorale(floor);
        }
        this.entityData.set(MORALE, personality.getMorale());
    }

    private float getMoraleFloor() {
        if (!ModConfig.safeGet(ModConfig.BOND_ENABLED)) return -1.0F;
        float floor = -0.5F + (getBondLevel() * 0.05F);
        return Mth.clamp(floor, -0.2F, 0.2F);
    }

    public void incrementResurrections() {
        personality.noteResurrection();
        this.entityData.set(RESURRECT_COUNT, personality.getTimesResurrected());
    }

    public void setFirstTamedGameTime(long gameTime) {
        personality.setFirstTamedGameTime(gameTime);
        this.entityData.set(FIRST_TAMED_TIME, personality.getFirstTamedGameTime());
    }

    public void addDistanceTraveled(long delta) {
        personality.addDistanceTraveled(delta);
        this.entityData.set(DIST_TRAVELED, personality.getDistanceTraveledWithOwner());
    }

    public void setAgeYears(int years) {
        personality.setAgeYears(years);
        this.entityData.set(AGE_YEARS, personality.getAgeYears());
    }

    public void setMajorKills(int value) {
        personality.setMajorKills(value);
        this.entityData.set(MAJOR_KILLS, personality.getMajorKills());
    }

    private void syncPersonalityToData() {
        this.entityData.set(PRIMARY_TRAIT, personality.getPrimaryTrait());
        this.entityData.set(SECONDARY_TRAIT, personality.getSecondaryTrait());
        this.entityData.set(BOND_XP, personality.getBondXp());
        this.entityData.set(BOND_LEVEL, personality.getBondLevel());
        this.entityData.set(BACKSTORY_ID, personality.getBackstoryId());
        this.entityData.set(MORALE, personality.getMorale());
        this.entityData.set(RESURRECT_COUNT, personality.getTimesResurrected());
        this.entityData.set(FIRST_TAMED_TIME, personality.getFirstTamedGameTime());
        this.entityData.set(DIST_TRAVELED, personality.getDistanceTraveledWithOwner());
        this.entityData.set(MAJOR_KILLS, personality.getMajorKills());
        this.entityData.set(AGE_YEARS, personality.getAgeYears());
    }

    public void noteResurrection() {
        incrementResurrections();
    }

    public void onResurrectedEvent() {
        noteResurrection();
        if (ModConfig.safeGet(ModConfig.BOND_ENABLED)) {
            int resXp = applyBondTraitMultiplier(ModConfig.safeGet(ModConfig.BOND_RESURRECT_XP), false, false, true);
            awardBondXp(resXp);
        }
        if (ModConfig.safeGet(ModConfig.MORALE_ENABLED)) {
            adjustMorale(ModConfig.safeGet(ModConfig.MORALE_RESURRECT_DELTA).floatValue());
        }
    }

    public int getStrength() {
        return getBaseStrength() + equipmentStrengthBonus;
    }

    public int getDexterity() {
        return getBaseDexterity() + equipmentDexterityBonus;
    }

    public int getIntelligence() {
        return getBaseIntelligence() + equipmentIntelligenceBonus;
    }

    public int getEndurance() {
        return getBaseEndurance() + equipmentEnduranceBonus;
    }

    public int getBaseStrength() {
        return this.entityData.get(STR);
    }

    public int getBaseDexterity() {
        return this.entityData.get(DEX);
    }

    public int getBaseIntelligence() {
        return this.entityData.get(INTL);
    }

    public int getBaseEndurance() {
        return this.entityData.get(END);
    }

    public int getSpecialistAttributeIndex() {
        return this.entityData.get(SPECIALIST);
    }

    public void setStrength(int value) {
        this.entityData.set(STR, Math.max(1, value));
    }

    public void setDexterity(int value) {
        this.entityData.set(DEX, Math.max(1, value));
    }

    public void setIntelligence(int value) {
        this.entityData.set(INTL, Math.max(1, value));
    }

    public void setEndurance(int value) {
        this.entityData.set(END, Math.max(1, value));
    }

    public void setSpecialistAttributeIndex(int idx) {
        this.entityData.set(SPECIALIST, idx);
        this.specialistAttr = idx;
    }

    public ResourceLocation getSkinTexture() {
        int sex = Mth.clamp(getSex(), 0, CompanionData.skins.length - 1);
        ResourceLocation[] variants = CompanionData.skins[sex];
        int idx = Mth.clamp(getSkinIndex(), 0, variants.length - 1);
        return variants[idx];
    }

    public boolean hasFoodInInventory() {
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            if (CompanionData.isFood(inventory.getItem(i)))
                return true;
        }
        return false;
    }

    public ItemStack checkFood() {
        int missing = (int) Math.ceil(this.getMaxHealth() - this.getHealth());
        ItemStack best = ItemStack.EMPTY;
        float bestOverflow = Float.MAX_VALUE;
        float bestUnder = -1;

        for (int i = 0; i < this.inventory.getContainerSize(); ++i) {
            ItemStack stack = this.inventory.getItem(i);
            if (!CompanionData.isFood(stack))
                continue;

            float healValue = estimateHealingPotential(stack, missing);
            if (healValue <= 0)
                continue;

            if (healValue >= missing) {
                float overflow = healValue - missing;
                if (overflow < bestOverflow) {
                    bestOverflow = overflow;
                    best = stack;
                }
            } else if (bestOverflow == Float.MAX_VALUE && healValue > bestUnder) {
                bestUnder = healValue;
                best = stack;
            }
        }
        return best;
    }

    public boolean healFromFoodStack(ItemStack stack) {
        if (stack.isEmpty() || !CompanionData.isFood(stack))
            return false;
        float missing = this.getMaxHealth() - this.getHealth();
        if (missing <= 0.01f)
            return false;

        FoodProperties food = stack.get(DataComponents.FOOD);
        if (food != null) {
            int healAmount = Math.max(1, Math.min(food.nutrition(), (int) Math.ceil(missing)));
            playConsumptionEffects(stack);
            stack.shrink(1);
            this.heal(healAmount);
            applyFoodEffects(food);
            food.usingConvertsTo().ifPresent(this::storeOrDrop);
            return true;
        }

        if (CompanionData.isHealingPotion(stack)) {
            ItemStack potionCopy = stack.copyWithCount(1); // preserve effects before shrinking
            playConsumptionEffects(potionCopy);
            stack.shrink(1);
            applyPotionEffects(potionCopy);
            storeOrDrop(new ItemStack(Items.GLASS_BOTTLE));
            return true;
        }

        return false;
    }

    private float estimateHealingPotential(ItemStack stack, float missingHealth) {
        FoodProperties food = stack.get(DataComponents.FOOD);
        if (food != null) {
            return Math.min(food.nutrition(), missingHealth);
        }
        if (!CompanionData.isHealingPotion(stack))
            return 0;

        float healAmount = 0f;
        PotionContents contents = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
        for (MobEffectInstance effect : contents.getAllEffects()) {
            if (effect.getEffect().is(MobEffects.HEAL)) {
                healAmount += 4f * (effect.getAmplifier() + 1);
            } else if (effect.getEffect().is(MobEffects.REGENERATION)) {
                healAmount += (effect.getDuration() * (effect.getAmplifier() + 1)) / 50f;
            } else if (effect.getEffect().is(MobEffects.ABSORPTION)) {
                healAmount += 4f * (effect.getAmplifier() + 1) * 0.5f; // weight shields lightly
            }
        }
        return Math.min(healAmount, missingHealth + 8); // let regen/absorb count a bit above missing
    }

    private void applyFoodEffects(FoodProperties food) {
        if (this.level().isClientSide())
            return;
        for (FoodProperties.PossibleEffect possible : food.effects()) {
            if (this.random.nextFloat() <= possible.probability()) {
                this.addEffect(possible.effect());
            }
        }
    }

    private void applyPotionEffects(ItemStack stack) {
        if (this.level().isClientSide())
            return;
        PotionContents contents = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
        for (MobEffectInstance effect : contents.getAllEffects()) {
            if (effect.getEffect().value().isInstantenous()) {
                effect.getEffect().value().applyInstantenousEffect(null, null, this, effect.getAmplifier(), 1.0D);
            } else {
                this.addEffect(new MobEffectInstance(effect));
            }
        }
    }

    private void playConsumptionEffects(ItemStack stack) {
        if (this.level().isClientSide())
            return;
        var sound = stack.getUseAnimation() == UseAnim.DRINK ? stack.getItem().getDrinkingSound()
                : stack.getItem().getEatingSound();
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(), sound, this.getSoundSource(), 0.7F,
                1.0F + (this.getRandom().nextFloat() - 0.5F) * 0.2F);
        for (int j = 0; j < 5; ++j) {
            double dx = this.getRandom().nextGaussian() * 0.02D;
            double dy = this.getRandom().nextGaussian() * 0.02D;
            double dz = this.getRandom().nextGaussian() * 0.02D;
            this.level().addParticle(
                    new net.minecraft.core.particles.ItemParticleOption(net.minecraft.core.particles.ParticleTypes.ITEM,
                            stack.copyWithCount(1)),
                    this.getX() + (double) (this.getRandom().nextFloat() * 0.4F - 0.2F),
                    this.getY() + this.getBbHeight() * 0.8D,
                    this.getZ() + (double) (this.getRandom().nextFloat() * 0.4F - 0.2F),
                    dx, dy, dz);
        }
    }

    private void storeOrDrop(ItemStack stack) {
        if (stack.isEmpty())
            return;
        ItemStack remainder = this.inventory.addItem(stack);
        if (!remainder.isEmpty()) {
            this.spawnAtLocation(remainder);
        }
    }

    /* ---------- Interaction ---------- */

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);
        if (hand == InteractionHand.MAIN_HAND) {
            if (!this.isTame() && !this.level().isClientSide()) {
                if (foodRequirements.isEmpty()) {
                    assignFoodRequirements();
                }
                if (foodRequirements.containsKey(held.getItem())) {
                    int remaining = foodRequirements.get(held.getItem());
                    if (remaining > 0) {
                        held.shrink(1);
                        foodRequirements.put(held.getItem(), remaining - 1);
                        syncFoodRequirements();
                        if (foodRequirements.values().stream().allMatch(v -> v <= 0)) {
                            this.tame(player);
                            setFirstTamedGameTime(this.level().getGameTime());
                            syncPersonalityToData();
                            player.sendSystemMessage(Component.translatable("chat.type.text", this.getDisplayName(),
        Component.literal("谢谢！")));
player.sendSystemMessage(Component.literal("同伴已加入"));
                            setPatrolPos(null);
                            setPatrolling(false);
                            setFollowing(true);
                            setPatrolRadius(4);
                            if (patrolGoal != null)
                                patrolGoal.radius = 4;
                            if (moveBackGoal != null)
                                moveBackGoal.radius = 4;
                        } else if (foodRequirements.get(held.getItem()) == 0) {
                            player.sendSystemMessage(Component.translatable("chat.type.text", this.getDisplayName(),
                                    CompanionData.ENOUGH_FOOD[this.random
                                            .nextInt(CompanionData.ENOUGH_FOOD.length)]));
                        } else {
                            player.sendSystemMessage(Component.translatable("chat.type.text", this.getDisplayName(),
                                    CompanionData.tameFail[this.random.nextInt(CompanionData.tameFail.length)]));
                        }
                    } else {
                        player.sendSystemMessage(Component.translatable("chat.type.text", this.getDisplayName(),
                                CompanionData.ENOUGH_FOOD[this.random.nextInt(CompanionData.ENOUGH_FOOD.length)]));
                    }
                } else {
                    player.sendSystemMessage(Component.translatable("chat.type.text", this.getDisplayName(),
                            CompanionData.WRONG_FOOD[this.random.nextInt(CompanionData.WRONG_FOOD.length)]));
                    player.sendSystemMessage(Component.literal(getFoodStatus()));
                }
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            } else {
                if (this.isAlliedTo(player)) {
                    if (!this.level().isClientSide() && CompanionData.isFood(held) && this.getHealth() < this.getMaxHealth() - 0.1F) {
                        ItemStack single = held.copyWithCount(1);
                        if (healFromFoodStack(single)) {
                            held.shrink(1);
                            int feedXp = applyBondTraitMultiplier(ModConfig.safeGet(ModConfig.BOND_FEED_XP), true, false, false);
                            awardBondXp(feedXp);
                            if (ModConfig.safeGet(ModConfig.MORALE_ENABLED)) {
                                adjustMorale(ModConfig.safeGet(ModConfig.MORALE_FEED_DELTA).floatValue());
                            }
                            return InteractionResult.CONSUME;
                        }
                    }
                    // Let the Companion Mover handle interaction (even when sneaking) to avoid
                    // triggering sit/GUI.
                    if (held.is(ModItems.COMPANION_MOVER.get())) {
                        return InteractionResult.PASS;
                    }
                    if (player.isShiftKeyDown()) {
                        if (!this.level().isClientSide()) {
                            toggleSit((ServerPlayer) player);
                        }
                    } else {
                        if (!this.level().isClientSide()) {
                            openGui((ServerPlayer) player);
                        }
                    }
                }
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }
        }
        return super.mobInteract(player, hand);
    }

    private void toggleSit(ServerPlayer player) {
        if (!this.isOrderedToSit()) {
            this.setOrderedToSit(true);
            Component text = Component.literal("好的主人,我就在这里等着您。");
            player.sendSystemMessage(Component.translatable("chat.type.text", this.getDisplayName(), text));
        } else {
            this.setOrderedToSit(false);
            Component text = Component.literal("我四处走走啦~。");
            player.sendSystemMessage(Component.translatable("chat.type.text", this.getDisplayName(), text));
        }
    }

    public void openGui(ServerPlayer player) {
        MenuProvider provider = new SimpleMenuProvider(
                (id, inv, p) -> new CompanionMenu(id, inv, this),
                getDisplayName());
        player.openMenu(provider, buf -> buf.writeVarInt(getId()));
    }

    private void assignFoodRequirements() {
        Map<Item, Integer> newReq = CompanionData.getRandomFoodRequirement(rand);
        foodRequirements.clear();
        foodRequirements.putAll(newReq);
        var entries = foodRequirements.entrySet().stream().toList();
        this.entityData.set(FOOD1, BuiltInRegistries.ITEM.getKey(entries.get(0).getKey()).toString());
        this.entityData.set(FOOD1_AMT, entries.get(0).getValue());
        this.entityData.set(FOOD2, BuiltInRegistries.ITEM.getKey(entries.get(1).getKey()).toString());
        this.entityData.set(FOOD2_AMT, entries.get(1).getValue());
    }

    private void syncFoodRequirements() {
        if (foodRequirements.isEmpty())
            return;
        foodRequirements.forEach((item, count) -> {
            String id = BuiltInRegistries.ITEM.getKey(item).toString();
            if (id.equals(entityData.get(FOOD1))) {
                entityData.set(FOOD1_AMT, count);
            } else if (id.equals(entityData.get(FOOD2))) {
                entityData.set(FOOD2_AMT, count);
            }
        });
    }

private String prettyItemName(String id) {
    ResourceLocation rl = ResourceLocation.tryParse(id);
    if (rl == null) return id;
    Item item = BuiltInRegistries.ITEM.get(rl);
    String cn = CompanionData.getChineseItemName(item);
    System.out.println("prettyItemName: " + id + " -> " + cn); // 控制台输出
    return cn;
}

    @Override
    public boolean wantsToAttack(LivingEntity target, LivingEntity owner) {
        if (target instanceof SummonedWitherSkeleton) {
            // Companion summons are utility allies; never mark them as valid targets.
            return false;
        }
        return super.wantsToAttack(target, owner);
    }

    @Override
    public boolean isAlliedTo(Entity other) {
        if (other instanceof SummonedWitherSkeleton skeleton) {
            // Treat any summoned wither skeleton as an ally so cross-class parties stay
            // cooperative.
            return true;
        }
        return super.isAlliedTo(other);
    }

    /* ---------- Breeding / persistence ---------- */

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.put("Inventory", this.inventory.createTag(this.registryAccess()));
        tag.putInt("skin", this.getSkinIndex());
        tag.putString("CustomSkinUrl", this.entityData.get(CUSTOM_SKIN_URL));
        tag.putBoolean("Eating", this.isEating());
        tag.putBoolean("Alert", this.isAlert());
        tag.putBoolean("Hunting", this.isHunting());
        tag.putBoolean("Patrolling", this.isPatrolling());
        tag.putBoolean("Following", this.isFollowing());
        tag.putBoolean("Guarding", this.isGuarding());
        tag.putBoolean("SprintEnabled", this.isSprintEnabled());
        tag.putBoolean("Pickup", this.isPickupEnabled());
        tag.putInt("radius", this.getPatrolRadius());
        tag.putInt("sex", this.getSex());
        tag.putInt("baseHealth", this.getBaseHealth());
        tag.putFloat("XpP", this.experienceProgress);
        tag.putInt("XpLevel", this.getExpLvl());
        tag.putInt("XpTotal", this.totalExperience);
        tag.putInt("KillCount", this.getKillCount());
        tag.putString("food1", entityData.get(FOOD1));
        tag.putString("food2", entityData.get(FOOD2));
        tag.putInt("food1_amt", entityData.get(FOOD1_AMT));
        tag.putInt("food2_amt", entityData.get(FOOD2_AMT));
        tag.putInt("Strength", getBaseStrength());
        tag.putInt("Dexterity", getBaseDexterity());
        tag.putInt("Intelligence", getBaseIntelligence());
        tag.putInt("Endurance", getBaseEndurance());
        tag.putInt("SpecialistAttr", getSpecialistAttributeIndex());
        if (this.getPatrolPos().isPresent()) {
            int[] patrolPos = { this.getPatrolPos().get().getX(), this.getPatrolPos().get().getY(),
                    this.getPatrolPos().get().getZ() };
            tag.putIntArray("patrol_pos", patrolPos);
        }
        CompoundTag personalityTag = new CompoundTag();
        personality.saveTo(personalityTag);
        tag.put("Personality", personalityTag);
        tag.putInt("AgeYears", personality.getAgeYears());
        tag.putLong("AgeLastCheck", personality.getLastAgeCheckGameTime());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setSkinIndex(tag.getInt("skin"));
        if (tag.contains("CustomSkinUrl")) {
            this.setCustomSkinUrl(tag.getString("CustomSkinUrl"));
        }
        this.setEating(tag.getBoolean("Eating"));
        this.setAlert(tag.getBoolean("Alert"));
        this.setHunting(tag.getBoolean("Hunting"));
        this.setPatrolling(tag.getBoolean("Patrolling"));
        this.setFollowing(tag.getBoolean("Following"));
        this.setGuarding(tag.getBoolean("Guarding"));
        if (tag.contains("SprintEnabled")) {
            this.setSprintEnabled(tag.getBoolean("SprintEnabled"));
        } else if (tag.contains("Stationery")) {
            // Backward compatibility: old saves used Stationery flag; treat "not
            // stationary" as sprint off.
            this.setSprintEnabled(false);
        }
        this.setPickupEnabled(tag.contains("Pickup") ? tag.getBoolean("Pickup") : true);
        this.setPatrolRadius(tag.getInt("radius"));
        this.setSex(tag.getInt("sex"));
        this.experienceProgress = tag.getFloat("XpP");
        this.totalExperience = tag.getInt("XpTotal");
        this.setExpLvl(tag.getInt("XpLevel"));
        setKillCount(tag.contains("KillCount") ? tag.getInt("KillCount") : 0);
        syncExpProgress();
        entityData.set(FOOD1, tag.getString("food1"));
        entityData.set(FOOD2, tag.getString("food2"));
        entityData.set(FOOD1_AMT, tag.getInt("food1_amt"));
        entityData.set(FOOD2_AMT, tag.getInt("food2_amt"));
        foodRequirements.clear();
        ResourceLocation id1 = ResourceLocation.parse(entityData.get(FOOD1));
        ResourceLocation id2 = ResourceLocation.parse(entityData.get(FOOD2));
        foodRequirements.put(BuiltInRegistries.ITEM.get(id1), entityData.get(FOOD1_AMT));
        foodRequirements.put(BuiltInRegistries.ITEM.get(id2), entityData.get(FOOD2_AMT));
        if (tag.getInt("baseHealth") == 0) {
            this.setBaseHealth(ModConfig.safeGet(ModConfig.BASE_HEALTH));
        } else {
            this.setBaseHealth(tag.getInt("baseHealth"));
        }
        setSpecialistAttributeIndex(tag.contains("SpecialistAttr") ? tag.getInt("SpecialistAttr") : -1);
        if (tag.contains("Personality", 10)) {
            personality.loadFrom(tag.getCompound("Personality"));
        } else {
            // backward compatibility if older saves carry individual keys
            personality.setPrimaryTrait(tag.getString(CompanionPersonality.KEY_PRIMARY));
            personality.setSecondaryTrait(tag.getString(CompanionPersonality.KEY_SECONDARY));
            personality.setBondXp(tag.getInt(CompanionPersonality.KEY_BOND_XP));
            personality.setBondLevel(tag.getInt(CompanionPersonality.KEY_BOND_LEVEL));
            personality.setBackstoryId(tag.getString(CompanionPersonality.KEY_BACKSTORY));
            personality.setMorale(tag.getFloat(CompanionPersonality.KEY_MORALE));
            if (tag.contains(CompanionPersonality.KEY_FIRST_TAMED)) {
                personality.setFirstTamedGameTime(tag.getLong(CompanionPersonality.KEY_FIRST_TAMED));
            }
        }
        if (!tag.contains("Personality", 10) && tag.contains("DistanceTravel")) {
            personality.setDistanceTraveled(tag.getLong("DistanceTravel"));
        }
        if (tag.contains("AgeYears")) {
            personality.setAgeYears(tag.getInt("AgeYears"));
        }
        if (tag.contains("AgeLastCheck")) {
            personality.setLastAgeCheckGameTime(tag.getLong("AgeLastCheck"));
        }
        if (tag.contains("Inventory", 9)) {
            this.inventory.fromTag(tag.getList("Inventory", 10), this.registryAccess());
        }
        syncPersonalityToData();
        // reset tracking anchors post-load
        this.lastTrackX = this.getX();
        this.lastTrackY = this.getY();
        this.lastTrackZ = this.getZ();
        // Backfill missing flavor data for pre-journal companions
        rollMissingFlavorData();
        if (tag.contains("patrol_pos")) {
            int[] positions = tag.getIntArray("patrol_pos");
            setPatrolPos(new BlockPos(positions[0], positions[1], positions[2]));
        }
        if (tag.contains("radius")) {
            patrolGoal = new PatrolGoal(this, 60, tag.getInt("radius"));
            moveBackGoal = new MoveBackToPatrolGoal(this, tag.getInt("radius"));
            this.goalSelector.addGoal(3, moveBackGoal);
            this.goalSelector.addGoal(3, patrolGoal);
        }
        this.setItemSlot(EquipmentSlot.FEET, ItemStack.EMPTY);
        this.setItemSlot(EquipmentSlot.LEGS, ItemStack.EMPTY);
        this.setItemSlot(EquipmentSlot.CHEST, ItemStack.EMPTY);
        this.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
        this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        checkArmor();
        if (tag.contains("Strength")) {
            setStrength(tag.getInt("Strength"));
            setDexterity(tag.getInt("Dexterity"));
            setIntelligence(tag.getInt("Intelligence"));
            setEndurance(tag.getInt("Endurance"));
        } else {
            assignRpgAttributes();
        }
        recomputeEquipmentAttributeBonuses();
        applyRpgAttributeModifiers();
        clampHealthToMax();
    }

    /* ---------- Spawning ---------- */

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel level, AgeableMob parent) {
        return null; // companions do not breed
    }

    @Override
    public MobCategory getClassification(boolean forSpawnCount) {
        return MobCategory.AMBIENT;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return CompanionData.isFood(stack);
    }

    @Override
    public void tick() {
        if (this.level().isClientSide()) {
            // Ensure swing timers advance each frame on the client so attackAnim decays
            // naturally.
            this.updateSwingTime();
        }
        if (this.level().isClientSide()) {
            int swingTick = this.entityData.get(LAST_SWING_TICK);
            if (swingTick != lastAppliedSwingTick) {
                lastAppliedSwingTick = swingTick;
                // Replay the swing locally to guarantee a visible animation even if packets
                // were dropped/suppressed.
                this.swinging = true;
                this.swingTime = 0;
                this.oAttackAnim = 0.0F;
                this.attackAnim = 0.0F;
                this.swingingArm = InteractionHand.MAIN_HAND;
                super.swing(InteractionHand.MAIN_HAND, true);
            }
        }
        if (!this.level().isClientSide()) {
            checkArmor();
            if (this.tickCount % 2 == 0 && isPickupEnabled() && this.isTame()) {
                collectNearbyItems();
            }
            updateSprintState();
            tickBondAndMorale();
            if (this.tickCount % 10 == 0) {
                checkStats();
                if (shouldRequestFood())
                    requestFoodFromOwner();
                LivingEntity target = this.getTarget();
                if (target != null && !target.isAlive()) {
                    this.setTarget(null);
                }
            }
            trackDistanceNearOwner();
            tickAging();
        }
        boolean equipmentChanged = recomputeEquipmentAttributeBonuses();
        if (equipmentChanged) {
            applyRpgAttributeModifiers();
            clampHealthToMax();
        }
        if (!level().isClientSide()) {
            personalityRefreshTicker++;
            if (equipmentChanged || personalityRefreshTicker >= 40) {
                personalityRefreshTicker = 0;
                refreshPersonalityModifiers();
            }
        }
        super.tick();
    }

    /**
     * Toggle sprinting based on the player-controlled flag and whether the
     * companion is actively moving/engaged.
     */
    private void updateSprintState() {
        boolean wantsSprint = isSprintEnabled() && !this.isOrderedToSit();
        boolean movingOrFighting = (this.getNavigation() != null && !this.getNavigation().isDone())
                || this.getTarget() != null;
        if (wantsSprint && movingOrFighting) {
            this.setSprinting(true);
        } else {
            this.setSprinting(false);
        }
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason,
            @Nullable SpawnGroupData spawnDataIn) {
        assignRpgAttributes();
        int baseHealth = ModConfig.safeGet(ModConfig.BASE_HEALTH) + CompanionData.getHealthModifier()
                + getEnduranceBonusHealth();
        modifyMaxHealth(baseHealth - 20, "companion base health", true);
        this.setHealth(this.getMaxHealth());
        setBaseHealth(baseHealth);
        setSex(this.random.nextInt(2));
        setSkinIndex(this.random.nextInt(CompanionData.skins[getSex()].length));
        setCustomName(Component.literal(CompanionData.getRandomName(getSex())));
        setPatrolPos(this.blockPosition());
        setPatrolling(true);
        setPatrolRadius(15);
        patrolGoal = new PatrolGoal(this, 60, getPatrolRadius());
        moveBackGoal = new MoveBackToPatrolGoal(this, getPatrolRadius());
        this.goalSelector.addGoal(3, moveBackGoal);
        this.goalSelector.addGoal(3, patrolGoal);
        setAgeYears(this.random.nextInt(18, 36)); // 18-35 inclusive
        personality.setLastAgeCheckGameTime(level.getLevel().getGameTime());
        personality.rollTraits(this.random, ModConfig.safeGet(ModConfig.TRAITS_ENABLED),
                ModConfig.safeGet(ModConfig.SECONDARY_TRAIT_CHANCE));
        personality.rollBackstory(this.random);
        personality.setMorale(0.0F);
        syncPersonalityToData();
        assignFoodRequirements();

        if (ModConfig.safeGet(ModConfig.SPAWN_ARMOR)) {
            for (int i = 0; i < 4; i++) {
                EquipmentSlot armorType = EquipmentSlot.values()[i + 2]; // FEET..HEAD
                ItemStack itemstack = CompanionData.getSpawnArmor(armorType);
                if (!itemstack.isEmpty()) {
                    this.inventory.setItem(i, itemstack);
                }
            }
            checkArmor();
        }
        recomputeEquipmentAttributeBonuses();
        applyRpgAttributeModifiers();
        clampHealthToMax();
        lastTrackX = this.getX();
        lastTrackY = this.getY();
        lastTrackZ = this.getZ();
        return super.finalizeSpawn(level, difficulty, reason, spawnDataIn);
    }

    /* ---------- Orders & actions ---------- */

    public void cycleOrders() {
        if (isFollowing()) {
            setPatrolling(true);
            setFollowing(false);
            setGuarding(false);
            setPatrolPos(blockPosition());
        } else if (isPatrolling()) {
            setPatrolling(false);
            setFollowing(false);
            setGuarding(true);
            setPatrolPos(blockPosition());
        } else {
            setPatrolling(false);
            setFollowing(true);
            setGuarding(false);
        }
    }

    public void toggleAlert() {
        setAlert(!isAlert());
    }

    public void toggleHunting() {
        setHunting(!isHunting());
    }

    public void toggleSprint() {
        setSprintEnabled(!isSprintEnabled());
    }

    private double followSpeed() {
        double base = 1.3D;
        if (hasTrait("trait_quickstep")) base += 0.05D;
        if (hasTrait("trait_cautious")) base -= 0.05D;
        return Math.max(1.05D, base);
    }

    private float followStartDistance() {
        float start = 8.0F;
        if (hasTrait("trait_cautious")) start = 10.0F;
        if (hasTrait("trait_guardian")) start = 7.0F;
        return start;
    }

    private float followStopDistance() {
        float stop = 2.5F;
        if (hasTrait("trait_cautious")) stop = 3.5F;
        if (hasTrait("trait_brave") || hasTrait("trait_guardian")) stop = 2.0F;
        return stop;
    }

    public void release() {
        this.setTame(false, true);
        this.setOwnerUUID(null);
        setFollowing(false);
        setAlert(false);
        setHunting(false);
        setPatrolPos(this.blockPosition());
        setPatrolling(true);
        setSprintEnabled(false);
        setPatrolRadius(15);
        assignFoodRequirements();
        if (this.isOrderedToSit()) {
            this.setOrderedToSit(false);
        }
    }

    /* ---------- Experience ---------- */

    public void giveExperiencePoints(int points) {
        int adjusted = Math.max(1, Math.round(points * getExperienceGainMultiplier()));
        this.experienceProgress += (float) adjusted / (float) this.getXpNeededForNextLevel();
        this.totalExperience = Mth.clamp(this.totalExperience + adjusted, 0, Integer.MAX_VALUE);
        syncExpProgress();

        while (this.experienceProgress < 0.0F) {
            float f = this.experienceProgress * (float) this.getXpNeededForNextLevel();
            if (this.getExpLvl() > 0) {
                this.giveExperienceLevels(-1);
                this.experienceProgress = 1.0F + f / (float) this.getXpNeededForNextLevel();
            } else {
                this.giveExperienceLevels(-1);
                this.experienceProgress = 0.0F;
            }
        }

        while (this.experienceProgress >= 1.0F) {
            this.experienceProgress = (this.experienceProgress - 1.0F) * (float) this.getXpNeededForNextLevel();
            this.giveExperienceLevels(1);
            this.experienceProgress /= (float) this.getXpNeededForNextLevel();
        }
        syncExpProgress();
    }

    public void giveExperienceLevels(int levels) {
        setExpLvl(getExpLvl() + levels);
        if (getExpLvl() < 0) {
            setExpLvl(0);
            this.experienceProgress = 0.0F;
            this.totalExperience = 0;
        }
        syncExpProgress();
        if (levels > 0 && this.getExpLvl() % 5 == 0 && (float) this.lastLevelUpTime < (float) this.tickCount - 100.0F) {
            this.lastLevelUpTime = this.tickCount;
        }
    }

    public int getXpNeededForNextLevel() {
        int level = this.getExpLvl();
        // MMO-style curve: gentle start, then superlinear growth so each level costs
        // meaningfully more XP
        double curve = Math.pow(level + 1, 1.35D);
        int required = (int) Math.round(20 + (curve * 10));
        return Math.max(20, required);
    }

    public void modifyMaxHealth(int change, String name, boolean permanent) {
        AttributeInstance attribute = this.getAttribute(Attributes.MAX_HEALTH);
        if (attribute == null)
            return;
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(
                com.majorbonghits.moderncompanions.ModernCompanions.MOD_ID, name.replace(" ", "_"));
        attribute.removeModifier(id);
        AttributeModifier modifier = new AttributeModifier(id, change, AttributeModifier.Operation.ADD_VALUE);
        if (permanent) {
            attribute.addPermanentModifier(modifier);
        } else {
            attribute.addTransientModifier(modifier);
        }
    }

    public void checkStats() {
        if ((int) this.getMaxHealth() != getBaseHealth() + (getExpLvl() / 3)) {
            if (getExpLvl() / 3 != 0) {
                modifyMaxHealth(getExpLvl() / 3, "companion level health", false);
            }
        }
    }

    private void syncExpProgress() {
        if (!this.level().isClientSide) {
            this.entityData.set(EXP_PROGRESS, this.experienceProgress);
        }
    }

    /* ---------- Combat & equipment ---------- */

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.getEntity() == this.getOwner() && !ModConfig.safeGet(ModConfig.FRIENDLY_FIRE_PLAYER)) {
            return false;
        }
        if (source.is(net.minecraft.tags.DamageTypeTags.IS_FALL) && !ModConfig.safeGet(ModConfig.FALL_DAMAGE)) {
            return false;
        }
        float before = this.getHealth();
        float adjusted = applyEnduranceResistance(source, amount);
        hurtArmor(source, adjusted);
        if (ModConfig.safeGet(ModConfig.MORALE_ENABLED)) {
            float projected = before - adjusted;
            if (projected <= this.getMaxHealth() * 0.25F && this.tickCount - lastNearDeathTick > 200) {
                adjustMorale(ModConfig.safeGet(ModConfig.MORALE_NEAR_DEATH_DELTA).floatValue());
                lastNearDeathTick = this.tickCount;
            }
        }
        return super.hurt(source, adjusted);
    }

    @Override
    public void die(DamageSource source) {
        if (!this.level().isClientSide()) {
            if (this instanceof Beastmaster beastmaster) {
                beastmaster.forceDespawnPet();
            }
            dropResurrectionScroll();
        }
        super.die(source);
    }

    public void hurtArmor(DamageSource source, float amount) {
        if (!(amount <= 0.0F)) {
            amount /= 4.0F;
            if (amount < 1.0F)
                amount = 1.0F;

            for (ItemStack itemstack : this.getArmorSlots()) {
                if (itemstack.getItem() instanceof ArmorItem armorItem) {
                    itemstack.hurtAndBreak((int) amount, this, armorItem.getEquipmentSlot());
                }
            }
        }
    }

    @Override
    protected void dropEquipment() {
        // Override to prevent duplicating the stored inventory when a scroll drops.
    }

    private void dropResurrectionScroll() {
        if (!this.isTame()) {
            return; // only tamed companions drop scrolls
        }
        ItemStack scroll = ResurrectionScrollItem.createFromCompanion(this,
                ModItems.RESURRECTION_SCROLL.get());
        ItemEntity item = this.spawnAtLocation(scroll);
        if (item != null) {
            item.setUnlimitedLifetime();
        }
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        forceSwingAnimation(InteractionHand.MAIN_HAND);
        ItemStack itemstack = this.getMainHandItem();
        if (!this.level().isClientSide && !itemstack.isEmpty() && entity instanceof LivingEntity) {
            itemstack.hurtAndBreak(1, this, EquipmentSlot.MAINHAND);
            if (this.getMainHandItem().isEmpty() && this.isTame() && this.getOwner() != null) {
                Component broken = Component.literal("我的武器坏了！");
                this.getOwner()
                        .sendSystemMessage(Component.translatable("chat.type.text", this.getDisplayName(), broken));
            }
        }
        return super.doHurtTarget(entity);
    }

    /**
     * Unconditionally broadcast a swing animation, bypassing the internal "already
     * swinging" guard
     * so rapid hits and server-only damage paths still show the attack motion to
     * all clients.
     */
    private void forceSwingAnimation(InteractionHand hand) {
        if (!(this.level() instanceof ServerLevel server))
            return;
        this.swingTime = 0;
        this.swinging = true;
        this.swingingArm = hand;
        this.entityData.set(LAST_SWING_TICK, this.tickCount);
        ServerChunkCache chunks = server.getChunkSource();
        chunks.broadcastAndSend(this, new ClientboundAnimatePacket(this, hand == InteractionHand.MAIN_HAND ? 0 : 3));
    }

    public void checkArmor() {
        ItemStack head = this.getItemBySlot(EquipmentSlot.HEAD);
        ItemStack chest = this.getItemBySlot(EquipmentSlot.CHEST);
        ItemStack legs = this.getItemBySlot(EquipmentSlot.LEGS);
        ItemStack feet = this.getItemBySlot(EquipmentSlot.FEET);
        for (int i = 0; i < this.inventory.getContainerSize(); ++i) {
            ItemStack itemstack = this.inventory.getItem(i);
            if (itemstack.getItem() instanceof ArmorItem armorItem) {
                switch (armorItem.getEquipmentSlot()) {
                    case HEAD -> {
                        if (head.isEmpty() || CompanionData.isBetterArmor(itemstack, head))
                            setItemSlot(EquipmentSlot.HEAD, itemstack);
                    }
                    case CHEST -> {
                        if (chest.isEmpty() || CompanionData.isBetterArmor(itemstack, chest))
                            setItemSlot(EquipmentSlot.CHEST, itemstack);
                    }
                    case LEGS -> {
                        if (legs.isEmpty() || CompanionData.isBetterArmor(itemstack, legs))
                            setItemSlot(EquipmentSlot.LEGS, itemstack);
                    }
                    case FEET -> {
                        if (feet.isEmpty() || CompanionData.isBetterArmor(itemstack, feet))
                            setItemSlot(EquipmentSlot.FEET, itemstack);
                    }
                }
            }
        }
    }

    public void checkWeapon() {
        // base class intentionally does nothing; subclasses choose weapons
    }

    /* ---------- Network-driven flag setters ---------- */
    public void applyFlag(String flag, boolean value) {
        switch (flag) {
            case "follow" -> setFollowing(value);
            case "patrol" -> {
                setPatrolling(value);
                setFollowing(!value);
                setGuarding(false);
                if (value)
                    setPatrolPos(blockPosition());
            }
            case "guard" -> {
                setGuarding(value);
                setPatrolling(false);
                setFollowing(!value);
                if (value)
                    setPatrolPos(blockPosition());
            }
            case "hunt" -> setHunting(value);
            case "alert" -> setAlert(value);
            case "sprint" -> setSprintEnabled(value);
            case "pickup" -> setPickupEnabled(value);
            default -> {
            }
        }
    }

    public boolean getFlagValue(String flag) {
        return switch (flag) {
            case "follow" -> isFollowing();
            case "patrol" -> isPatrolling();
            case "guard" -> isGuarding();
            case "hunt" -> isHunting();
            case "alert" -> isAlert();
            case "sprint" -> isSprintEnabled();
            case "pickup" -> isPickupEnabled();
            default -> false;
        };
    }

    /**
     * Gently attract and collect nearby item entities into the companion's
     * inventory to emulate player pickup.
     */
    private void collectNearbyItems() {
        double range = 3.0D;
        var box = this.getBoundingBox().inflate(range);
        for (ItemEntity item : this.level().getEntitiesOfClass(ItemEntity.class, box,
                e -> e.isAlive() && !e.hasPickUpDelay())) {
            if (item.getItem().isEmpty())
                continue;
            // Blacklist certain items from being auto-picked up (e.g., resurrection
            // scrolls).
            if (item.getItem().is(com.majorbonghits.moderncompanions.core.ModItems.RESURRECTION_SCROLL.get()))
                continue;
            var pull = this.position().subtract(item.position());
            if (pull.lengthSqr() > 0.01) {
                item.setDeltaMovement(item.getDeltaMovement().scale(0.9).add(pull.normalize().scale(0.08)));
            }
            if (this.distanceToSqr(item) <= 2.25D) {
                ItemStack stack = item.getItem();
                ItemStack leftover = tryInsertBackpackFirst(stack);
                if (!leftover.isEmpty()) {
                    leftover = this.inventory.addItem(leftover);
                }
                this.inventory.setChanged();
                if (leftover.isEmpty()) {
                    item.discard();
                } else {
                    item.setItem(leftover);
                }
            }
        }
    }

    /**
     * Common shield detector so we don't place shields into the main hand when falling back.
     */
    protected boolean isShieldItem(ItemStack stack) {
        if (stack.isEmpty()) return false;
        if (stack.is(Items.SHIELD) || stack.is(TagsInit.Items.SHIELDS)) return true;
        return stack.getItem().builtInRegistryHolder().unwrapKey()
                .map(key -> key.location().getPath().toLowerCase(Locale.ROOT).contains("shield"))
                .orElse(false);
    }

    /**
     * Apply or remove the flat preferred-weapon bonus.
     */
    protected void setPreferredWeaponBonus(boolean enabled) {
        var damage = this.getAttribute(Attributes.ATTACK_DAMAGE);
        if (damage == null) return;
        damage.removeModifier(PREFERRED_WEAPON_MOD);
        if (enabled) {
            damage.addTransientModifier(new AttributeModifier(PREFERRED_WEAPON_MOD, 2.0D, AttributeModifier.Operation.ADD_VALUE));
        }
    }

    /**
     * If Sophisticated Backpacks + Curios are present and the companion has a backpack
     * equipped in the back slot, try inserting into it before using the normal inventory.
     */
    private ItemStack tryInsertBackpackFirst(ItemStack stack) {
        if (stack.isEmpty()) return stack;
        if (!net.neoforged.fml.ModList.get().isLoaded("curios") || !net.neoforged.fml.ModList.get().isLoaded("sophisticatedbackpacks")) {
            return stack;
        }
        try {
            var handlerOpt = top.theillusivec4.curios.api.CuriosApi.getCuriosInventory(this);
            if (handlerOpt.isEmpty()) return stack;
            var handler = handlerOpt.get();
            var backOpt = handler.getStacksHandler("back");
            if (backOpt.isEmpty()) return stack;
            var stacks = backOpt.get().getStacks();
            for (int i = 0; i < stacks.getSlots(); i++) {
                ItemStack backpack = stacks.getStackInSlot(i);
                if (backpack.isEmpty()) {
                    continue;
                }
                if (!backpack.getItem().builtInRegistryHolder().key().location().getNamespace().equals("sophisticatedbackpacks")) {
                    continue;
                }

                net.neoforged.neoforge.items.IItemHandler handlerItem = null;
                // Preferred: direct wrapper (matches how SB exposes inventory for IO)
                try {
                    var wrapperCls = Class.forName("net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackWrapper");
                    var fromStack = wrapperCls.getMethod("fromStack", ItemStack.class);
                    Object wrapper = fromStack.invoke(null, backpack);
                    var getInv = wrapperCls.getMethod("getInventoryForInputOutput");
                    handlerItem = (net.neoforged.neoforge.items.IItemHandler) getInv.invoke(wrapper);
                } catch (Exception ignored) { }

                // Fallback: item capability if wrapper failed
                if (handlerItem == null) {
                    handlerItem = backpack.getCapability(net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.ITEM, null);
                }
                if (handlerItem == null) continue;

                ItemStack remainder = net.neoforged.neoforge.items.ItemHandlerHelper.insertItemStacked(handlerItem, stack, false);
                if (remainder.getCount() != stack.getCount()) {
                    // Inserted at least part; stop processing other containers
                    return remainder;
                }
                stack = remainder;
            }
        } catch (Exception ignored) {
        }
        return stack;
    }

    /* ---------- RPG attribute generation & effects ---------- */

    private void assignRpgAttributes() {
        int[] stats = { 4, 4, 4, 4 }; // STR, DEX, INT, END base
        for (int i = 0; i < 23; i++) {
            stats[this.random.nextInt(stats.length)]++;
        }
        double specialistChance = 0.02D + (this.random.nextDouble() * 0.04D); // 2–6%
        if (this.random.nextDouble() < specialistChance) {
            int pick = this.random.nextInt(stats.length);
            stats[pick] += 5;
            setSpecialistAttributeIndex(pick);
        } else {
            setSpecialistAttributeIndex(-1);
        }
        setStrength(stats[0]);
        setDexterity(stats[1]);
        setIntelligence(stats[2]);
        setEndurance(stats[3]);
    }

    /**
     * Recalculate enchantment-driven bonuses from the companion's worn armor.
     * Returns true when a change is detected so downstream attribute application
     * can be refreshed.
     */
    private boolean recomputeEquipmentAttributeBonuses() {
        int newStr = getEnchantmentBonus(ModEnchantments.EMPOWER);
        int newDex = getEnchantmentBonus(ModEnchantments.NIMBILITY);
        int newInt = getEnchantmentBonus(ModEnchantments.ENLIGHTENMENT);
        int newEnd = getEnchantmentBonus(ModEnchantments.VITALITY);

        if (newStr == equipmentStrengthBonus && newDex == equipmentDexterityBonus
                && newInt == equipmentIntelligenceBonus && newEnd == equipmentEnduranceBonus) {
            return false;
        }

        equipmentStrengthBonus = newStr;
        equipmentDexterityBonus = newDex;
        equipmentIntelligenceBonus = newInt;
        equipmentEnduranceBonus = newEnd;
        return true;
    }

    private int getEnchantmentBonus(ResourceKey<Enchantment> enchantment) {
        var registry = this.level().registryAccess().registry(Registries.ENCHANTMENT);
        if (registry.isEmpty())
            return 0;
        int total = 0;
        for (ItemStack armor : this.getArmorSlots()) {
            total += registry.get().getHolder(enchantment)
                    .map(holder -> EnchantmentHelper.getItemEnchantmentLevel(holder, armor))
                    .orElse(0);
        }
        return total;
    }

    private void applyRpgAttributeModifiers() {
        applyStrengthModifiers();
        applyDexterityModifiers();
        applyEnduranceModifiers();
        // intelligence currently drives XP gain inside giveExperiencePoints; no
        // attribute modifier needed
        refreshPersonalityModifiers();
    }

    private void applyStrengthModifiers() {
        double delta = (getStrength() - 4) * 0.25D; // +0.25 damage per point over base
        applyModifier(Attributes.ATTACK_DAMAGE, "rpg_strength_damage", delta, AttributeModifier.Operation.ADD_VALUE);

        double kb = (getStrength() - 4) * 0.03D;
        applyModifier(Attributes.ATTACK_KNOCKBACK, "rpg_strength_knockback", kb, AttributeModifier.Operation.ADD_VALUE);
    }

    private void applyDexterityModifiers() {
        double speed = (getDexterity() - 4) * 0.003D;
        applyModifier(Attributes.MOVEMENT_SPEED, "rpg_dex_speed", speed, AttributeModifier.Operation.ADD_VALUE);

        double atkSpeed = (getDexterity() - 4) * 0.04D;
        applyModifier(Attributes.ATTACK_SPEED, "rpg_dex_attack_speed", atkSpeed, AttributeModifier.Operation.ADD_VALUE);

        double kbResist = Math.max(0.0D, (getDexterity() - 10) * 0.01D); // slight dodge feel at high dex
        applyModifier(Attributes.KNOCKBACK_RESISTANCE, "rpg_dex_kb_resist", kbResist,
                AttributeModifier.Operation.ADD_VALUE);
    }

    private void applyEnduranceModifiers() {
        int baseline = ModConfig.BASE_HEALTH != null ? ModConfig.safeGet(ModConfig.BASE_HEALTH) : 20;
        int baseBonusHealth = getEnduranceBonusHealth();
        int desiredBase = Math.max(getBaseHealth(), baseline + baseBonusHealth);
        setBaseHealth(desiredBase);
        modifyMaxHealth(desiredBase - 20, "companion base health", true);

        int gearHealthBonus = Math.max(0, getEndurance() - getBaseEndurance());
        applyModifier(Attributes.MAX_HEALTH, "rpg_end_gear_health", gearHealthBonus,
                AttributeModifier.Operation.ADD_VALUE);

        double kbResist = Math.min(0.6D, (getEndurance() - 4) * 0.02D);
        applyModifier(Attributes.KNOCKBACK_RESISTANCE, "rpg_end_kb_resist", kbResist,
                AttributeModifier.Operation.ADD_VALUE);
    }

    private void refreshPersonalityModifiers() {
        if (level().isClientSide()) return;
        var damage = this.getAttribute(Attributes.ATTACK_DAMAGE);
        var armor = this.getAttribute(Attributes.ARMOR);
        var speed = this.getAttribute(Attributes.MOVEMENT_SPEED);
        var kb = this.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
        if (damage != null) damage.removeModifier(MOD_MORALE_DAMAGE);
        if (armor != null) armor.removeModifier(MOD_MORALE_ARMOR);
        if (speed != null) speed.removeModifier(MOD_TRAIT_QUICKSTEP);
        if (speed != null) speed.removeModifier(MOD_TRAIT_RECKLESS);
        if (kb != null) kb.removeModifier(MOD_TRAIT_STALWART);
        if (damage != null) damage.removeModifier(MOD_TRAIT_BRAVE);
        if (armor != null) armor.removeModifier(MOD_TRAIT_GUARDIAN);
        if (armor != null) armor.removeModifier(MOD_TRAIT_DEVOTED);
        if (damage != null) {
            damage.removeModifier(MOD_TRAIT_NIGHT_OWL_DAMAGE);
            damage.removeModifier(MOD_TRAIT_SUN_DAMAGE);
            damage.removeModifier(MOD_TRAIT_MELANCHOLIC);
        }
        if (speed != null) {
            speed.removeModifier(MOD_TRAIT_NIGHT_OWL_SPEED);
            speed.removeModifier(MOD_TRAIT_SUN_SPEED);
        }

        float morale = getMorale();
        if (morale > 0.5f) {
            if (damage != null) {
                damage.addTransientModifier(new AttributeModifier(MOD_MORALE_DAMAGE, 0.5D, AttributeModifier.Operation.ADD_VALUE));
            }
            if (armor != null) {
                armor.addTransientModifier(new AttributeModifier(MOD_MORALE_ARMOR, 0.5D, AttributeModifier.Operation.ADD_VALUE));
            }
        } else if (morale < -0.5f) {
            if (damage != null) {
                damage.addTransientModifier(new AttributeModifier(MOD_MORALE_DAMAGE, -0.5D, AttributeModifier.Operation.ADD_VALUE));
            }
            if (armor != null) {
                armor.addTransientModifier(new AttributeModifier(MOD_MORALE_ARMOR, -0.5D, AttributeModifier.Operation.ADD_VALUE));
            }
        }

        if (hasTrait("trait_quickstep") && speed != null) {
            speed.addTransientModifier(new AttributeModifier(MOD_TRAIT_QUICKSTEP, 0.02D, AttributeModifier.Operation.ADD_VALUE));
        }
        if (hasTrait("trait_reckless") && speed != null) {
            speed.addTransientModifier(new AttributeModifier(MOD_TRAIT_RECKLESS, 0.01D, AttributeModifier.Operation.ADD_VALUE));
        }
        if (hasTrait("trait_stalwart") && kb != null) {
            kb.addTransientModifier(new AttributeModifier(MOD_TRAIT_STALWART, 0.05D, AttributeModifier.Operation.ADD_VALUE));
        }
        if (hasTrait("trait_brave") && damage != null) {
            damage.addTransientModifier(new AttributeModifier(MOD_TRAIT_BRAVE, 0.25D, AttributeModifier.Operation.ADD_VALUE));
        }
        if (hasTrait("trait_guardian") && armor != null) {
            armor.addTransientModifier(new AttributeModifier(MOD_TRAIT_GUARDIAN, 0.25D, AttributeModifier.Operation.ADD_VALUE));
        }
        if (hasTrait("trait_devoted") && armor != null) {
            armor.addTransientModifier(new AttributeModifier(MOD_TRAIT_DEVOTED, 0.15D, AttributeModifier.Operation.ADD_VALUE));
        }

        // Time-of-day traits
        if (this.level() != null) {
            boolean isDay = this.level().isDay();
            boolean isNight = !isDay;
            if (isNight && hasTrait("trait_night_owl")) {
                if (damage != null) {
                    damage.addTransientModifier(new AttributeModifier(MOD_TRAIT_NIGHT_OWL_DAMAGE, 0.25D, AttributeModifier.Operation.ADD_VALUE));
                }
                if (speed != null) {
                    speed.addTransientModifier(new AttributeModifier(MOD_TRAIT_NIGHT_OWL_SPEED, 0.01D, AttributeModifier.Operation.ADD_VALUE));
                }
            }
            if (isDay && hasTrait("trait_sun_blessed")) {
                if (damage != null) {
                    damage.addTransientModifier(new AttributeModifier(MOD_TRAIT_SUN_DAMAGE, 0.25D, AttributeModifier.Operation.ADD_VALUE));
                }
                if (speed != null) {
                    speed.addTransientModifier(new AttributeModifier(MOD_TRAIT_SUN_SPEED, 0.01D, AttributeModifier.Operation.ADD_VALUE));
                }
            }
        }

        // Melancholic: minor penalty when morale is low
        if (hasTrait("trait_melancholic") && damage != null && getMorale() < -0.2f) {
            damage.addTransientModifier(new AttributeModifier(MOD_TRAIT_MELANCHOLIC, -0.2D, AttributeModifier.Operation.ADD_VALUE));
        }
    }

    private int getEnduranceBonusHealth() {
        return Math.max(0, getBaseEndurance() - 4); // +1 hp per END over base (0.5 hearts)
    }

    private float applyEnduranceResistance(DamageSource source, float amount) {
        boolean physical = !source.is(net.minecraft.tags.DamageTypeTags.IS_FIRE)
                && !source.is(net.minecraft.tags.DamageTypeTags.IS_EXPLOSION)
                && !source.is(net.minecraft.tags.DamageTypeTags.BYPASSES_ARMOR)
                && !source.is(net.minecraft.tags.DamageTypeTags.BYPASSES_INVULNERABILITY);
        if (!physical) {
            return amount;
        }
        float reduction = (float) Math.min(0.35D, Math.max(0.0D, (getEndurance() - 4) * 0.015D));
        return amount * (1.0F - reduction);
    }

    private void clampHealthToMax() {
        float max = this.getMaxHealth();
        if (this.getHealth() > max) {
            this.setHealth(max);
        }
    }

    private void applyModifier(net.minecraft.core.Holder<net.minecraft.world.entity.ai.attributes.Attribute> attribute,
            String idName, double value, AttributeModifier.Operation op) {
        AttributeInstance instance = this.getAttribute(attribute);
        if (instance == null)
            return;
        ResourceLocation id = ResourceLocation
                .fromNamespaceAndPath(com.majorbonghits.moderncompanions.ModernCompanions.MOD_ID, idName);
        instance.removeModifier(id);
        if (value != 0.0D) {
            AttributeModifier modifier = new AttributeModifier(id, value, op);
            instance.addPermanentModifier(modifier);
        }
    }

    private float getExperienceGainMultiplier() {
        float mult = 1.0F + (float) ((getIntelligence() - 4) * 0.03D);
        if (hasTrait("trait_disciplined")) {
            mult += 0.05F;
        }
        return mult;
    }

    public boolean hasTrait(String traitId) {
        if (traitId == null || traitId.isEmpty()) return false;
        return traitId.equals(getPrimaryTraitId()) || traitId.equals(getSecondaryTraitId());
    }

    private boolean shouldRequestFood() {
        return this.isTame()
                && this.getHealth() < this.getMaxHealth() - 0.5F
                && !hasFoodInInventory()
                && this.tickCount - lastFoodRequestTick > FOOD_REQUEST_COOLDOWN_TICKS;
    }

    private void requestFoodFromOwner() {
        if (this.level().isClientSide())
            return;
        if (!this.isTame())
            return;
        lastFoodRequestTick = this.tickCount;
        if (this.getOwner() instanceof ServerPlayer player) {
            Component text = Component.literal(randomFoodRequestLine());
            player.sendSystemMessage(Component.translatable("chat.type.text", this.getDisplayName(), text));
        }
    }

    private void tickBondAndMorale() {
        if (!ModConfig.safeGet(ModConfig.BOND_ENABLED))
            return;
        if (!this.isTame())
            return;
        LivingEntity owner = this.getOwner();
        if (!(owner instanceof Player))
            return;
        double dist2 = this.distanceToSqr(owner);
        if (dist2 > 24 * 24)
            return;
        bondTickCounter++;
        int interval = ModConfig.safeGet(ModConfig.BOND_TICK_INTERVAL);
        if (bondTickCounter >= Math.max(20, interval)) {
            int base = ModConfig.safeGet(ModConfig.BOND_TIME_XP);
            awardBondXp(applyBondTraitMultiplier(base, false, true, false));
            bondTickCounter = 0;
        }
    }

    private int applyBondTraitMultiplier(int base, boolean feeding, boolean passive, boolean resurrect) {
        double mult = 1.0D;
        if (hasTrait("trait_devoted")) mult += 0.2D;
        if (feeding && hasTrait("trait_glutton")) mult += 0.15D;
        if (base <= 0) return 0;
        return Math.max(1, (int) Math.round(base * mult));
    }

    private void trackDistanceNearOwner() {
        double dx = this.getX() - lastTrackX;
        double dz = this.getZ() - lastTrackZ;
        double moved = Math.sqrt(dx * dx + dz * dz);
        lastTrackX = this.getX();
        lastTrackY = this.getY();
        lastTrackZ = this.getZ();
        if (!this.isTame()) return;
        LivingEntity owner = this.getOwner();
        if (owner == null) return;
        if (this.distanceToSqr(owner) > 24 * 24) return;
        if (moved > 0.0001D) {
            distanceAccumulator += moved;
            long whole = (long) distanceAccumulator;
            if (whole > 0) {
                addDistanceTraveled(whole);
                distanceAccumulator -= whole;
            }
        }
    }

    private void tickAging() {
        if (this.level().isClientSide()) return;
        long now = this.level().getGameTime();
        if (personality.getLastAgeCheckGameTime() < 0) {
            personality.setLastAgeCheckGameTime(now);
            return;
        }
        long elapsed = now - personality.getLastAgeCheckGameTime();
        if (elapsed >= AGE_INTERVAL_TICKS) {
            long years = elapsed / AGE_INTERVAL_TICKS;
            if (years > 0) {
                setAgeYears(personality.getAgeYears() + (int) years);
                long remainder = elapsed - years * AGE_INTERVAL_TICKS;
                personality.setLastAgeCheckGameTime(now - remainder);
            }
        }
    }

    /**
     * For older companions without newly-added flavor data, roll safe defaults.
     */
    private void rollMissingFlavorData() {
        if (personality.getPrimaryTrait().isEmpty() && personality.getSecondaryTrait().isEmpty()) {
            // Only roll traits once for legacy companions that had none.
            personality.rollTraits(this.random, ModConfig.safeGet(ModConfig.TRAITS_ENABLED),
                    ModConfig.safeGet(ModConfig.SECONDARY_TRAIT_CHANCE));
        }
        if (personality.getBackstoryId().isEmpty()) {
            personality.rollBackstory(this.random);
        }
        if (personality.getAgeYears() <= 0) {
            setAgeYears(this.random.nextInt(18, 36));
            personality.setLastAgeCheckGameTime(this.level().getGameTime());
        }
        syncPersonalityToData();
    }

  private String randomFoodRequestLine() {
    String[] lines = new String[] {
            "我受伤了——请给我点食物！",
            "哎哟，现在真的很需要吃点东西。",
            "生命值很低，有能吃的东西吗？",
            "再挨一下可能就倒了，请给点食物！",
            "有点头晕，来点食物会好点。",
            "绷带？不用。面包？好的，谢谢。",
            "我的胃在喊‘痛’，你有干粮吗？",
            "如果你喂我，我就能继续战斗。",
            "做个交易：我保你安全，你管我饱饭。",
            "吃点东西恢复得更快。",
            "我这的血条已经红透了……",
            "我都能看到重生界面了，有吃的吗？",
            "如果我倒下，都怪你和你的零食短缺。",
            "我不是饿死鬼的时候打架更厉害，只是说一声。",
            "现在正是吃零食的好时机。",
            "我并不是在演戏，但没有食物我可能会晕过去。",
            "哎哟，好疼。有什么好吃又能治伤的东西吗？",
            "我敢说，一个三明治至少能解决一半问题。",
            "先喂食，再英雄主义。行吗？",
            "我本想多抱怨几句，但我太饿了。",
            "如果你喂我，我保证暂时不再嚷嚷……就一会儿。",
            "我们进入了危险区域。请直接把食物喂给同伴。",
            "我觉得我的血量掉在了后面某个地方。有吃的吗？",
            "你忠诚的同伴急需零食。",
            "我能扛住攻击，扛不住饥饿。帮帮忙。",
            "我现在离倒下只差一个坏决定。食物或许有用。",
            "我愿意继续保护你，但我的生命值不同意。",
            "全身都疼，除了胃口。",
            "我没有生气，我只是又饿又快要死了。",
            "我可以咬敌人，也可以咬食物。你选。",
            "我敢肯定‘快死了’最怕的就是食物。",
            "好吧，我承认，我需要零食和一个拥抱。",
            "我的盔甲裂了，我也裂了。有食物吗？",
            "警告：同伴完整性剩余12%。请投喂食物。",
            "我的脾大概已经气到辞职了。有炖菜吗？",
            "如果你丢食物过来，我保证接住……大概吧。",
            "我想挺过去，但我快站不住了。有干粮吗？",
            "听到Boss音乐和我肚子叫同时响起，这正常吗？",
            "低生命值，高焦虑，零零食。糟糕的组合。",
            "如果你有面包，现在是创造奇迹的时候。",
            "药水、药膏、鸡腿……我不挑。",
            "如果我倒了，从我尸体上能摸出后悔和食物渣。",
            "在我开始嚼东西的那一刻，我就会停止抱怨。",
            "你的背包看起来很重。让我帮你减轻一下——用零食。",
            "空着肚子可没法格挡死亡。",
            "我目前的生存策略就指望你喂我了。",
            "生命药水是不错，但你试过汤吗？",
            "救命，我好像在漏血。送食物来。",
            "医嘱：多吃食物，少挨刀。",
            "如果你喂我，我就假装这一切都是计划的一部分。",
            "我很好。完全没事。开玩笑的，请喂我。",
            "想象一下，我在满血满腹的状态下会有多英勇。",
            "新任务：恢复同伴。目标：提供零食。",
            "我的血量条简直可以当恐怖故事讲了。",
            "如果饿不死我，刚才那一击也会要我的命。",
            "我现在打一个喷嚏都可能倒下。",
            "我能抗怪物，但不能扛饿。",
            "现在投喂食物将极大提高团队士气——我的士气。",
            "这感觉像是‘先吃饭，再打架’的情况。",
            "好消息是，至少我胃口还在。",
            "如果你听到‘砰’的一声，那是我倒下了。带食物来。",
            "我的内心独白一直在循环播放‘哎哟’和‘零食’。",
            "好消息：我忠诚。坏消息：我快饿死了。",
            "别让我的墓碑上写着‘死于没零食’。",
            "我现在真的很想吃点不是地板的东西。",
            "如果我有更多食物，我就不会这么濒死了。",
            "把我的故事传下去……除非你有食物，那先救我。",
            "提醒：同伴需要卡路里才能运行。",
            "已达到危急状态。现在需要紧急零食。",
            "我一半的血量没了，所有的肉干也没了。",
            "我的血量比你标准还低。用食物解决它。",
            "我们有战地医生还是战地三明治？",
            "我的身体说‘休息’，但我的胃说‘自助餐’。",
            "我经历过更好的日子，也吃过更多的零食。",
            "如果‘快死了’有味道，那一定是‘没食物’。",
            "我发誓，吃饱后我闪避得更好了。",
            "低血量已加入聊天。请用食物回复。",
            "你的同伴正在缓冲……需要食物才能继续。",
            "把这当作礼貌的濒死零食请求。",
            "我在坚守防线，但我更想拿个三明治。",
            "如果援军不来，至少让零食来吧。"
    };
    return lines[rand.nextInt(lines.length)];
}
}
