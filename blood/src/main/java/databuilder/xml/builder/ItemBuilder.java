package databuilder.xml.builder;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import l2s.gameserver.model.items.ItemInfo;
import l2s.gameserver.stats.conditions.Condition;
import l2s.gameserver.stats.conditions.ConditionLogicAnd;
import l2s.gameserver.stats.conditions.ConditionPlayerMaxLevel;
import l2s.gameserver.stats.conditions.ConditionPlayerMinLevel;
import l2s.gameserver.templates.item.Bodypart;
import l2s.gameserver.templates.item.ItemGrade;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import databuilder.MainBuilder;
import databuilder.utils.XmlPretty;

public class ItemBuilder {
	private HashMap<String, Element> _rootHolder = new HashMap<String, Element>();
	private HashMap<Integer, Element> _l2sHolder = new HashMap<Integer, Element>();
	private HashMap<Integer, L2ItemInfo> _itemHolder = new HashMap<Integer, L2ItemInfo>();
	
	public class L2ItemInfo{
		int _id;
		String _item_type;
		String _name, _add_name, _description;
		String _card, _icon;
		boolean _can_exchange, _can_drop, _can_destroy, _can_privatestore, _can_auction, _can_public_store;
		int _tooltip;
		int _rarity, _durability, _weight, _material, _family, _grade,
		_body_part, _random_damage, _weapon_type, _handness, _mp_consume, _SS_count, _SPS_count;
		boolean _crystallizable, _stackable, _is_mage;
		
		
		double _pdef, _mdef, _patt, _matt, _atk_speed, _accuracy, 
		_magic_accuracy, _critical, _magic_critical, _move_speed, 
		_shield_pdef, _shield_rate, _evasion, _magic_evasion;
		
		Element _element = null;
		
		private final List<Condition> _conditions = new ArrayList<Condition>();
		
		public L2ItemInfo(ResultSet rset){
			try{
			_id = rset.getInt("id");
			_name = rset.getString("name");
			_item_type = rset.getString("item_type");
			_add_name = rset.getString("add_name");
			_description = rset.getString("description").replace("--", "-");
			_tooltip = rset.getInt("tooltip");
			_rarity = rset.getInt("rarity");
			_card = rset.getString("Card");
			_can_exchange = rset.getBoolean("can_exchange");
			_can_drop = rset.getBoolean("can_drop");
			_can_destroy = rset.getBoolean("can_destroy");
			_can_privatestore = rset.getBoolean("can_private_store");
			_can_auction = rset.getBoolean("can_auction");
			_can_public_store = rset.getBoolean("can_public_store");
			
			_icon = rset.getString("icon");
			_durability = rset.getInt("durability");
			_weight = rset.getInt("weight");
			_material = rset.getInt("material");
			_family = rset.getInt("family");
			
			_crystallizable = rset.getBoolean("crystallizable");
			_stackable = rset.getBoolean("stackable");
			_is_mage = rset.getBoolean("is_mage");
			
			_pdef = rset.getDouble("pdef");
			_mdef = rset.getDouble("mdef");
			_patt = rset.getDouble("patt");
			_matt = rset.getDouble("matt");
			_atk_speed = rset.getDouble("atk_speed");
			_accuracy = rset.getDouble("accuracy");
			_magic_accuracy = rset.getDouble("magic_accuracy");
			_critical = rset.getDouble("critical");
			_magic_critical = rset.getDouble("magic_critical");
			_move_speed = rset.getDouble("move_speed");
			_shield_pdef = rset.getDouble("shield_pdef");
			_shield_rate = rset.getDouble("shield_rate");
			_evasion = rset.getDouble("evasion");
			_magic_evasion = rset.getDouble("magic_evasion");
			
			_body_part = rset.getInt("body_part");
			_random_damage = rset.getInt("random_damage");
			_weapon_type = rset.getInt("weapon_type");
			_handness = rset.getInt("handness");
			_mp_consume = rset.getInt("mp_consume");
			_SS_count = rset.getInt("SS_count");
			_SPS_count = rset.getInt("SPS_count");
			
			_grade = rset.getInt("grade");
			
			parseDescription();
			
			_itemHolder.put(_id, this);
			}catch (SQLException e){
				System.out.println(e);
			}
		}
		
		public void addCondition(Element condElement, Condition cond){
			if(cond instanceof ConditionLogicAnd){
				for(Condition subcond: ((ConditionLogicAnd)cond)._conditions){
					addCondition(condElement, subcond);
				}
			}
			if(cond instanceof ConditionPlayerMinLevel){
				condElement.addElement("player").addAttribute("minLevel", Integer.toString(((ConditionPlayerMinLevel) cond)._level));
			}
			if(cond instanceof ConditionPlayerMaxLevel){
				condElement.addElement("player").addAttribute("maxLevel", Integer.toString(((ConditionPlayerMaxLevel) cond)._level));
			}
		}
		
