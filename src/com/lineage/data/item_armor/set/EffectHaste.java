package com.lineage.data.item_armor.set;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_SkillHaste;

public class EffectHaste
  implements ArmorSetEffect
{
  private final int _add;

  public EffectHaste(int add)
  {
    _add = add;
  }

  public void giveEffect(L1PcInstance pc)
  {	  
      pc.addHasteItemEquipped(_add);
      pc.removeHasteSkillEffect();
      pc.sendPackets(new S_SkillHaste(pc.getId(), 1, -1));

      if (pc.getMoveSpeed() != 1) {
        pc.setMoveSpeed(1);
        pc.broadcastPacketAll(new S_SkillHaste(pc.getId(), 1, 0));
      }      
  }

  public void cancelEffect(L1PcInstance pc)
  {
      pc.addHasteItemEquipped(-_add);
      if (pc.getHasteItemEquipped() == 0) {
        pc.setMoveSpeed(0);
        pc.sendPacketsAll(new S_SkillHaste(pc.getId(), 0, 0));
      }    
  }

  public int get_mode()
  {
    return _add;
  }
}