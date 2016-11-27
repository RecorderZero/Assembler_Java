import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class AssemblerDemo 
{
	private static int LOCCTR = 0;
	
	public static void main(String[] args) throws IOException //can i put openfile function in other class?
	{
		OPTAB.generlizeCODE();//initialize opcode
	    OPTAB.generlizeFORMAT();//initialize opformat
	  	//System.out.printf("ADD  %x",OPTAB.getCODE("ADD"));//test OPtoCODE
	    //System.out.printf("ADD  %d",OPTAB.getFORMAT("ADD"));//test OPtoFORMAT
	    BufferedReader File = new BufferedReader(new FileReader(args[0]));
	    InstructionSet start_InstructionSet = new InstructionSet(File.readLine());
	    InstructionSet end_InstructionSet = null;
	    //start_InstructionSet.output();//test whether start_Instruction can be initialized
	    try 
	    {
	    	InstructionSet tmp = start_InstructionSet;
	        InstructionSet new_InstructionSet = new InstructionSet(File.readLine());
	        while (new_InstructionSet.toString() != null) 
	        {
	        	tmp.setNext(new_InstructionSet);
	        	tmp = new_InstructionSet;
	            new_InstructionSet = new InstructionSet(File.readLine());
	            //System.out.println(tmp.toString());//print out the file by one string per instruction.
	        }
	        end_InstructionSet = tmp;
	    } 
	    catch (IOException ex) 
	    {
	        ex.printStackTrace();
	    }
	    finally 
	    {
	    	File.close();
	    }
	    InstructionSet first_InstructionSet = start_InstructionSet.getNext();
	    //for(InstructionSet tmp = start_InstructionSet;tmp!=null;tmp = tmp.getNext())
		//	System.out.println(tmp.toString()); //print out the file by one string per instruction.
	    start_InstructionSet.line_partition();//initialize start for using LOCCTR
	    //start_InstructionSet.print();//test start
	    end_InstructionSet.line_partition();//initialize end
	    //end_InstructionSet.print();//test end
	    LOCCTR = Integer.parseInt(start_InstructionSet.getOPERAND(),16);
	    start_InstructionSet.setLOC(LOCCTR);
	    //start_InstructionSet.print();//test start
		for(InstructionSet tmp = first_InstructionSet;tmp!=end_InstructionSet;tmp = tmp.getNext())//line partition and pass1
		{
			tmp.line_partition();
			if(tmp.getisComment())continue;
			tmp.setANDmoveLOC(LOCCTR);
			if(!(tmp.getLABEL().equals("")))//build SYMTAB
				SYMTAB.putADR(tmp.getLABEL(),tmp.getLOC());
		}
		for(InstructionSet tmp = first_InstructionSet;tmp!=end_InstructionSet;tmp = tmp.getNext())//pass2
		{
			if(tmp.getisComment())continue;
			tmp.generateobjectcode();
		}
		//for(InstructionSet tmp = start_InstructionSet;tmp!=null;tmp = tmp.getNext())
		//	System.out.println(tmp.getOPERAND()); //print out the segment.
		for(InstructionSet tmp = start_InstructionSet;tmp!=null;tmp = tmp.getNext())
			tmp.print(); //print out the instruction in correct format.
		//SYMTAB.print();//test whether making SYMTAB is successful or not
	}
	public static int getLOCCTR()
	{
		return LOCCTR;
	}
	public static void setLOCCTR(int nextloc)
	{
		LOCCTR = nextloc;
	}
	
}

