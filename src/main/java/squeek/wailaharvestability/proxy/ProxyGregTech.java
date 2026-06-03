package squeek.wailaharvestability.proxy;

import net.minecraft.block.Block;
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

    public static GTBlockType getGTBlockType(Block block) {
        if (!isModLoaded) return GTBlockType.None;
        String blockId = GameRegistry.findUniqueIdentifierFor(block).toString();
        if (blockId.startsWith(oreUniqueIdentifier)) return GTBlockType.Ore;
        if (blockId.startsWith(casingUniqueIdentifier)) return GTBlockType.Casing;
        if (blockId.equals(machineUniqueIdentifier)) return GTBlockType.Machine;
        return GTBlockType.None;
    }

    public static boolean isGTTool(ItemStack itemStack) {
        return isModLoaded && itemStack.hasTagCompound() && itemStack.getTagCompound().hasKey("GT.ToolStats");
    }

    public enum GTBlockType {
        None,
        Ore,
        Casing,
        Machine
    }
}
