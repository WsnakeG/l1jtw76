package com.lineage.data.item_etcitem.itemskill;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.model.skill.L1SkillUse;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * 精靈水晶通用技能使用器，支援一般施法與需選取目標的 SPELLSC 技能
 */
public class ItemSpiritCrystal extends ItemExecutor {

    public static ItemExecutor get() {
        return new ItemSpiritCrystal();
    }

    @Override
    public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
        if (pc == null || item == null) return;

        String className = item.getItem().getclassname();
        if (className == null || !className.contains("$")) {
            pc.sendPackets(new S_ServerMessage("沒有取得ClassName"));
            return;
        }

        String[] parts = className.split(" ");
        if (parts.length < 2) {
            pc.sendPackets(new S_ServerMessage("沒有取得參數"));
            return;
        }

        String nameId = parts[1];
        int deleteFlag = 1;
        if (parts.length >= 3) {
            try {
                deleteFlag = Integer.parseInt(parts[2]);
            } catch (NumberFormatException e) {
                pc.sendPackets(new S_ServerMessage(79));
                return;
            }
        }

        int skillId = getSkillIdByNameId(nameId);
        if (skillId == -1) {
            pc.sendPackets(new S_ServerMessage("找不到技能"));
            return;
        }

        // 這五種技能需要用 SPELLSC 模式處理
        boolean isSpellscTarget = false;
        switch (skillId) {
            case 152:  // 地面障礙
            case 157:  // 大地屏障
            case 167:  // 風之枷鎖
            case 173:  // 污濁之水
            case 174:  // 精準射擊
                isSpellscTarget = true;
                break;
        }

        if (isSpellscTarget) {
            // 判斷是否為 client 傳回來的點選座標階段
            if (data == null || data.length < 3 || data[0] == 0) {
                // 第一次點擊，僅進入 client 選擇目標流程，不消耗道具
                L1SkillUse prepare = new L1SkillUse();
                prepare.handleCommands(pc, skillId, 0, 0, 0, 0, L1SkillUse.TYPE_SPELLSC);
                return;
            }

            // client 已經點擊目標，正式施法，消耗道具
            int targetId = data[0];
            int targetX = data[1];
            int targetY = data[2];

            L1BuffUtil.cancelAbsoluteBarrier(pc);
            L1SkillUse l1skilluse = new L1SkillUse();
            l1skilluse.handleCommands(pc, skillId, targetId, targetX, targetY, 0, L1SkillUse.TYPE_NORMAL);

        } else {
            // 非 SPELLSC 類技能：立即施放
            L1BuffUtil.cancelAbsoluteBarrier(pc);
            L1SkillUse l1skilluse = new L1SkillUse();
            l1skilluse.handleCommands(pc, skillId, pc.getId(), pc.getX(), pc.getY(), 0, L1SkillUse.TYPE_NORMAL);
        }

        // 消耗道具
        if (deleteFlag == 1) {
            pc.getInventory().removeItem(item, 1);
        }

        // 成功訊息
        pc.sendPackets(new S_ServerMessage("手中的精靈水晶散發出耀眼的光芒"));
    }

    private int getSkillIdByNameId(String nameId) {
        switch (nameId.toLowerCase()) {
            case "$1837": return 148; // 火焰武器
            case "$1838": return 149; // 風之神射
            case "$1839": return 150; // 風之疾走
            case "$1840": return 151; // 大地防護
            case "$1841": return 152; // 地面障礙
            case "$1844": return 155; // 烈炎氣息
            case "$1845": return 156; // 暴風之眼
            case "$1846": return 157; // 大地屏障
            case "$1847": return 158; // 生命之泉
            case "$1848": return 159; // 大地的祝福
            case "$1851": return 163; // 烈炎武器
            case "$1852": return 164; // 生命的祝福
            case "$1853": return 165; // 生命呼喚
            case "$1854": return 166; // 暴風神射
            case "$1855": return 167; // 風之枷鎖
            case "$1856": return 168; // 鋼鐵防護
            case "$3265": return 169; // 體能激發
            case "$3266": return 170; // 水之元氣
            case "$3267": return 171; // 屬性之火
            case "$4714": return 175; // 烈焰之魂
            case "$4715": return 176; // 能量激發
            case "$4716": return 160; // 水之防護
            case "$4717": return 173; // 污濁之水
            case "$4718": return 174; // 精準射擊
            default: return -1;
        }
    }
}
