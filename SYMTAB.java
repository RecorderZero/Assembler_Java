import java.util.HashMap;
import java.util.Map;

public class SYMTAB 
{
	private static Map<String,Integer> SYMtoADR = new HashMap<String,Integer>();
	
	public static void putADR(String key,int value)
	{
		SYMtoADR.put(key, value);
	}
	public static int getADR(String key)
	{
		if(SYMtoADR.get(key) == null)
			return 0;
		else
			return SYMtoADR.get(key);
	}
	public static void print()
	{
		for (String name: SYMtoADR.keySet())
		{

            String key =name.toString();
            int value = SYMtoADR.get(name);  
            System.out.printf("%s %x\n",key,value);  


		}
	}
}
