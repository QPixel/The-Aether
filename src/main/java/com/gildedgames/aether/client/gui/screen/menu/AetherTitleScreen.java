package com.gildedgames.aether.client.gui.screen.menu;

import com.gildedgames.aether.Aether;
import com.gildedgames.aether.client.gui.button.AetherMenuButton;
import com.gildedgames.aether.client.gui.button.DynamicMenuButton;
import com.gildedgames.aether.client.registry.AetherSoundEvents;
import com.gildedgames.aether.core.AetherConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import com.mojang.math.Vector3f;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.sounds.Music;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.PanoramaRenderer;
import net.minecraft.client.renderer.CubeMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.Util;
import net.minecraft.util.Mth;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.internal.BrandingControl;

import javax.annotation.Nonnull;

public class AetherTitleScreen extends TitleScreen {
	public static final Music MENU = new Music(AetherSoundEvents.MUSIC_MENU.get(), 20, 600, true);

	private final PanoramaRenderer panorama = new PanoramaRenderer(new CubeMap(new ResourceLocation(Aether.MODID, "textures/gui/title/panorama/panorama")));
	private static final ResourceLocation PANORAMA_OVERLAY = new ResourceLocation("textures/gui/title/background/panorama_overlay.png");
	private static final ResourceLocation AETHER_LOGO = new ResourceLocation(Aether.MODID, "textures/gui/title/aether.png");


	private AetherNotificationModUpdateScreen modUpdateNotification;
	public boolean fading;
	public long fadeInStart;

	private boolean alignedLeft;

	public AetherTitleScreen() {
		this.fading = true;
	}

	@Override
	protected void init() {
		super.init();
		this.alignedLeft = this.alignElementsLeft();
		this.setupButtons();
		this.modUpdateNotification = new AetherNotificationModUpdateScreen();
		this.modUpdateNotification.init();
	}

	public void setupButtons() {
		int buttonCount = 0;
		for (Widget widget : this.renderables) {
			if (widget instanceof AetherMenuButton aetherMenuButton) {
				if (this.alignElementsLeft()) {
					aetherMenuButton.x = 30;
					aetherMenuButton.y = 80 + buttonCount * 25;
					aetherMenuButton.setWidth(200);
				} else {
					aetherMenuButton.x = aetherMenuButton.initialX;
					aetherMenuButton.y = aetherMenuButton.initialY - 10;
					aetherMenuButton.setWidth(aetherMenuButton.initialWidth);
				}
				buttonCount++;
			}
		}
	}

	@Override
	public void render(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
		if (this.alignedLeft != this.alignElementsLeft()) {
			this.alignedLeft = this.alignElementsLeft();
			this.setupButtons();
		}
		if (this.minecraft != null) {
			if (this.fadeInStart == 0L && this.fading) {
				this.fadeInStart = Util.getMillis();
			}
			float f = this.fading ? (float) (Util.getMillis() - this.fadeInStart) / 1000.0F : 1.0F;
			this.panorama.render(partialTicks, Mth.clamp(f, 0.0F, 1.0F));
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.setShaderTexture(0, PANORAMA_OVERLAY);
			RenderSystem.enableBlend();
			RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.fading ? (float)Mth.ceil(Mth.clamp(f, 0.0F, 1.0F)) : 1.0F);
			blit(poseStack, 0, 0, this.width, this.height, 0.0F, 0.0F, 16, 128, 16, 128);
			float f1 = this.fading ? Mth.clamp(f - 1.0F, 0.0F, 1.0F) : 1.0F;
			int l = Mth.ceil(f1 * 255.0F) << 24;
			if ((l & -67108864) != 0) {
				RenderSystem.setShader(GameRenderer::getPositionTexShader);
				RenderSystem.setShaderTexture(0, AETHER_LOGO);
				RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, f1);
				int logoX = this.alignElementsLeft() ? 10 : this.width / 2 - 102;
				int logoY = this.alignElementsLeft() ? 15 : 30;
				this.blit(poseStack, logoX, logoY, 0, 0, 155, 44);
				this.blit(poseStack, logoX + 155, logoY, 0, 45, 155, 44);

				net.minecraftforge.client.ForgeHooksClient.renderMainMenu(this, poseStack, this.font, this.width, this.height, l);

				if (this.splash != null) {
					float splashX = this.alignElementsLeft() ? 200.0F : (float) this.width / 2 + 90;
					float splashY = this.alignElementsLeft() ? 50.0F : 70.0F;
					poseStack.pushPose();
					poseStack.translate(splashX, splashY, 0.0F);
					poseStack.mulPose(Vector3f.ZP.rotationDegrees(-20.0F));
					float f2 = 1.8F - Mth.abs(Mth.sin((float) (Util.getMillis() % 1000L) / 1000.0F * ((float) Math.PI * 2F)) * 0.1F);
					f2 = f2 * 100.0F / (float) (this.font.width(this.splash) + 32); poseStack.scale(f2, f2, f2);
					drawCenteredString(poseStack, this.font, this.splash, 0, -8, 16776960 | l);
					poseStack.popPose();
				}

				if (this.alignElementsLeft()) {
					BrandingControl.forEachLine(true, true, (brdline, brd) ->
							drawString(poseStack, this.font, brd, width - this.font.width(brd) - 1, this.height - (10 + (brdline + 1) * (this.font.lineHeight + 1)), 16777215 | l)
					);
					BrandingControl.forEachAboveCopyrightLine((brdline, brd) ->
							drawString(poseStack, this.font, brd, 1, this.height - (brdline + 1) * (this.font.lineHeight + 1), 16777215 | l)
					);
				} else {
					BrandingControl.forEachLine(true, true, (brdline, brd) ->
							drawString(poseStack, this.font, brd, 2, this.height - (10 + brdline * (this.font.lineHeight + 1)), 16777215 | l)
					);
					BrandingControl.forEachAboveCopyrightLine((brdline, brd) ->
							drawString(poseStack, this.font, brd, this.width - this.font.width(brd), this.height - (10 + (brdline + 1) * (this.font.lineHeight + 1)), 16777215 | l)
					);
				}
			}

