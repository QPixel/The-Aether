package com.aetherteam.aether.client.event.listeners;

import com.aetherteam.aether.client.event.hooks.DimensionClientHooks;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.tuple.Triple;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class DimensionClientListener {
    @SubscribeEvent
    public static void onRenderFog(ViewportEvent.RenderFog event) {
        Camera camera = event.getCamera();
        FogRenderer.FogMode fogMode = event.getMode();
        Float renderNearFog = DimensionClientHooks.renderNearFog(camera, fogMode, event.getFarPlaneDistance());
        if (!event.isCanceled() && renderNearFog != null) {
            event.setNearPlaneDistance(renderNearFog);
            event.setCanceled(true);
        }
        Float reduceLavaFog = DimensionClientHooks.reduceLavaFog(camera, event.getNearPlaneDistance());
        if (!event.isCanceled() && reduceLavaFog != null) {
            event.setNearPlaneDistance(reduceLavaFog);
            event.setFarPlaneDistance(reduceLavaFog * 4);
            event.setCanceled(true);
        }
    }

    /**
     * The purpose of this event handler is to prevent the fog from turning black near the void in the Aether.
     * This works with any dimension using the Aether's dimension effects.
     */
    @SubscribeEvent
    public static void onRenderFogColor(ViewportEvent.ComputeFogColor event) {
        Camera camera = event.getCamera();
        Triple<Float, Float, Float> renderFogColors = DimensionClientHooks.renderFogColors(camera, event.getRed(), event.getGreen(), event.getBlue());
        if (renderFogColors.getLeft() != null && renderFogColors.getMiddle() != null && renderFogColors.getRight() != null) {
            event.setRed(renderFogColors.getLeft());
            event.setGreen(renderFogColors.getMiddle());
            event.setBlue(renderFogColors.getRight());
        }
        Triple<Float, Float, Float> adjustWeatherFogColors = DimensionClientHooks.adjustWeatherFogColors(camera, event.getRed(), event.getGreen(), event.getBlue());
        if (adjustWeatherFogColors.getLeft() != null && adjustWeatherFogColors.getMiddle() != null && adjustWeatherFogColors.getRight() != null) {
            event.setRed(adjustWeatherFogColors.getLeft());
            event.setGreen(adjustWeatherFogColors.getMiddle());
            event.setBlue(adjustWeatherFogColors.getRight());
        }
    }

    /**
     * Ticks time in clientside Aether levels.
     */
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            DimensionClientHooks.tickTime();
        }
    }
}