		public Element getElement()
		{
			if(_element == null)
			{
				_element = _l2sHolder.get(_id);
				
				if(_element != null)
				{
					if(!_description.isEmpty())
						getRoot(_id).addComment(String.format("\n\t\t%s - %s\n\t\t%s\n\t", _id, _name, _description));
					getRoot(_id).add(_element.detach());
					
				}
			}
			
			if(_element == null)
			{
				System.out.println("Missing:"+_id);
				if(!_description.isEmpty())
					getRoot(_id).addComment(String.format("\n\t\t%s - %s\n\t\t%s\n\t", _id, _name, _description));
				_element = getRoot(_id).addElement(_item_type).addComment("Full generated by blood");
				_element.addAttribute("id", Integer.toString(_id) );
				if(_is_mage) _element.addElement("set").addAttribute("name", "is_magic_weapon").addAttribute("value", "true");
				// TODO calculate crystal here
				_element.addElement("set").addAttribute("name", "crystal_type").addAttribute("value", getCrystalType());
				_element.addElement("set").addAttribute("name", "icon").addAttribute("value", _icon);
				// TODO add price here
				if(!_can_exchange) _element.addElement("set").addAttribute("name", "tradeable").addAttribute("value", "false");
				if(!_can_drop) _element.addElement("set").addAttribute("name", "dropable").addAttribute("value", "false");
				if(!_can_destroy) _element.addElement("set").addAttribute("name", "destroyable").addAttribute("value", "false");
				if(!_can_privatestore) _element.addElement("set").addAttribute("name", "privatestoreable").addAttribute("value", "false");
				if(!_can_auction) _element.addElement("set").addAttribute("name", "commissionable").addAttribute("value", "false");
				if(!_can_public_store) _element.addElement("set").addAttribute("name", "sellable").addAttribute("value", "false");
				if(_tooltip == 9 || _tooltip == 15) _element.addElement("set").addAttribute("name", "freightable").addAttribute("value", "true");
				
				if(_durability > 0) _element.addElement("set").addAttribute("name", "durability").addAttribute("value", Integer.toString(_durability));
				if(_weight > 0) _element.addElement("set").addAttribute("name", "weight").addAttribute("value", Integer.toString(_weight));
				
				if(isWeapon())
				{
					_element.addElement("set").addAttribute("name", "rnd_dam").addAttribute("value", Integer.toString(_random_damage));
					_element.addElement("set").addAttribute("name", "soulshots").addAttribute("value", Integer.toString(_SS_count));
					_element.addElement("set").addAttribute("name", "spiritshots").addAttribute("value", Integer.toString(_SPS_count));
				}
								
				_element.addElement("set").addAttribute("name", "type").addAttribute("value", getItemType());
				
				
				// equip
				List<Bodypart> equip = getEquipType();
				if(equip.size() > 0)
				{
					Element equipElement = _element.addElement("equip");
					for(Bodypart slot_id: equip)
						equipElement.addElement("slot").addAttribute("id", slot_id.toString());
				}
				
				// for
				if(_pdef != 0 || _mdef != 0 || _patt != 0 || _matt != 0
						|| _atk_speed != 0 || _move_speed != 0 
						|| _accuracy != 0 || _magic_accuracy != 0 || _critical != 0 || _magic_critical != 0
						|| _shield_pdef != 0 || _shield_rate != 0 || _evasion != 0  || _magic_evasion != 0){
					Element forElement = _element.addElement("for");
					if(_pdef != 0) forElement.addElement("add").addAttribute("stat", "pDef").addAttribute("order", "0x10").addAttribute("value", Double.toString(_pdef));
					if(_mdef != 0) forElement.addElement("add").addAttribute("stat", "mDef").addAttribute("order", "0x10").addAttribute("value", Double.toString(_mdef));
					if(_patt != 0) forElement.addElement("add").addAttribute("stat", "pAtk").addAttribute("order", "0x10").addAttribute("value", Double.toString(_patt));
					if(_matt != 0) forElement.addElement("add").addAttribute("stat", "mAtk").addAttribute("order", "0x10").addAttribute("value", Double.toString(_matt));
					if(_atk_speed != 0) forElement.addElement("set").addAttribute("stat", "basePAtkSpd").addAttribute("order", "0x08").addAttribute("value", Double.toString(_atk_speed));
					if(_move_speed != 0) forElement.addElement("add").addAttribute("stat", "runSpd").addAttribute("order", isWeapon() ? "0x08":"0x60").addAttribute("value", Double.toString(_move_speed));
					if(_critical != 0) forElement.addElement(isWeapon() ? "set" : "add").addAttribute("stat", "basePCritRate").addAttribute("order", isWeapon() ? "0x08":"0x60").addAttribute("value", Double.toString(_critical));
					if(_magic_critical != 0) forElement.addElement(isWeapon() ? "set" : "add").addAttribute("stat", "baseMCritRate").addAttribute("order", isWeapon() ? "0x08":"0x60").addAttribute("value", Double.toString(_magic_critical));
					if(_accuracy != 0) forElement.addElement("add").addAttribute("stat", "pAccCombat").addAttribute("order", "0x08").addAttribute("value", Double.toString(_accuracy));
					if(_magic_accuracy != 0) forElement.addElement("add").addAttribute("stat", "mAccCombat").addAttribute("order", "0x08").addAttribute("value", Double.toString(_magic_accuracy));
					if(_shield_pdef != 0) forElement.addElement("add").addAttribute("stat", "sDef").addAttribute("order", "0x10").addAttribute("value", Double.toString(_shield_pdef));
					if(_shield_rate != 0) forElement.addElement("add").addAttribute("stat", "rShld").addAttribute("order", "0x10").addAttribute("value", Double.toString(_shield_rate));
					if(_evasion != 0) forElement.addElement("add").addAttribute("stat", "pEvasRate").addAttribute("order", "0x10").addAttribute("value", Double.toString(_evasion));
					if(_magic_evasion != 0) forElement.addElement("add").addAttribute("stat", "mEvasRate").addAttribute("order", "0x10").addAttribute("value", Double.toString(_magic_evasion));
				}
				
				
			}
			
			_element.addAttribute("name", _name);
			if(!_add_name.isEmpty()) _element.addAttribute("add_name", _add_name);
			
			_element.addComment("added by blood");
			
			if(!_card.isEmpty()) _element.addElement("set").addAttribute("name", "card").addAttribute("value", _card);
			_element.addElement("set").addAttribute("name", "grade").addAttribute("value", Integer.toString(_grade));
			_element.addElement("set").addAttribute("name", "rarity").addAttribute("value", Integer.toString(_rarity));
			_element.addElement("set").addAttribute("name", "material").addAttribute("value", Integer.toString(_material));
			
			// condition
			if(_conditions.size() > 0){
				Element condElement = _element.addElement("cond");
				for(Condition condition: _conditions){
					addCondition(condElement, condition);
				}
			}
			
			return _element;
		}
		
