package wily.legacy.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CraftingTableBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import wily.legacy.inventory.LegacyCraftingMenu;
import wily.legacy.entity.LegacyPlayer;

@Mixin(CraftingTableBlock.class)
public class CraftingTableBlockMixin {
    @Redirect(method = "useWithoutItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getMenuProvider(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/MenuProvider;"))
    public MenuProvider use(BlockState instance, Level level, BlockPos blockPos, BlockState blockState, Level level1, BlockPos blockPos1, Player player) {
        if (player instanceof LegacyPlayer p && !p.hasClassicCrafting()) return LegacyCraftingMenu.getMenuProvider(blockPos,false);
        return instance.getMenuProvider(level,blockPos);
    }
}
