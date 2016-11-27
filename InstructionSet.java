//should distinguish assembler directive
//BYTE X'' need different operation
//RESB.RESW.START.END need different operation
//seem like LOC.objectcode should convert to String

import java.util.StringTokenizer;
public class InstructionSet 
{
	private static final int OPSHIFT = 65536;
	private static final int XSHIFT = 32768;
	private static final int ASCIISHIFT = 256;
	
	private String ALL = "";
	private int LOC = 0;
	private String LABEL = "";
	private String OP = "";
	private short x = 0;
	private int FORMAT = 0;//is it a essential attribute in InstructionSet?
	private String OPERAND = "";
	private int objectcode = 0;
	private boolean isComment = false;
	private InstructionSet nextInstruction = null;
	
	public InstructionSet()
	{
	}
	public InstructionSet(String tmp)
	{
		setALL(tmp);
	}
	public void copy(InstructionSet other)
	{
		setALL(other.toString());
		setLABEL(other.getLABEL());
		setOP(other.getOP());
		setOPERAND(other.getOPERAND());
		setobjectcode(other.getobjectcode());
		setNext(other.getNext());
	}
	public void setLOC(int loc)
	{
		LOC = loc;
	}
	public void setLABEL(String label)
	{
		LABEL = label;
	}
	public void setOP(String op)
	{
		OP = op;
	}
	public void setFORMAT(int format)
	{
		FORMAT = format;
	}
	public void setOPERAND(String operand)
	{
		OPERAND = operand;
	}
	public void setobjectcode(int code)
	{
		objectcode = code;
	}
	public int getLOC()
	{
		return LOC;
	}
	public String getLABEL()
	{
		return LABEL;
	}
	public String getOP()
	{
		return OP;
	}
	public int getFORMAT()
	{
		return FORMAT;
	}
	public String getOPERAND()
	{
		return OPERAND;
	}
	public int getobjectcode()
	{
		return objectcode;
	}
	public boolean getisComment()
	{
		return isComment;
	}
	public void setNext(InstructionSet other)
	{
		this.nextInstruction = other;
	}
	public InstructionSet getNext()
	{
		return nextInstruction;
	}
	public void setALL(String all)
	{
		ALL = all;
	}
	public String toString()
	{
		return ALL;
	}
	public void line_partition()
	{
		StringTokenizer st = new StringTokenizer(this.toString());
		if (this.toString().indexOf(".")!=-1)
		{
			this.isComment = true;
		}
		else if(st.countTokens() == 3)
		{
			setLABEL(st.nextToken());
			setOP(st.nextToken());
			setOPERAND(st.nextToken());
			if(getOPERAND().indexOf(",X")!=-1)
			{
				x = 1;
				setOPERAND(getOPERAND().substring(0,getOPERAND().length()-2));
			}
		}
		else if(st.countTokens() == 2)
		{
			setOP(st.nextToken());
			setOPERAND(st.nextToken());
			if(getOPERAND().indexOf(",X")!=-1)
			{
				x = 1;
				setOPERAND(getOPERAND().substring(0,getOPERAND().length()-2));
			}
		}
		else if(st.countTokens() == 1)
		{
			setOP(st.nextToken());
		}
	}
	public void setANDmoveLOC(int nowloc)//seems like i should move this function to other class
	{
		//need to be modified
		setLOC(nowloc);//setLOC
		//AssemblerDemo.setLOCCTR(nowloc + 3);//default increase
		setFORMAT(OPTAB.getFORMAT(getOP()));
		if(getOP().equals("BYTE"))
		{
    		if(getOPERAND().charAt(0) == 'C')
    			AssemblerDemo.setLOCCTR(nowloc + getOPERAND().length() - 3);
    		else if(getOPERAND().charAt(0)=='X')
    			AssemblerDemo.setLOCCTR(nowloc + (getOPERAND().length() - 3)/2);
		}
    	else if(getOP().equals("WORD"))
    		AssemblerDemo.setLOCCTR(nowloc + 3);//may need to be modified
    	else if(getOP().equals("RESB"))
    		AssemblerDemo.setLOCCTR(nowloc + 1 * Integer.parseInt(getOPERAND()));
    	else if(getOP().equals("RESW"))
    		AssemblerDemo.setLOCCTR(nowloc + 3 * Integer.parseInt(getOPERAND()));
    	else
    	{
    		switch(getFORMAT())
    		{
    			case 0:break;//assembler directive, need to be modified
    			case 1:AssemblerDemo.setLOCCTR(nowloc+1);break;
    			case 2:AssemblerDemo.setLOCCTR(nowloc+2);break;
    			case 3:AssemblerDemo.setLOCCTR(nowloc+3);break;
    			case 4:AssemblerDemo.setLOCCTR(nowloc+4);break;
    		}
    	}
	}
	public void generateobjectcode()//seems like i should move this function to other class
	{
		if(getOP().equals("RESB")||getOP().equals("RESW"));
		else if(getOP().equals("BYTE"))
		{
			if(getOPERAND().charAt(0)=='X')
				objectcode = Integer.parseInt(getOPERAND().substring(2,getOPERAND().length()-1),16);
			else if(getOPERAND().charAt(0)=='C')
			{
				int ASCII = 0;
				String tmp = getOPERAND().substring(2,getOPERAND().length()-1);
		    	for (char c : tmp.toCharArray())
		    		ASCII = ASCII*ASCIISHIFT+(int)c;
		    	objectcode += ASCII;
			}
		}
		else if(getOP().equals("WORD"))
		{
			objectcode = objectcode + Integer.parseInt(getOPERAND());
		}
		else
		{
			objectcode += x*XSHIFT;
			objectcode += OPTAB.getCODE(getOP())*OPSHIFT;
			objectcode += SYMTAB.getADR(getOPERAND());
		}
	}
	public void print()
	{
		if(isComment)
			System.out.println(this.toString());
		else if(x == 1)
			System.out.printf("%-5x%-7s%-7s%-9s%06x\n",getLOC(),getLABEL(),getOP(),getOPERAND()+",X",getobjectcode());
		else
			System.out.printf("%-5x%-7s%-7s%-9s%06x\n",getLOC(),getLABEL(),getOP(),getOPERAND(),getobjectcode());
	}
}
