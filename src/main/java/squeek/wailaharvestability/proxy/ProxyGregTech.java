package squeek.wailaharvestability.proxy;

import net.minecraft.block.Block;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;

public class ProxyGregTech {

    public static final String modID = "gregtech";
    public static final String oreBlockID = "gt.blockores";
    public static final String oreBlockUniqueIdentifier = modID + ":" + oreBlockID;
    public static final String casingID = "gt.blockcasings";
    public static final String casingUniqueIdentifier = modID + ":" + casingID;
    public static final String machineID = "gt.blockmachines";
    public static final String machineUniqueIdentifier = modID + ":" + machineID;
    public static boolean isModLoaded = Loader.isModLoaded(modID);

    public static boolean isOreBlock(Block block) {
        return isModLoaded && GameRegistry.findUniqueIdentifierFor(block).toString().equals(oreBlockUniqueIdentifier);
    }

    public static boolean isCasing(Block block) {
        return isModLoaded && GameRegistry.findUniqueIdentifierFor(block).toString().equals(casingUniqueIdentifier);
    }

    public static boolean isMachine(Block block) {
        return isModLoaded && GameRegistry.findUniqueIdentifierFor(block).toString().equals(machineUniqueIdentifier);
    }
}