		public List<Bodypart> getEquipType(){
			List<Bodypart> result = new ArrayList<Bodypart>();
			
			
			switch (_body_part) {
			case 1: result.add(Bodypart.RIGHT_EAR); result.add(Bodypart.LEFT_EAR); break;
			case 3: result.add(Bodypart.NECKLACE); break;
			case 4: result.add(Bodypart.RIGHT_FINGER); result.add(Bodypart.LEFT_FINGER); break;
			case 6: result.add(Bodypart.HEAD); break;
			case 7: result.add(Bodypart.LEFT_RIGHT_HAND); break;
			case 8: result.add(Bodypart.FULL_ARMOR); break;
			case 9: result.add(Bodypart.FORMAL_WEAR); break;
			case 10: result.add(Bodypart.HAIR_ALL); break;
			case 12: result.add(Bodypart.LEFT_BRACELET); break;
			case 19: result.add(Bodypart.BELT); break;
			case 20: result.add(Bodypart.BROOCH); break;
			case 21: result.add(Bodypart.JEWEL); break;
			case 27: result.add(Bodypart.GLOVES); break;
			case 28: result.add(Bodypart.CHEST); break;
			case 29: result.add(Bodypart.LEGS); break;
			case 30: result.add(Bodypart.FEET); break;
			case 31: result.add(Bodypart.BACK); break;
			case 32: result.add(Bodypart.FACE); break;
			case 33: result.add(Bodypart.HAIR); break;
			case 34: result.add(Bodypart.RIGHT_HAND); break;
			case 35: result.add(Bodypart.LEFT_HAND); break;
			

			default:
				break;
			}
				
			if(result.size() == 0 && isWeapon()){
				result.add(Bodypart.RIGHT_HAND);
			}
			
			return result;
		}
		
