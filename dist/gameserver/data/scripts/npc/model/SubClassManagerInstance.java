package npc.model;

import java.util.Collection;
import java.util.StringTokenizer;

import l2s.commons.map.hash.TIntStringHashMap;
import l2s.gameserver.Config;
import l2s.gameserver.data.htm.HtmCache;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.instances.player.SubClass;
import l2s.gameserver.model.actor.instances.player.SubClassList;
import l2s.gameserver.model.base.ClassId;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.base.Experience;
import l2s.gameserver.model.instances.AwakeningManagerInstance;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.tables.SubClassTable;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.HtmlUtils;
import l2s.gameserver.utils.ItemFunctions;

import org.apache.commons.lang3.ArrayUtils;

/**
 * @author Bonux
 */
public final class SubClassManagerInstance extends NpcInstance
{
    private static final long serialVersionUID = 1L;

    // Предмет: Сертификат на Смену Профессии
    private static final int CERTIFICATE_ID = 30433;

    public SubClassManagerInstance(int objectId, NpcTemplate template)
    {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command)
    {
        if(!canBypassCheck(player, this))
            return;

        StringTokenizer st = new StringTokenizer(command, "_");
        String cmd = st.nextToken();
        if(cmd.equalsIgnoreCase("subclass"))
        {
            //Глобальные проверки, которые распространяются на любые операции с саб-классами.
            if(player.getServitors().length > 0)
            {
                showChatWindow(player, "default/" + getNpcId() + "-no_servitor.htm");
                return;
            }

            if(player.isTransformed())
            {
                showChatWindow(player, "default/" + getNpcId() + "-no_transform.htm");
                return;
            }

            if(player.getWeightPenalty() >= 3 || player.getInventoryLimit() * 0.8 < player.getInventory().getSize())
            {
                showChatWindow(player, "default/" + getNpcId() + "-no_weight.htm");
                return;
            }

            if(player.getLevel() < 40 || player.getClassId().getClassLevel().ordinal() < 2)
            {
                showChatWindow(player, "default/" + getNpcId() + "-no_level.htm");
                return;
            }

            // Действия с саб-классами.
            String cmd2 = st.nextToken();
            if(cmd2.equalsIgnoreCase("add")) // TODO: [Bonux] Сверить с оффом.
            {
                if(!checkSubClassQuest(player))
                {
                    showChatWindow(player, "default/" + getNpcId() + "-no_quest.htm");
                    return;
                }

                if(player.getSubClassList().size() >= SubClassList.MAX_SUB_COUNT)
                {
                    showChatWindow(player, "default/" + getNpcId() + "-add_no_limit.htm");
                    return;
                }

                // Проверка хватает ли уровня
                Collection<SubClass> subClasses = player.getSubClassList().values();
                for(SubClass subClass : subClasses)
                {
                    if(subClass.getLevel() < Config.ALT_GAME_LEVEL_TO_GET_SUBCLASS)
                    {
                        showChatWindow(player, "default/" + getNpcId() + "-add_no_level.htm", "<?LEVEL?>", Config.ALT_GAME_LEVEL_TO_GET_SUBCLASS);
                        return;
                    }
                }

                if(!st.hasMoreTokens())
                {
                    StringBuilder availSubList = new StringBuilder();
                    int[] availSubClasses = SubClassTable.getInstance().getAvailableSubClasses(player, player.getActiveClassId(), ClassLevel.SECOND);
                    for(int subClsId : availSubClasses)
                    {
                        // На оффе оно вбито в npcstring-*.dat. Из-за несоответствия байпассов, рисуем сами.
                        availSubList.append("<a action=\"bypass -h npc_%objectId%_subclass_add_" + subClsId + "\">" + HtmlUtils.htmlClassName(subClsId) + "</a><br>");
                    }
                    showChatWindow(player, "default/" + getNpcId() + "-add_list.htm", "<?ADD_SUB_LIST?>", availSubList.toString());
                    return;
                }
                else
                {
                    int addSubClassId = Integer.parseInt(st.nextToken());
                    if(!st.hasMoreTokens())
                    {
                        String addSubConfirm = "<a action=\"bypass -h npc_%objectId%_subclass_add_" + addSubClassId + "_confirm\">" + HtmlUtils.htmlClassName(addSubClassId) + "</a>";
                        showChatWindow(player, "default/" + getNpcId() + "-add_confirm.htm", "<?ADD_SUB_CONFIRM?>", addSubConfirm);
                        return;
                    }
                    else
                    {
                        String cmd3 = st.nextToken();
                        if(cmd3.equalsIgnoreCase("confirm"))
                        {
							/*TODO: [Bonux] Проверить на оффе.
							if(Config.ENABLE_OLYMPIAD && Olympiad.isRegisteredInComp(player))
							{
								player.sendPacket(SystemMsg.C1_DOES_NOT_MEET_THE_PARTICIPATION_REQUIREMENTS_SUBCLASS_CHARACTER_CANNOT_PARTICIPATE_IN_THE_OLYMPIAD);
								return;
							}*/

                            if(player.addSubClass(addSubClassId, true, 0, 0, 0L, 0))
                            {
                                player.sendPacket(new SystemMessagePacket(SystemMsg.THE_NEW_SUBCLASS_S1_HAS_BEEN_ADDED).addClassName(addSubClassId));
                                showChatWindow(player, "default/" + getNpcId() + "-add_success.htm");
                                return;
                            }
                            else
                            {
                                showChatWindow(player, "default/" + getNpcId() + "-add_error.htm");
                                return;
                            }
                        }
                    }
                }
            }
            else if(cmd2.equalsIgnoreCase("change"))
            {
                if(!checkSubClassQuest(player) && !player.getSubClassList().haveSubClasses())
                {
                    showChatWindow(player, "default/" + getNpcId() + "-no_quest.htm");
                    return;
                }

                if(ItemFunctions.getItemCount(player, CERTIFICATE_ID) == 0)
                {
                    showChatWindow(player, "default/" + getNpcId() + "-no_certificate.htm");
                    return;
                }

                if(checkSubClassQuest(player) && !player.getSubClassList().haveSubClasses()) // TODO: [Bonux] Проверить сообщение на оффе.
                {
                    showChatWindow(player, "default/" + getNpcId() + "-no_subs.htm");
                    return;
                }

                if(player.getClassId().isOfLevel(ClassLevel.AWAKED))
                {
                    showChatWindow(player, "default/" + getNpcId() + "-change_no_awake.htm");
                    return;
                }

                if(!st.hasMoreTokens())
                {
                    StringBuilder mySubList = new StringBuilder();
                    Collection<SubClass> subClasses = player.getSubClassList().values();
                    for(SubClass sub : subClasses)
                    {
                        if(sub == null) // Не может быть, но на всякий пожарный.
                            continue;

                        if(sub.isBase())
                            continue;

                        if(sub.isDual()) // Двойной саб-класс отменить нельзя.
                            continue;

                        int classId = sub.getClassId();
                        // На оффе оно вбито в npcstring-*.dat. Из-за несоответствия байпассов, рисуем сами.
                        mySubList.append("<a action=\"bypass -h npc_%objectId%_subclass_change_" + classId + "\">" + HtmlUtils.htmlClassName(classId) + "</a><br>");
                    }
                    showChatWindow(player, "default/" + getNpcId() + "-change_list.htm", "<?CHANGE_SUB_LIST?>", mySubList.toString());
                    return;
                }
                else
                {
                    int changeClassId = Integer.parseInt(st.nextToken());
                    if(!st.hasMoreTokens())
                    {
                        StringBuilder availSubList = new StringBuilder();
                        int[] availSubClasses = SubClassTable.getInstance().getAvailableSubClasses(player, changeClassId, ClassId.VALUES[changeClassId].getClassLevel());
                        for(int subClsId : availSubClasses)
                        {
                            // На оффе оно вбито в npcstring-*.dat. Из-за несоответствия байпассов, рисуем сами.
                            availSubList.append("<a action=\"bypass -h npc_%objectId%_subclass_change_" + changeClassId + "_" + subClsId + "\">" + HtmlUtils.htmlClassName(subClsId) + "</a><br>");
                        }
                        showChatWindow(player, "default/" + getNpcId() + "-change_change_list.htm", "<?CHANGE_CHANGE_SUB_LIST?>", availSubList.toString());
                        return;
                    }
                    else
                    {
                        int newSubClassId = Integer.parseInt(st.nextToken());
                        if(!st.hasMoreTokens())
                        {
                            String newSubConfirm = "<a action=\"bypass -h npc_%objectId%_subclass_change_" + changeClassId + "_" + newSubClassId + "_confirm\">" + HtmlUtils.htmlClassName(newSubClassId) + "</a>";
                            showChatWindow(player, "default/" + getNpcId() + "-change_confirm.htm", "<?CHANGE_SUB_CONFIRM?>", newSubConfirm);
                            return;
                        }
                        else
                        {
                            String cmd3 = st.nextToken();
                            if(cmd3.equalsIgnoreCase("confirm"))
                            {
                                SubClass oldSubClass = player.getSubClassList().getByClassId(changeClassId);
                                long exp = oldSubClass.getExp();
                                int sp = oldSubClass.getSp();
                                if(player.modifySubClass(changeClassId, newSubClassId, true))
                                {
                                    ItemFunctions.removeItem(player, CERTIFICATE_ID, 1, true);
                                    player.sendPacket(new SystemMessagePacket(SystemMsg.THE_NEW_SUBCLASS_S1_HAS_BEEN_ADDED).addClassName(newSubClassId));
                                    showChatWindow(player, "default/" + getNpcId() + "-add_success.htm");
                                    return;
                                }
                                else
                                {
                                    showChatWindow(player, "default/" + getNpcId() + "-add_error.htm");
                                    return;
                                }
                            }
                        }
                    }
                }
            }
            else if(cmd2.equalsIgnoreCase("cancel"))
            {
                if(!checkSubClassQuest(player) && !player.getSubClassList().haveSubClasses())
                {
                    showChatWindow(player, "default/" + getNpcId() + "-no_quest.htm");
                    return;
                }

                if(checkSubClassQuest(player) && !player.getSubClassList().haveSubClasses()) // TODO: [Bonux] Проверить сообщение на оффе.
                {
                    showChatWindow(player, "default/" + getNpcId() + "-no_subs.htm");
                    return;
                }

                if(!st.hasMoreTokens())
                {
                    StringBuilder mySubList = new StringBuilder();
                    Collection<SubClass> subClasses = player.getSubClassList().values();
                    for(SubClass sub : subClasses)
                    {
                        if(sub == null) // Не может быть, но на всякий пожарный.
                            continue;

                        if(sub.isBase())
                            continue;

                        if(sub.isDual()) // Двойной саб-класс отменить нельзя.
                            continue;

                        int classId = sub.getClassId();
                        // На оффе оно вбито в npcstring-*.dat. Из-за несоответствия байпассов, рисуем сами.
                        mySubList.append("<a action=\"bypass -h npc_%objectId%_subclass_cancel_" + classId + "\">" + HtmlUtils.htmlClassName(classId) + "</a><br>");
                    }
                    showChatWindow(player, "default/" + getNpcId() + "-cancel_list.htm", "<?CANCEL_SUB_LIST?>", mySubList.toString());
                    return;
                }
                else
                {
                    int cancelClassId = Integer.parseInt(st.nextToken());
                    if(!st.hasMoreTokens())
                    {
                        StringBuilder availSubList = new StringBuilder();
                        int[] availSubClasses = SubClassTable.getInstance().getAvailableSubClasses(player, cancelClassId, ClassLevel.SECOND);
                        for(int subClsId : availSubClasses)
                        {
                            // На оффе оно вбито в npcstring-*.dat. Из-за несоответствия байпассов, рисуем сами.
                            availSubList.append("<a action=\"bypass -h npc_%objectId%_subclass_cancel_" + cancelClassId + "_" + subClsId + "\">" + HtmlUtils.htmlClassName(subClsId) + "</a><br>");
                        }
                        showChatWindow(player, "default/" + getNpcId() + "-cancel_change_list.htm", "<?CANCEL_CHANGE_SUB_LIST?>", availSubList.toString());
                        return;
                    }
                    else
                    {
                        int newSubClassId = Integer.parseInt(st.nextToken());
                        if(!st.hasMoreTokens())
                        {
                            String newSubConfirm = "<a action=\"bypass -h npc_%objectId%_subclass_cancel_" + cancelClassId + "_" + newSubClassId + "_confirm\">" + HtmlUtils.htmlClassName(newSubClassId) + "</a>";
                            showChatWindow(player, "default/" + getNpcId() + "-cancel_confirm.htm", "<?CANCEL_SUB_CONFIRM?>", newSubConfirm);
                            return;
                        }
                        else
                        {
                            String cmd3 = st.nextToken();
                            if(cmd3.equalsIgnoreCase("confirm"))
                            {
                                if(player.modifySubClass(cancelClassId, newSubClassId, false))
                                {
                                    player.sendPacket(new SystemMessagePacket(SystemMsg.THE_NEW_SUBCLASS_S1_HAS_BEEN_ADDED).addClassName(newSubClassId));
                                    showChatWindow(player, "default/" + getNpcId() + "-add_success.htm");
                                    return;
                                }
                                else
                                {
                                    showChatWindow(player, "default/" + getNpcId() + "-add_error.htm");
                                    return;
                                }
                            }
                        }
                    }
                }
            }
            else if(cmd2.equalsIgnoreCase("reawake"))
            {
                if(!player.isDualClassActive() || !player.getClassId().isOfLevel(ClassLevel.AWAKED))
                {
                    showChatWindow(player, "default/" + getNpcId() + "-reawake_no.htm");
                    return;
                }

                if(!st.hasMoreTokens())
                {
                    int cost = calcReawakeCost(player);
                    if(player.getAdena() < cost)
                    {
                        showChatWindow(player, "default/" + getNpcId() + "-reawake_no_adena.htm", "<?reawake_price?>", String.valueOf(cost));
                        return;
                    }

                    int cloakId = player.getClassId().getCloakId();
                    if(cloakId == 0 || ItemFunctions.getItemCount(player, cloakId) == 0)
                    {
                        // [Bonux] На оффе нету отдельного диалога при отсутствии плаща. На оффе выводится диалог отсутствия адены.
                        showChatWindow(player, "default/" + getNpcId() + "-reawake_no_cloak.htm", "<?reawake_price?>", String.valueOf(cost));
                        return;
                    }

                    showChatWindow(player, "default/" + getNpcId() + "-reawake_continue.htm", "<?reawake_price?>", String.valueOf(cost));
                    return;
                }

                String cmd3 = st.nextToken();
                if(cmd3.equalsIgnoreCase("continue"))
                {
                    if(!st.hasMoreTokens())
                    {
                        TIntStringHashMap tpls = HtmCache.getInstance().getTemplates("default/" + getNpcId() + "-reawake_list.htm", player);
                        String html = tpls.getNotNull(0);
                        String bypass = tpls.getNotNull(1);

                        StringBuilder reawakeList = new StringBuilder();
                        int changeClassId = player.getClassId().getId();
                        for(ClassId clsId : ClassId.VALUES)
                        {
                            if(!clsId.isOutdated())
                                continue;

                            if(!clsId.isOfLevel(ClassLevel.AWAKED))
                                continue;

                            reawakeList.append(bypass.replace("<?change_class_id?>", String.valueOf(changeClassId)).replace("<?class_id?>", String.valueOf(clsId.getId())).replace("<?class_name?>", HtmlUtils.htmlClassName(clsId.getId())));
                            reawakeList.append("<br>");
                        }

                        showChatWindow(player, html, "<?AVAILABLE_REAWAKE_LIST?>", reawakeList.toString());
                        return;
                    }
                    else
                    {
                        int playerClassId = player.getClassId().getId();
                        int changeClassId = Integer.parseInt(st.nextToken());
                        int newClassId = Integer.parseInt(st.nextToken());

                        if(changeClassId != playerClassId) // На всякий пожарный..
                            return;

                        if(!st.hasMoreTokens())
                        {
                            int[] availClasses = SubClassTable.getInstance().getAvailableSubClasses(player, changeClassId, ClassLevel.AWAKED);
                            ClassId newClsId = ClassId.VALUES[newClassId];

                            TIntStringHashMap tpls = HtmCache.getInstance().getTemplates("default/" + getNpcId() + "-reawake_last_list.htm", player);
                            String html = tpls.getNotNull(0);
                            String bypass = tpls.getNotNull(1);

                            boolean avail = false;

                            StringBuilder classes = new StringBuilder();
                            for(ClassId c : ClassId.VALUES)
                            {
                                if(c.getBaseAwakedClassId() != newClsId)
                                    continue;

                                if(!ArrayUtils.contains(availClasses, c.getId()))
                                    continue;

                                classes.append(bypass.replace("<?change_class_id?>", String.valueOf(changeClassId)).replace("<?class_id?>", String.valueOf(c.getId())).replace("<?class_name?>", c.getName(player)));
                                classes.append("<br>");
                                avail = true;
                            }

                            if(!avail)
                            {
                                showChatWindow(player, "default/" + getNpcId() + "-reawake_no_avail.htm");
                                return;
                            }

                            showChatWindow(player, html, "<?CLASS_LIST?>", classes.toString());
                            return;
                        }
                        else
                        {
                            String cmd4 = st.nextToken();
                            if(cmd4.equalsIgnoreCase("finish"))
                            {
                                if(changeClassId == newClassId || playerClassId == newClassId) // На всякий пожарный..
                                    return;

                                int cost = calcReawakeCost(player);
                                if(player.getAdena() < cost)
                                {
                                    showChatWindow(player, "default/" + getNpcId() + "-reawake_no_adena.htm", "<?reawake_price?>", String.valueOf(cost));
                                    return;
                                }

                                int cloakId = player.getClassId().getCloakId();
                                if(cloakId == 0 || ItemFunctions.getItemCount(player, cloakId) == 0)
                                {
                                    showChatWindow(player, "default/" + getNpcId() + "-reawake_no_adena.htm", "<?reawake_price?>", String.valueOf(cost));
                                    return;
                                }

                                if(player.modifySubClass(changeClassId, newClassId, true))
                                {
                                    // Сбрасываем до 85 уровня.
                                    long newExp = Experience.LEVEL[85] - player.getExp();
                                    player.addExpAndSp(newExp, 0, true);

                                    player.reduceAdena(cost, true);
                                    ItemFunctions.removeItem(player, cloakId, 1, true);

                                    player.sendPacket(new SystemMessagePacket(SystemMsg.THE_NEW_SUBCLASS_S1_HAS_BEEN_ADDED).addClassName(newClassId));
                                    ItemFunctions.addItem(player, player.getClassId().getCloakId(), 1, true);

                                    ItemFunctions.removeItem(player, AwakeningManagerInstance.CHAOS_POMANDER_DUAL_CLASS, ItemFunctions.getItemCount(player, AwakeningManagerInstance.CHAOS_POMANDER_DUAL_CLASS), false);
                                    ItemFunctions.addItem(player, AwakeningManagerInstance.CHAOS_POMANDER_DUAL_CLASS, 2, true);

                                    showChatWindow(player, "default/" + getNpcId() + "-reawake_success.htm");
                                    return;
                                }
                                else
                                {
                                    showChatWindow(player, "default/" + getNpcId() + "-reawake_error.htm");
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }
        else
            super.onBypassFeedback(player, command);
    }

    private static boolean checkSubClassQuest(Player player)
    {
        if(!Config.ALT_GAME_SUBCLASS_WITHOUT_QUESTS)
            return player.isQuestCompleted("_10385_RedThreadofFate");
        return true;
    }

    private static int calcReawakeCost(Player player)
    {
        int level = player.getLevel();
        switch(level)
        {
            case 86:
                return 90000000;
            case 87:
                return 80000000;
            case 88:
                return 70000000;
            case 89:
                return 60000000;
            case 90:
                return 50000000;
            case 91:
                return 40000000;
            case 92:
                return 30000000;
            case 93:
                return 20000000;
            case 94:
                return 10000000;
            case 95:
            case 96:
            case 97:
            case 98:
            case 99:
                return 0;
        }
        return 100000000;
    }
}