			for (GuiEventListener guiEventListener : this.children()) {
				if (guiEventListener instanceof AbstractWidget abstractWidget) {
					if (f1 > 0.02F) {
						abstractWidget.setAlpha(f1);
						abstractWidget.visible = true;
					} else {
						abstractWidget.visible = false;
					}
				}
			}

			int offset = 0;
			for (Widget widget : this.renderables) {
				widget.render(poseStack, mouseX, mouseY, partialTicks);
				if (widget instanceof AetherMenuButton aetherButton) {
					if (aetherButton.isMouseOver(mouseX, mouseY)) {
						if (aetherButton.renderOffset < 15) {
							aetherButton.renderOffset += 4;
						}
					} else {
						if (aetherButton.renderOffset > 0) {
							aetherButton.renderOffset -= 4;
						}
					}
				}
				if (widget instanceof DynamicMenuButton dynamicMenuButton) {
					if (dynamicMenuButton.enabled) {
						offset -= 24;
					}
				}
			}
			for (Widget widget : this.renderables) {
				if (widget instanceof Button button) {
					Component buttonText = button.getMessage();
					if (buttonText.equals(new TranslatableComponent("narrator.button.accessibility"))) {
						button.x = this.width - 48 + offset;
						button.y = 4;
					} else if (buttonText.equals(new TranslatableComponent("narrator.button.language"))) {
						button.x = this.width - 24 + offset;
						button.y = 4;
					}
				}
			}

			if (f1 >= 1.0f) {
				this.modUpdateNotification.render(poseStack, mouseX, mouseY, partialTicks);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Nonnull
	@Override
	protected <T extends GuiEventListener & Widget & NarratableEntry> T addRenderableWidget(@Nonnull T widget) {
		if (widget instanceof Button button) {
			if (this.isButtonAether(button.getMessage())) {
				AetherMenuButton aetherButton = new AetherMenuButton(button);
				return (T) super.addRenderableWidget(aetherButton);
			}
		}
		return super.addRenderableWidget(widget);
	}

	public boolean isButtonAether(Component buttonText) {
		return buttonText.equals(new TranslatableComponent("menu.singleplayer"))
				|| buttonText.equals(new TranslatableComponent("menu.multiplayer"))
				|| buttonText.equals(new TranslatableComponent("menu.online"))
				|| buttonText.equals(new TranslatableComponent("fml.menu.mods"))
				|| buttonText.equals(new TranslatableComponent("menu.options"))
				|| buttonText.equals(new TranslatableComponent("menu.quit"));
	}

	public boolean alignElementsLeft() {
		return (AetherConfig.CLIENT.menu_type_toggles_alignment.get() && AetherConfig.CLIENT.enable_world_preview.get()) || AetherConfig.CLIENT.align_aether_menu_elements_left.get();
	}
}