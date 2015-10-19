package squeek.wailaharvestability;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import mcp.mobius.waila.api.impl.ConfigHandler;
import net.minecraft.block.Block;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.IShearable;
import squeek.wailaharvestability.helpers.BlockHelper;
import squeek.wailaharvestability.helpers.ColorHelper;
import squeek.wailaharvestability.helpers.OreHelper;
import squeek.wailaharvestability.helpers.StringHelper;
import squeek.wailaharvestability.helpers.ToolHelper;
import squeek.wailaharvestability.proxy.ProxyCreativeBlocks;

public class WailaHandler implements IWailaDataProvider
{
	@Override
	public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config)
	{
		return null;
	}

	@Override
	public List<String> getWailaHead(ItemStack itemStack, List<String> toolTip, IWailaDataAccessor accessor, IWailaConfigHandler config)
	{
		return toolTip;
	}

	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> toolTip, IWailaDataAccessor accessor, IWailaConfigHandler config)
	{
		Block block = accessor.getBlock();
		int meta = accessor.getMetadata();

		if (ProxyCreativeBlocks.isCreativeBlock(block, meta))
			return toolTip;

		EntityPlayer player = accessor.getPlayer();

		// for disguised blocks
		if (itemStack.getItem() instanceof ItemBlock)
		{
			block = Block.getBlockFromItem(itemStack.getItem());
			meta = itemStack.getItemDamage();
		}

		boolean minimalLayout = config.getConfig("harvestability.minimal", false);
		
		List<String> stringParts = new ArrayList<String>();
		getHarvestability(stringParts, player, block, meta, accessor.getPosition(), config, minimalLayout);
		
		if (!stringParts.isEmpty())
		{
			if (minimalLayout)
				toolTip.add(StringHelper.concatenateStringList(stringParts, EnumChatFormatting.RESET + Config.MINIMAL_SEPARATOR_STRING));
			else
				toolTip.addAll(stringParts);
		}
		
		return toolTip;
	}
	
	public void getHarvestability(List<String> stringList, EntityPlayer player, Block block, int meta, MovingObjectPosition position, IWailaConfigHandler config, boolean minimalLayout)
	{
		boolean isSneaking = player.isSneaking();
		boolean showHarvestLevel = config.getConfig("harvestability.harvestlevel") && (!config.getConfig("harvestability.harvestlevel.sneakingonly") || isSneaking);
		boolean showEffectiveTool = config.getConfig("harvestability.effectivetool") && (!config.getConfig("harvestability.effectivetool.sneakingonly") || isSneaking);
		boolean showCurrentlyHarvestable = config.getConfig("harvestability.currentlyharvestable") && (!config.getConfig("harvestability.currentlyharvestable.sneakingonly") || isSneaking);
		boolean hideWhileHarvestable = config.getConfig("harvestability.unharvestableonly", false);
		boolean showOresOnly = config.getConfig("harvestability.oresonly", false);
		boolean toolRequiredOnly = config.getConfig("harvestability.toolrequiredonly");

		if (showHarvestLevel || showEffectiveTool || showCurrentlyHarvestable)
		{
			if (showOresOnly && !OreHelper.isBlockAnOre(block, meta))
			{
				return;
			}

			if (BlockHelper.isBlockUnbreakable(block, player.worldObj, position.blockX, position.blockY, position.blockZ))
			{
				String unbreakableString = ColorHelper.getBooleanColor(false) + Config.NOT_CURRENTLY_HARVESTABLE_STRING + (!minimalLayout ? EnumChatFormatting.RESET + StatCollector.translateToLocal("wailaharvestability.harvestable") : "");
				stringList.add(unbreakableString);
				return;
			}

			// needed to stop array index out of bounds exceptions on mob spawners
			// block.getHarvestLevel/getHarvestTool are only 16 elements big
			if (meta >= 16)
				meta = 0;

			int harvestLevel = block.getHarvestLevel(meta);
			String effectiveTool = BlockHelper.getEffectiveToolOf(player.worldObj, position.blockX, position.blockY, position.blockZ, block, meta);
			if (effectiveTool != null && harvestLevel < 0)
				harvestLevel = 0;
			boolean blockHasEffectiveTools = harvestLevel >= 0 && effectiveTool != null;

			String shearability = getShearabilityString(player, block, meta, position, config);
			String silkTouchability = getSilkTouchabilityString(player, block, meta, position, config);

			if (toolRequiredOnly && block.getMaterial().isToolNotRequired() && !blockHasEffectiveTools && shearability.isEmpty() && silkTouchability.isEmpty())
				return;

			boolean canHarvest = false;
			boolean isEffective = false;
			boolean isAboveMinHarvestLevel = false;
			boolean isHoldingTinkersTool = false;

			ItemStack itemHeld = player.getHeldItem();
			if (itemHeld != null)
			{
				isHoldingTinkersTool = ToolHelper.hasToolTag(itemHeld);
				canHarvest = ToolHelper.canToolHarvestBlock(itemHeld, block, meta) || (!isHoldingTinkersTool && block.canHarvestBlock(player, meta));
				isAboveMinHarvestLevel = (showCurrentlyHarvestable || showHarvestLevel) && ToolHelper.canToolHarvestLevel(itemHeld, block, meta, harvestLevel);
				isEffective = showEffectiveTool && ToolHelper.isToolEffectiveAgainst(itemHeld, block, meta, effectiveTool);
			}

			boolean isCurrentlyHarvestable = (canHarvest && isAboveMinHarvestLevel) || (!isHoldingTinkersTool && ForgeHooks.canHarvestBlock(block, player, meta));

			if (hideWhileHarvestable && isCurrentlyHarvestable)
				return;

			String currentlyHarvestable = showCurrentlyHarvestable ? ColorHelper.getBooleanColor(isCurrentlyHarvestable) + (isCurrentlyHarvestable ? Config.CURRENTLY_HARVESTABLE_STRING : Config.NOT_CURRENTLY_HARVESTABLE_STRING) + (!minimalLayout ? EnumChatFormatting.RESET + StatCollector.translateToLocal("wailaharvestability.currentlyharvestable") : "") : "";

			if (!currentlyHarvestable.isEmpty() || !shearability.isEmpty() || !silkTouchability.isEmpty())
			{
				String separator = (!shearability.isEmpty() || !silkTouchability.isEmpty() ? " " : "");
				stringList.add(currentlyHarvestable + separator + silkTouchability + (!silkTouchability.isEmpty() ? separator : "") + shearability);
			}
			if (harvestLevel != -1 && showEffectiveTool && effectiveTool != null)
			{
				String effectiveToolString;
				if (StatCollector.canTranslate("wailaharvestability.toolclass." + effectiveTool))
					effectiveToolString = StatCollector.translateToLocal("wailaharvestability.toolclass." + effectiveTool);
				else
					effectiveToolString = effectiveTool.substring(0, 1).toUpperCase() + effectiveTool.substring(1);
				stringList.add((!minimalLayout ? StatCollector.translateToLocal("wailaharvestability.effectivetool") : "") + ColorHelper.getBooleanColor(isEffective && (!isHoldingTinkersTool || canHarvest), isHoldingTinkersTool && isEffective && !canHarvest) + effectiveToolString);
			}
			if (harvestLevel >= 1 && showHarvestLevel)
				stringList.add((!minimalLayout ? StatCollector.translateToLocal("wailaharvestability.harvestlevel") : "") + ColorHelper.getBooleanColor(isAboveMinHarvestLevel && canHarvest) + StringHelper.stripFormatting(StringHelper.getHarvestLevelName(harvestLevel)));
		}
	}
	
	public String getShearabilityString(EntityPlayer player, Block block, int meta, MovingObjectPosition position, IWailaConfigHandler config)
	{
		boolean isSneaking = player.isSneaking();
		boolean showShearability = config.getConfig("harvestability.shearability") && (!config.getConfig("harvestability.shearability.sneakingonly") || isSneaking);
		
		if (showShearability && (block instanceof IShearable || block == Blocks.deadbush || (block == Blocks.double_plant && block.getItemDropped(meta, new Random(), 0) == null)))
		{
			ItemStack itemHeld = player.getHeldItem();
			boolean isHoldingShears = itemHeld != null && itemHeld.getItem() instanceof ItemShears;
			boolean isShearable = isHoldingShears && ((IShearable) block).isShearable(itemHeld, player.worldObj, position.blockX, position.blockY, position.blockZ);
			return ColorHelper.getBooleanColor(isShearable, !isShearable && isHoldingShears) + Config.SHEARABILITY_STRING;
		}
		return "";
	}

	public String getSilkTouchabilityString(EntityPlayer player, Block block, int meta, MovingObjectPosition position, IWailaConfigHandler config)
	{
		boolean isSneaking = player.isSneaking();
		boolean showSilkTouchability = config.getConfig("harvestability.silktouchability") && (!config.getConfig("harvestability.silktouchability.sneakingonly") || isSneaking);
		
		if (showSilkTouchability && block.canSilkHarvest(player.worldObj, player, position.blockX, position.blockY, position.blockZ, meta))
		{
			Item itemDropped = block.getItemDropped(meta, new Random(), 0);
			boolean silkTouchMatters = (itemDropped instanceof ItemBlock && itemDropped != Item.getItemFromBlock(block)) || block.quantityDropped(new Random()) <= 0;
			if (silkTouchMatters)
			{
				boolean hasSilkTouch = EnchantmentHelper.getSilkTouchModifier(player);
				return ColorHelper.getBooleanColor(hasSilkTouch) + Config.SILK_TOUCHABILITY_STRING;
			}
		}
		return "";
	}

	@Override
	public List<String> getWailaTail(ItemStack itemStack, List<String> toolTip, IWailaDataAccessor accessor, IWailaConfigHandler config)
	{
		return toolTip;
	}

	public static HashMap<String, Boolean> configOptions = new HashMap<String, Boolean>();
	static
	{
		configOptions.put("harvestability.harvestlevel", true);
		configOptions.put("harvestability.effectivetool", true);
		configOptions.put("harvestability.currentlyharvestable", true);
		configOptions.put("harvestability.harvestlevel.sneakingonly", false);
		configOptions.put("harvestability.effectivetool.sneakingonly", false);
		configOptions.put("harvestability.currentlyharvestable.sneakingonly", false);
		configOptions.put("harvestability.oresonly", false);
		configOptions.put("harvestability.minimal", false);
		configOptions.put("harvestability.unharvestableonly", false);
		configOptions.put("harvestability.toolrequiredonly", true);
		configOptions.put("harvestability.shearability", true);
		configOptions.put("harvestability.shearability.sneakingonly", false);
		configOptions.put("harvestability.silktouchability", true);
		configOptions.put("harvestability.silktouchability.sneakingonly", false);
	}

	public static void callbackRegister(IWailaRegistrar registrar)
	{
		WailaHandler instance = new WailaHandler();

		for (Map.Entry<String, Boolean> entry : configOptions.entrySet())
		{
			// hacky way to set default values to anything but true
			ConfigHandler.instance().getConfig(entry.getKey(), entry.getValue());
			registrar.addConfig("Harvestability", entry.getKey(), "option." + entry.getKey());
		}

		registrar.registerBodyProvider(instance, Block.class);
	}
}