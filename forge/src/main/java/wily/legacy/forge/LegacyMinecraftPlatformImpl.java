package wily.legacy.forge;

import dev.architectury.platform.Mod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.loading.LoadingModList;

import java.nio.file.Path;

public class LegacyMinecraftPlatformImpl {
    public static Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }

    public static TagKey<Item> getCommonItemTag(String commonTag) {
        return ItemTags.create(new ResourceLocation("forge", commonTag));
    }

    public static boolean isHiddenMod(Mod mod) {
        return false;
    }

    public static Screen getConfigScreen(Mod mod, Screen screen) {
        return ModList.get().getModContainerById(mod.getModId()).flatMap(m-> m.getCustomExtension(ConfigScreenHandler.ConfigScreenFactory.class)).map(s -> s.screenFunction().apply(Minecraft.getInstance(), screen)).orElse(null);
    }

    public static boolean isLoadingMod(String modId) {
        return LoadingModList.get().getModFileById(modId) != null;
    }
}
