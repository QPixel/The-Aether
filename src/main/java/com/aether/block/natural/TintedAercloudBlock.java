package com.aether.block.natural;

import com.aether.block.util.IAetherBlockColor;
import com.aether.item.block.TintedBlockItem;

import net.minecraft.block.AbstractBlock;

public class TintedAercloudBlock extends AercloudBlock implements IAetherBlockColor
{
	public static final int
		COLOR_DEFAULT = 0xFFFFFF,
		COLOR_BLUE_OLD = 0xCCFFFF,
		COLOR_BLUE_NEW = 0x71D2FF,
		COLOR_GOLDEN_OLD = 0xFFFF80,
		COLOR_GOLDEN_NEW = 0xFFE082;

	private final int hexColor, updatedHexColor;
	
	public TintedAercloudBlock(int hexColor, int updatedHexColor, AbstractBlock.Properties properties) {
		super(properties);
		this.hexColor = hexColor;
		this.updatedHexColor = updatedHexColor;
	}
	
	@Override
	public final int getColor(boolean updatedVersion) {
		return updatedVersion? updatedHexColor : hexColor;
	}
	
	@Override
	public TintedBlockItem asItem() {
		return (TintedBlockItem)super.asItem();
	}
}