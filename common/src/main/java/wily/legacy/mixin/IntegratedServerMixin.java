package wily.legacy.mixin;

import com.mojang.datafixers.DataFixer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.client.server.LanServerPinger;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.Services;
import net.minecraft.server.WorldStem;
import net.minecraft.server.level.progress.ChunkProgressListenerFactory;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.objectweb.asm.Opcodes;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wily.legacy.LegacyMinecraftClient;
import wily.legacy.client.LegacyOptions;

import java.io.IOException;
import java.net.Proxy;
import java.util.function.BooleanSupplier;

@Mixin(IntegratedServer.class)
public abstract class IntegratedServerMixin extends MinecraftServer {
    @Shadow @Final private Minecraft minecraft;

    @Shadow public LanServerPinger lanPinger;

    @Shadow @Final private static Logger LOGGER;

    @Shadow private boolean paused;

    public IntegratedServerMixin(Thread thread, LevelStorageSource.LevelStorageAccess levelStorageAccess, PackRepository packRepository, WorldStem worldStem, Proxy proxy, DataFixer dataFixer, Services services, ChunkProgressListenerFactory chunkProgressListenerFactory) {
        super(thread, levelStorageAccess, packRepository, worldStem, proxy, dataFixer, services, chunkProgressListenerFactory);
    }

    public IntegratedServer self(){
        return (IntegratedServer) (Object) this;
    }
    @Inject(method = "tickServer", at = @At("HEAD"))
    public void tickServer(BooleanSupplier booleanSupplier, CallbackInfo ci) {
        if (LegacyMinecraftClient.manualSave){
            LegacyMinecraftClient.manualSave = false;
            getProfiler().push("manualSave");
            LOGGER.info("Saving manually...");
            this.saveEverything(false, false, false);
            getProfiler().pop();
        }
    }
    @Redirect(method = "tickServer", at = @At(value = "FIELD", target = "Lnet/minecraft/client/server/IntegratedServer;paused:Z", opcode = Opcodes.GETFIELD, ordinal = 1))
    public boolean tickServer(IntegratedServer instance) {
        return paused && ((LegacyOptions) minecraft.options).autoSaveWhenPause().get();
    }
    @Inject(method = "stopServer", at = @At(value = "HEAD"), cancellable = true)
    public void tickServer(CallbackInfo ci) {
        if (!((LegacyOptions) minecraft.options).autoSaveWhenPause().get()){
            ci.cancel();
            if (self().metricsRecorder.isRecording()) {
                self().cancelRecordingMetrics();
            }
            self().getConnection().stop();
            LOGGER.info("Stopping server");
            self().resources.close();
            try {
                self().storageSource.close();
            } catch (IOException iOException2) {
                LOGGER.error("Failed to unlock level {}", self().storageSource.getLevelId(), iOException2);
            }
            if (this.lanPinger != null) {
                this.lanPinger.interrupt();
                this.lanPinger = null;
            }
        }
    }
}
