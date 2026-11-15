package squeek.wailaharvestability.proxy;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;

public class ProxyGregTech {

    public static final String modID = "gregtech";
    public static final String casingID = "gt.blockcasings";
    public static final String casingUniqueIdentifier = modID + ":" + casingID;
    public static final String machineID = "gt.blockmachines";
    public static final String machineUniqueIdentifier = modID + ":" + machineID;
    public static final String oreID = "gt.blockores";
    public static final String oreUniqueIdentifier = modID + ":" + oreID;
    public static boolean isModLoaded = Loader.isModLoaded(modID) && !Loader.isModLoaded("gregapi");
    public static final Map<Integer, List<String>> toolMap = new HashMap<>();

    static {
        toolMap.put(16, Arrays.asList("wrench")); // Wrench
        toolMap.put(17, Arrays.asList("wrench")); // Wrench Broken
        toolMap.put(120, Arrays.asList("wrench")); // Wrench LV
        toolMap.put(121, Arrays.asList("wrench")); // Wrench LV Broken
        toolMap.put(122, Arrays.asList("wrench")); // Wrench MV
        toolMap.put(123, Arrays.asList("wrench")); // Wrench MV Broken
        toolMap.put(124, Arrays.asList("wrench")); // Wrench HV
        toolMap.put(125, Arrays.asList("wrench")); // Wrench HV Broken
        toolMap.put(100, Arrays.asList("pickaxe", "shovel")); // Drill LV
        toolMap.put(101, Arrays.asList("pickaxe", "shovel")); // Drill LV Broken
        toolMap.put(102, Arrays.asList("pickaxe", "shovel")); // Drill MV
        toolMap.put(103, Arrays.asList("pickaxe", "shovel")); // Drill MV Broken
        toolMap.put(104, Arrays.asList("pickaxe", "shovel")); // Drill HV
        toolMap.put(105, Arrays.asList("pickaxe", "shovel")); // Drill HV Broken
        toolMap.put(10, Arrays.asList("axe")); // Saw
        toolMap.put(11, Arrays.asList("axe")); // SawBroken
        toolMap.put(188, Arrays.asList("axe")); // Pocket Saw
        toolMap.put(189, Arrays.asList("axe")); // Pocket Saw Broken
        toolMap.put(110, Arrays.asList("axe", "shears")); // Chainsaw LV
        toolMap.put(111, Arrays.asList("axe", "shears")); // Chainsaw LV Broken
        toolMap.put(112, Arrays.asList("axe", "shears")); // Chainsaw MV
        toolMap.put(113, Arrays.asList("axe", "shears")); // Chainsaw MV Broken
        toolMap.put(114, Arrays.asList("axe", "shears")); // Chainsaw HV
        toolMap.put(115, Arrays.asList("axe", "shears")); // Chainsaw HV Broken
        toolMap.put(130, Arrays.asList("pickaxe")); // Jackhammer LV
        toolMap.put(131, Arrays.asList("pickaxe")); // Jackhammer LV Broken
        toolMap.put(132, Arrays.asList("pickaxe")); // Jackhammer MV
        toolMap.put(133, Arrays.asList("pickaxe")); // Jackhammer MV Broken
        toolMap.put(134, Arrays.asList("pickaxe")); // Jackhammer HV
        toolMap.put(135, Arrays.asList("pickaxe")); // Jackhammer HV Broken
        toolMap.put(12, Arrays.asList("pickaxe")); // Hammer
        toolMap.put(13, Arrays.asList("pickaxe")); // HammerBroken
        toolMap.put(26, Arrays.asList("cutter")); // Wirecutters
        toolMap.put(27, Arrays.asList("cutter")); // Wirecutters Broken
        toolMap.put(192, Arrays.asList("cutter")); // Pocket Wirecutters
        toolMap.put(193, Arrays.asList("cutter")); // Pocket Wirecutters Broken
        toolMap.put(196, Arrays.asList("cutter")); // Wirecutters LV
        toolMap.put(197, Arrays.asList("cutter")); // Wirecutters LV Broken
        toolMap.put(198, Arrays.asList("cutter")); // Wirecutters MV
        toolMap.put(199, Arrays.asList("cutter")); // Wirecutters MV Broken
        toolMap.put(200, Arrays.asList("cutter")); // Wirecutters HV
        toolMap.put(201, Arrays.asList("cutter")); // Wirecutters HV Broken
        toolMap.put(28, Arrays.asList("scoop")); // Scoop
        toolMap.put(29, Arrays.asList("scoop")); // Scoop Broken

    }

    public static GTBlockType getGTBlockType(Block block) {
        if (!isModLoaded) return GTBlockType.None;
        String blockId = GameRegistry.findUniqueIdentifierFor(block).toString();
        if (blockId.startsWith(oreUniqueIdentifier)) return GTBlockType.Ore;
        if (blockId.startsWith(casingUniqueIdentifier)) return GTBlockType.Casing;
        if (blockId.equals(machineUniqueIdentifier)) return GTBlockType.Machine;
        return GTBlockType.None;
    }

    public static boolean isGTTool(ItemStack itemStack) {
        if (itemStack != null) {
            return isModLoaded && itemStack.hasTagCompound() && itemStack.getTagCompound().hasKey("GT.ToolStats");
        }
        return false;
    }

    public enum GTBlockType {
        None,
        Ore,
        Casing,
        Machine
    }

    public static List<String> getToolClassList(ItemStack itemStack) {

        Integer toolID = Items.feather.getDamage(itemStack);
        List<String> toolClassList = toolMap.get(toolID);
        if (toolClassList == null) {
            return Arrays.asList("");
        }
        return toolClassList;
    }

    public static boolean canHarvestBlock(ItemStack tool, Block block, int meta) {
        return tool.getItem().getDigSpeed(tool, block, meta) > 0.0F;
    }

    public static boolean isToolEffective(ItemStack tool, String effectiveTool) {
        List<String> aToolClassList = getToolClassList(tool);

        return aToolClassList.contains(effectiveTool);

    }
}
