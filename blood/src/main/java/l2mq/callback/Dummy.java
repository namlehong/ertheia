package l2mq.callback;

import org.gearman.GearmanFunction;
import org.gearman.GearmanFunctionCallback;


/**
 * The echo worker polls jobs from a job server and execute the echo function.
 *
 * The echo worker illustrates how to setup a basic worker
 */
public class Dummy implements GearmanFunction {

	@Override
    public byte[] work(String function, byte[] data, GearmanFunctionCallback callback) throws Exception 
    {

		String result 	= "";
		String dataStr = new String(data);
		@SuppressWarnings("unused")
		String[] wordList = dataStr.split(";", 6);
		
		System.out.println("Dummy: "+ dataStr);
		
		return result.getBytes();
    }
	
	

}
