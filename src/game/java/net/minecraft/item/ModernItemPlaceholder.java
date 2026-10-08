package net.minecraft.item;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

public final class ModernItemPlaceholder extends Item {

	@Override
	public void getSubItems(CreativeTabs itemIn, NonNullList<ItemStack> tab) {
		Minecraft minecraft = Minecraft.getMinecraft();
		if (minecraft != null && minecraft.isSingleplayer()) {
			super.getSubItems(itemIn, tab);
		}
	}

	@Override
	public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag advanced) {
		tooltip.add(I18n.translateToLocal("item.modern26_2.placeholder.appearance_warning"));
	}
}
