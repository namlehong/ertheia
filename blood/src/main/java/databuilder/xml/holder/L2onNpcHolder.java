package databuilder.xml.holder;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import l2s.commons.data.xml.AbstractHolder;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import databuilder.utils.XmlPretty;
import databuilder.xml.holder.L2onNpcHolder.L2onNpcInfo;

/**
 * @author: VISTALL
 * @date:  16:15/14.12.2010
 */
public final class L2onNpcHolder extends AbstractHolder
{
	private static final L2onNpcHolder _instance = new L2onNpcHolder();

	private HashMap<String, Element> _rootHolder = new HashMap<String, Element>();
	private HashMap<Integer, Element> _l2sHolder = new HashMap<Integer, Element>();
	private TreeMap<Integer, L2onNpcInfo> _l2onInfoHolder = new TreeMap<Integer, L2onNpcInfo>();
	
	public class L2onNpcInfo{
		
		public int _id;
		public int _level = 0;
		public int _hp;
		public long _exp;
		public int _sp;
		
		public L2onNpcInfo(int id){
			_id = id;
			_l2onInfoHolder.put(_id, this);
		}
		
		public void setLevel(int value){
			_level = value;
		}
		
		public void setHp(int value){
			_hp = value;
		}
		
		public void setExp(long value){
			_exp = value;
		}
		
		public void setSp(int value){
			_sp = value;
		}
		
		public String toString(){
			return "ID:"+_id+" level:"+_level+" hp:"+_hp+" exp:"+_exp+" sp:"+_sp;
		}
		
	}

	public static L2onNpcHolder getInstance()
	{
		return _instance;
	}

	L2onNpcHolder()
	{

	}
	
	public L2onNpcInfo getNpcInfo(int id){
		return _l2onInfoHolder.get(id) != null ? _l2onInfoHolder.get(id) : new L2onNpcInfo(id);
	}
	
	public void addL2sElement(int npc_id, Element e){
		_l2sHolder.put(npc_id, e);
	}
	
	public Element getRoot(int id){
		int min_id = id - id%100;
		int max_id = min_id + 99;
		return getRoot(String.format("%05d-%05d", min_id, max_id));
	}
	
	public Element getRoot(String file){
		
		Element resultElement = _rootHolder.get(file);
		
		if(resultElement == null)
		{
			Document document = DocumentHelper.createDocument();
			resultElement = document.addElement("list");
			_rootHolder.put(file, resultElement);
		}
		
		return resultElement;
	}

	public void store(){
		for(Map.Entry<String, Element> entry: _rootHolder.entrySet())
			XmlPretty.writeToFile(entry.getKey(), entry.getValue().asXML(), "npc.dtd", "data/blood_droplist/");
	}
	
	public void build()
	{
		int count = 0;
		for(L2onNpcInfo info: _l2onInfoHolder.values())
		{
			count++;
			if(count%100 == 0){
				System.out.println("processed item: "+count);
			}
		}
	}
	
	
	@Override
	protected void process()
	{
//		build();
//		store();
	}
	

	@Override
	public int size()
	{
		return _l2onInfoHolder.size();
	}

	@Override
	public void clear()
	{
		_l2onInfoHolder.clear();
	}

	public L2onNpcInfo getNpcInfoNoCreate(int npcId) {
		// TODO Auto-generated method stub
		return _l2onInfoHolder.get(npcId);
	}
}
