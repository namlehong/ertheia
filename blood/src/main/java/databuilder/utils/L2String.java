package databuilder.utils;

import java.util.LinkedList;

import databuilder.utils.diff_match_patch.Diff;
import databuilder.utils.diff_match_patch.LinesToCharsResult;
import databuilder.utils.diff_match_patch.Operation;

public class L2String {
	public static String diffDesc(String textA, String textB){
		diff_match_patch patcher = new diff_match_patch();
		
		textA = textA.replaceAll("(P|M|Rsk)(\\.)","$1");
		textB = textB.replaceAll("(P|M|Rsk)(\\.)","$1");
		LinkedList<Diff> diffResult = patcher.diff_main(textA, textB);
//		LinesToCharsResult diffResult = patcher.diff_linesToChars(textA, textB);
		patcher.diff_cleanupSemantic(diffResult);
//		patcher.diff_cleanupSemanticLossless(diffResult);
//		patcher.Diff_EditCost = 4;
//		patcher.diff_cleanupEfficiency(diffResult);
		
//		System.out.println(diffResult);
		
		
		StringBuilder sb = new StringBuilder();
		
		Diff nextBlock = null;
		
		for(int i = 0; i < diffResult.size(); i++)
		{
			Diff diffBlock = diffResult.get(i);
			
			switch(diffBlock.operation){
			case DELETE:
			case INSERT:
				if(i > 0){
					Diff prevBlock = diffResult.get(i-1);
					if(prevBlock.operation == Operation.EQUAL)
					{
						int prevDot = prevBlock.text.lastIndexOf(".");
						if(!prevBlock.equals(nextBlock) || prevDot > 0)
							sb.append(prevBlock.text.substring(prevDot+1));
					}					
				}
				
				if(diffBlock.operation == Operation.INSERT)
					sb.append(diffBlock.text);
				
				if(i < diffResult.size() -1)
				{
					nextBlock = diffResult.get(i+1);
					if(nextBlock.operation == Operation.EQUAL)
					{
						int dotIndex = nextBlock.text.indexOf(".");
						if(dotIndex > 0)
							sb.append(nextBlock.text.substring(0, nextBlock.text.indexOf(".")+1));
						else
							sb.append(nextBlock.text);
					}
				}
				break;
			default:
				break;
			}
		}
		
		return sb.toString().trim();
	}
}
