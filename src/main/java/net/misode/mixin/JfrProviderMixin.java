package net.misode.mixin;

import java.util.List;
import jdk.jfr.Event;
import jdk.jfr.FlightRecorder;
import net.minecraft.util.profiling.jfr.JfrProfiler;
import net.minecraft.util.profiling.jfr.event.ChunkGenerationEvent;
import net.minecraft.util.profiling.jfr.event.NetworkSummaryEvent;
import net.minecraft.util.profiling.jfr.event.PacketReceivedEvent;
import net.minecraft.util.profiling.jfr.event.PacketSentEvent;
import net.minecraft.util.profiling.jfr.event.ServerTickTimeEvent;
import net.minecraft.util.profiling.jfr.event.WorldLoadFinishedEvent;
import net.misode.event.FeatureGenerationEvent;
import net.misode.event.SurfaceBuildEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(JfrProfiler.class)
public abstract class JfrProviderMixin {

	@Final
	@Shadow
	@Mutable
	private static List<Class<? extends Event>> EVENTS;

	@Inject(
		method = "<init>",
		at = @At("TAIL")
	)
	private void init(CallbackInfo ci) {
		List<Class<? extends Event>> events = List.of(
			ChunkGenerationEvent.class,
			PacketReceivedEvent.class,
			PacketSentEvent.class,
			NetworkSummaryEvent.class,
			ServerTickTimeEvent.class,
			WorldLoadFinishedEvent.class,
			SurfaceBuildEvent.class,
			FeatureGenerationEvent.class
		);
		events.forEach(FlightRecorder::register);
		EVENTS = events;
	}
}
