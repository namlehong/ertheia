package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.instancemanager.CommissionManager;
import l2s.gameserver.model.Player;

/**
 * @author Bonux
 */
public class RequestCommissionList extends L2GameClientPacket
{
	public int _tree;
	public int _type;
	public int _quality;
	public int _grade;
	public String _searchWords;

	@Override
	protected void readImpl()
	{
		_tree = readD(); //Уровень дерева(1 - по типу, 2 - //)
		_type = readD(); //Тип (0 - Оружие, 1 - Доспехи, 2 - Аксессуары, 3 - Припасы, 4 - Предметы питомца, 5 - Остальное)
		_quality = readD(); //Вид (-1 - Все, 0 - Обычный, 1 - Редкий)
		_grade = readD(); //Grade
		_searchWords = readS(); //Фраза поиска.

	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		CommissionManager.getInstance().sendCommissionList(activeChar, _tree, _type, _quality, _grade, _searchWords);
	}
}
