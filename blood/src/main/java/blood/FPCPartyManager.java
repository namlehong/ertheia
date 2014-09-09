package blood;

import java.util.ArrayList;

import blood.base.FPCParty;

public class FPCPartyManager {
	
	public static FPCPartyManager _instance;
	protected ArrayList<FPCParty> _openParties = new ArrayList<FPCParty>();
	protected ArrayList<FPCParty> _fullParties = new ArrayList<FPCParty>();
	
	public static FPCPartyManager getInstance() {
        if (_instance == null) {
            _instance =
                new FPCPartyManager();
        }
        return _instance;
    }
	
	public FPCParty getParty(FPCInfo fpc)
	{
		for(FPCParty party: _openParties)
		{
			if(party.addMember(fpc.getActor()))
			{
				if(party.isFull())
				{
					_openParties.remove(party);
					_fullParties.add(party);
				}
				return party;
			}
		}
		
		// we dont have any party match requirement
		// let create new party
		FPCParty newParty = new FPCParty(fpc.getActor());
		// add to open list
		_openParties.add(newParty);
		return newParty;
	}
	
	public void reopenParty(FPCParty party)
	{
		_fullParties.remove(party);
		_openParties.add(party);
	}
	
	public void debug()
	{
		System.out.println("DEBUG: FPCPartyManager _openParties.size(): "+_openParties.size());
		System.out.println("DEBUG: FPCPartyManager _fullParties.size(): "+_fullParties.size());
	}

}
