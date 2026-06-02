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

    public static boolean isRelevantGTBlock(Block block) {
        if (!isModLoaded) return false;
        String blockId = GameRegistry.findUniqueIdentifierFor(block).toString();
        return blockId.startsWith(oreUniqueIdentifier) || blockId.startsWith(casingUniqueIdentifier)
                || blockId.equals(machineUniqueIdentifier);
    }

    public static boolean isGTTool(ItemStack itemStack) {
        return isModLoaded && itemStack.hasTagCompound() && itemStack.getTagCompound().hasKey("GT.ToolStats");
    }
}