		private String getCrystalType() {
			if(_item_type.equalsIgnoreCase("etcitem")) return ItemGrade.NONE.toString();
			
			switch (_grade) {
			case 1: return ItemGrade.D.toString();
			case 2: return ItemGrade.C.toString();
			case 3: return ItemGrade.B.toString();
			case 4: return ItemGrade.A.toString();
			case 5:
			case 6: return ItemGrade.S.toString();
			case 8:
			case 9:
			case 10: return ItemGrade.R.toString();
			default: return ItemGrade.NONE.toString();
			}
		}

		public String getItemType()
		{
			if(isWeapon())
			{
				switch (_weapon_type) {
				case 1:
					return _body_part == 7 ? "BIGSWORD" : "SWORD";
					
				case 2:
					return _body_part == 7 ? "BIGBLUNT" : "BLUNT";
					
				case 3:
					return "DAGGER";
					
				case 4:
					return "POLE";
					
				case 5:
					return "DUALFIST";
					
				case 6:
					return "BOW";
					
				case 7:
					return "ETC";
					
				case 8:
					return "DUAL";
					
				case 10:
					return "ROD";
					
				case 11:
					return "RAPIER";
				
				case 12:
					return "CROSSBOW";
					
				case 13:
					return "ANCIENTSWORD";
					
				case 15:
					return "DUALDAGGER";
					
				case 17:
					return "CROSSBOW";
					
				case 18:
					return "DUALBLUNT";

				default:
					break;
				}
			}
			
			return "NONE";
		}
		
		public boolean isWeapon(){
			return _item_type.equalsIgnoreCase("weapon");
		}
		
		public boolean isArmor(){
			return _item_type.equalsIgnoreCase("armor");
		}
		
		public boolean isEtcitem(){
			return _item_type.equalsIgnoreCase("armor");
		}
		
		public String toString(){
			return String.format("%s[id=%d, cond=%d]", _name, _id, _conditions.size());
		}
		
		protected void parseDescription()
		{
			if(_description.toLowerCase().contains("can be used by characters")){
				Condition cond = null;
				Matcher matcher = Pattern.compile("can be used by characters[^0-9]*(\\d+)[^0-9\\.]*(\\d*)").matcher(_description.toLowerCase());
				if(matcher.find()){
					cond = joinAnd(cond, new ConditionPlayerMinLevel(Integer.parseInt(matcher.group(1))));
					if(matcher.group(2) != null && !matcher.group(2).isEmpty())
						cond = joinAnd(cond, new ConditionPlayerMaxLevel(Integer.parseInt(matcher.group(2))));
				}
				
				if(cond != null)
					_conditions.add(cond);
				
			}
		}
		
		protected Condition joinAnd(Condition cond, Condition c)
		{
			if(cond == null)
				return c;
			if(cond instanceof ConditionLogicAnd)
			{
				((ConditionLogicAnd) cond).add(c);
				return cond;
			}
			ConditionLogicAnd and = new ConditionLogicAnd();
			and.add(cond);
			and.add(c);
			return and;
		}
	}
	
	private static final ItemBuilder _instance = new ItemBuilder();

	public ItemBuilder(){
		getItemName();
	}
	
	public static ItemBuilder getInstance()
	{
		return _instance;
	}
	
	public void addElement(int item_id, Element e){
		_l2sHolder.put(item_id, e);
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
	
	public L2ItemInfo getItem(int id){
		return _itemHolder.get(id);
	}
	
	public void getItemName(){
		try {
			PreparedStatement statement = MainBuilder.connection().prepareStatement("SELECT * FROM L2ItemName");
			ResultSet rset = statement.executeQuery();
			while(rset.next()){
				 new L2ItemInfo(rset);
			}
		}catch (SQLException e) {
			e.printStackTrace();
		};
	}
	
	public void store(){
		for(Map.Entry<String, Element> entry: _rootHolder.entrySet())
			XmlPretty.writeToFile(entry.getKey(), entry.getValue().asXML(), "item.dtd", "data/items/");
	}
	
	public void build()
	{
		for(Map.Entry<Integer, L2ItemInfo> entry: _itemHolder.entrySet())
		{
			entry.getValue().getElement();
		}
//		buildFated();
	}
	
	public void buildFated(){
		try {
			PreparedStatement statement = MainBuilder.connection().prepareStatement("SELECT id FROM L2ItemName where name LIKE ?");
			statement.setString(1, "Fated%");
			ResultSet rset = statement.executeQuery();
			while(rset.next()){
				L2ItemInfo info = getItem(rset.getInt("id"));
				info.getElement().addElement("set").addAttribute("name", "handler").addAttribute("value", "FateSupportBox");				
//				Element cond = item.addElement("cond");
				
			
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
	}
}
