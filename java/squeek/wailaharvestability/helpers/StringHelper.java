package squeek.wailaharvestability.helpers;

import java.lang.reflect.Method;
import java.util.List;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

public class StringHelper
{

	public static Class<?> HarvestLevels = null;
	public static Method getHarvestLevelName = null;
	static
	{
		try
		{
			HarvestLevels = Class.forName("tconstruct.library.util.HarvestLevels");
			getHarvestLevelName = HarvestLevels.getDeclaredMethod("getHarvestLevelName", int.class);
		}
		catch (Exception e)
		{
		}
	}

	public static String getHarvestLevelName(int num)
	{
		if (getHarvestLevelName != null)
		{
			try
			{
				return (String) getHarvestLevelName.invoke(null, num);
			}
			catch (Exception e)
			{
			}
		}

		String unlocalized = "wailaharvestability.harvestlevel" + (num + 1);

		if (StatCollector.canTranslate(unlocalized))
			return StatCollector.translateToLocal(unlocalized);

		return String.valueOf(num);
	}

	public static String concatenateStringList(List<String> strings, String separator)
	{
		StringBuilder sb = new StringBuilder();
		String sep = "";
		for (String s : strings)
		{
			sb.append(sep).append(s);
			sep = separator;
		}
		return sb.toString();
	}

	public static String stripFormatting(String str)
	{
		return EnumChatFormatting.getTextWithoutFormattingCodes(str);
	}
}
