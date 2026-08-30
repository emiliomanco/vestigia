package com.emiliomanco.vestigia.block.entity;

import com.emiliomanco.vestigia.menu.VestigeTableMenu;
import com.emiliomanco.vestigia.registry.ModBlockEntities;
import com.emiliomanco.vestigia.registry.ModRecipes;
import com.emiliomanco.vestigia.registry.ModSounds;
import com.emiliomanco.vestigia.ritual.VestigeRitualInput;
import com.emiliomanco.vestigia.ritual.VestigeRitualRecipe;
import com.geckolib.animatable.GeoBlockEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.util.GeckoLibUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class VestigeTableBlockEntity extends BaseContainerBlockEntity implements GeoBlockEntity {

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("vestiges_table_idle");

    private final AnimatableInstanceCache animationCache = GeckoLibUtil.createInstanceCache(this);

    public static final int SLOT_FIRST_INGREDIENT = VestigeRitualInput.SLOT_FIRST_INGREDIENT;
    public static final int INGREDIENT_COUNT = VestigeRitualInput.INGREDIENT_COUNT;
    public static final int SLOT_RESULT = INGREDIENT_COUNT;
    public static final int CONTAINER_SIZE = INGREDIENT_COUNT + 1;

    public static final int DATA_HAS_RITUAL = 0;
    public static final int DATA_COUNT = 1;

    private static final int EVALUATION_INTERVAL_TICKS = 20;

    private static final int HUM_PERIOD_TICKS = 40;

    private static final float HUM_VOLUME = 0.5F;

    private NonNullList<ItemStack> items = NonNullList.withSize(CONTAINER_SIZE, ItemStack.EMPTY);

    private int ticksUntilEvaluation;
    private int ticksUntilHum;

    private Optional<RecipeHolder<VestigeRitualRecipe>> matched = Optional.empty();

    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case DATA_HAS_RITUAL -> matched.isPresent() ? 1 : 0;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {}

        @Override
        public int getCount() {
            return DATA_COUNT;
        }
    };

    public VestigeTableBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(ModBlockEntities.VESTIGE_TABLE.get(), worldPosition, blockState);
    }

    public void serverTick(Level level, BlockPos pos) {
        hum(level, pos);
        if (--ticksUntilEvaluation > 0) {
            return;
        }
        ticksUntilEvaluation = EVALUATION_INTERVAL_TICKS;
        evaluate(level, pos);
    }

    private void hum(Level level, BlockPos pos) {
        if (!canComplete()) {
            ticksUntilHum = 0;
            return;
        }
        if (--ticksUntilHum > 0) {
            return;
        }
        ticksUntilHum = HUM_PERIOD_TICKS;
        level.playSound(null, pos, ModSounds.VESTIGE_TABLE_READY.get(), SoundSource.BLOCKS,
                HUM_VOLUME, 1.0F);
    }

    private void evaluate(Level level, BlockPos pos) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        VestigeRitualInput input = currentInput(pos);
        Optional<RecipeHolder<VestigeRitualRecipe>> found = Optional.empty();

        if (!input.isEmpty()) {
            found = serverLevel.recipeAccess()
                    .recipeMap()
                    .byType(ModRecipes.VESTIGE_RITUAL_TYPE.get())
                    .stream()
                    .filter(holder -> holder.value().matchesItems(input))
                    .findFirst();
        }

        matched = found;

        items.set(SLOT_RESULT, canComplete() ? previewResult() : ItemStack.EMPTY);

    }

    private VestigeRitualInput currentInput(BlockPos pos) {
        List<ItemStack> ingredients = new ArrayList<>(INGREDIENT_COUNT);
        for (int i = 0; i < INGREDIENT_COUNT; i++) {
            ingredients.add(items.get(SLOT_FIRST_INGREDIENT + i));
        }
        return new VestigeRitualInput(ingredients, pos);
    }

    public boolean canComplete() {
        return matched.isPresent();
    }

    public ItemStack previewResult() {
        return matched.map(holder -> holder.value().previewResult()).orElse(ItemStack.EMPTY);
    }

    public float consumeForCompletedRitual() {
        float experience = matched.map(holder -> holder.value().experience()).orElse(0.0F);
        for (int slot = SLOT_FIRST_INGREDIENT; slot < SLOT_RESULT; slot++) {
            ItemStack stack = items.get(slot);
            if (!stack.isEmpty()) {
                stack.shrink(1);
            }
        }
        setChanged();
        return experience;
    }

    public ContainerData dataAccess() {
        return dataAccess;
    }

    @Override
    public int getContainerSize() {
        return CONTAINER_SIZE;
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return slot != SLOT_RESULT;
    }

    @Override
    public void setChanged() {
        super.setChanged();
        ticksUntilEvaluation = 1;
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.vestigia.vestige_table");
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new VestigeTableMenu(containerId, inventory, this, dataAccess);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        items = NonNullList.withSize(CONTAINER_SIZE, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(input, items);
        ticksUntilEvaluation = 1;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output, items);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>("main", 0, state -> state.setAndContinue(IDLE)));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animationCache;
    }
}
