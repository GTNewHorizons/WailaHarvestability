package squeek.wailaharvestability.proxy;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

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
    public static boolean isModLoaded = Loader.isModLoaded(modID);

    /**
     * Use a nested class so that ProxyGregTech can load without loading GTMethods. This allows us to keep GTUTIL_IS_ORE
     * static final without hacky code (MethodHandles have zero runtime cost if they're stored in a static final field).
     */
    private static class GTMethods {

        public static final MethodHandle GTUTIL_IS_ORE;

        static {
            try {
                GTUTIL_IS_ORE = MethodHandles.lookup().findStatic(
                        Class.forName("gregtech.api.util.GTUtility"),
                        "isOre",
                        MethodType.methodType(boolean.class, Block.class, int.class));
            } catch (NoSuchMethodException | IllegalAccessException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static boolean isOreBlock(Block block, int meta) {
        if (!isModLoaded) return false;

        try {
            // loads GTMethods
            return (boolean) GTMethods.GTUTIL_IS_ORE.invokeExact(block, meta);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isCasing(Block block) {
        return isModLoaded && GameRegistry.findUniqueIdentifierFor(block).toString().equals(casingUniqueIdentifier);
    }

    public static boolean isMachine(Block block) {
        return isModLoaded && GameRegistry.findUniqueIdentifierFor(block).toString().equals(machineUniqueIdentifier);
    }

    public static boolean isGTTool(ItemStack itemStack) {
        return isModLoaded && itemStack.hasTagCompound() && itemStack.getTagCompound().hasKey("GT.ToolStats");
    }
}
