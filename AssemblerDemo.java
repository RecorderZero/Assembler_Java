import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileWriter;

public class AssemblerDemo 
{
	private static int LOCCTR = 0;
	private static int program_length = 0;
	
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
	    //System.out.println(start_InstructionSet.print());//test start
	    end_InstructionSet.line_partition();//initialize end
	    //System.out.println(end_InstructionSet.print());//test end
	    LOCCTR = Integer.parseInt(start_InstructionSet.getOPERAND(),16);
	    start_InstructionSet.setLOC(LOCCTR);
	    //System.out.println(start_InstructionSet.print());//test start
	    //InstructionSet last_InstructionSet = null;
		for(InstructionSet tmp = first_InstructionSet;tmp!=end_InstructionSet;tmp = tmp.getNext())//line partition and pass1
		{
			tmp.line_partition();
			if(tmp.getisComment())continue;
			tmp.setANDmoveLOC(LOCCTR);
			if(!(tmp.getLABEL().equals("")))//build SYMTAB
				SYMTAB.putADR(tmp.getLABEL(),tmp.getLOC());
			program_length = LOCCTR - first_InstructionSet.getLOC();
		}
		//System.out.printf("%x",program_length);//test program_length
		for(InstructionSet tmp = first_InstructionSet;tmp!=end_InstructionSet;tmp = tmp.getNext())//pass2
		{
			if(tmp.getisComment())continue;
			tmp.generateobjectcode();
		}
		//for(InstructionSet tmp = start_InstructionSet;tmp!=null;tmp = tmp.getNext())
		//	System.out.println(tmp.getOPERAND()); //print out the segment.
		//for(InstructionSet tmp = start_InstructionSet;tmp!=null;tmp = tmp.getNext())
		//	System.out.println(tmp.print()); //print out the instruction in correct format.
		//SYMTAB.print();//test whether making SYMTAB is successful or not
		//System.out.println(start_InstructionSet.print());//test start
		//System.out.println(first_InstructionSet.print());//test first
		//System.out.println(last_InstructionSet.print());//test last
		//System.out.println(end_InstructionSet.print());//test end
		FileWriter fw1 = new FileWriter("Figure2.2.txt");
		FileWriter fw2 = new FileWriter("Figure2.3.txt");
		fw1.write(start_InstructionSet.print());
		String start = String.format("%c^%-6s^%06X^%06X\r\n",'H',start_InstructionSet.getLABEL(),start_InstructionSet.getLOC(),program_length);
		fw2.write(start);
		String output = "";
		String buffer = "";
		int countspace = 0;
		for(InstructionSet tmp = first_InstructionSet;tmp!=end_InstructionSet;tmp = tmp.getNext())//text t
		{
			fw1.write(tmp.print());
			if(!tmp.getOP().equals("RESB") && !tmp.getOP().equals("RESW") && !tmp.getisComment())
			{
				if(output.equals(""))
					output = String.format("%c^%06X", 'T',tmp.getLOC());//Initialize
				buffer += "^"+tmp.getobjectcode();
				countspace ++;
			}
			else if((tmp.getOP().equals("RESB") || tmp.getOP().equals("RESW")) && !buffer.equals(""))
			{
				output += String.format("^%02X%s",( buffer.length() - countspace ) / 2 , buffer);
				fw2.write(output + "\r\n");
				buffer = "";
				output = "";
				countspace = 0;
				continue;
			}
			if( buffer.length() + tmp.getNext().getobjectcode().length() - countspace > 60 )
			{
				output += String.format("^%02X%s",( buffer.length() - countspace ) / 2 , buffer);
				fw2.write(output + "\r\n");
				buffer = "";
				output = "";
				countspace = 0;
				continue;
			}	
		}
		fw2.write(output + String.format("^%02X%s",( buffer.length() - countspace ) / 2 , buffer) + "\r\n");//last line
		fw1.write(end_InstructionSet.print());
		String end = String.format("%c^%06X",'E',SYMTAB.getADR(end_InstructionSet.getOPERAND()));
		fw2.write(end);
		fw1.flush();
		fw1.close();
		fw2.flush();
		fw2.close();
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

