package net.misode.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.registry.Registry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.feature.util.PlacedFeatureIndexer;
import net.minecraft.world.gen.structure.Structure;
import net.misode.event.FeatureGenerationEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ChunkGenerator.class)
public abstract class ChunkGeneratorMixin {

	@Unique
	private FeatureGenerationEvent featureGenerationEvent;

	@Inject(
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/gen/feature/PlacedFeature;generate(Lnet/minecraft/world/StructureWorldAccess;Lnet/minecraft/world/gen/chunk/ChunkGenerator;Lnet/minecraft/util/math/random/Random;Lnet/minecraft/util/math/BlockPos;)Z",
			shift = At.Shift.BEFORE
		),
		method = "generateFeatures"
	)
	private void generateFeatures(
		StructureWorldAccess world, Chunk chunk, StructureAccessor structureAccessor, CallbackInfo info,
		@Local ChunkPos chunkPos,
		@Local(ordinal = 1) Registry<PlacedFeature> placedFeatureRegistry,
		@Local(ordinal = 2) int step,
		@Local PlacedFeature placedFeature
	) {
		String featureKey = placedFeatureRegistry.getKey(placedFeature).map(k -> k.getValue().toString()).orElse("unregistered");
		featureGenerationEvent = new FeatureGenerationEvent(chunkPos, world.toServerWorld().getRegistryKey(), featureKey, step);
		featureGenerationEvent.begin();
	}

	@Inject(
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/gen/feature/PlacedFeature;generate(Lnet/minecraft/world/StructureWorldAccess;Lnet/minecraft/world/gen/chunk/ChunkGenerator;Lnet/minecraft/util/math/random/Random;Lnet/minecraft/util/math/BlockPos;)Z",
			shift = At.Shift.AFTER
		),
		method = "generateFeatures"
	)
	private void generateFeaturesCommit(CallbackInfo info) {
		if (featureGenerationEvent != null) {
			featureGenerationEvent.commit();
		}
	}
}
