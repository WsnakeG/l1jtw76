package com.lineage.data.item_armor.set;

import com.lineage.server.model.Instance.L1PcInstance;

public class Effect_Hit_modifier
  implements ArmorSetEffect
{
  private final int _add;

  public Effect_Hit_modifier(int add)
  {
    _add = add;
  }

  public void giveEffect(L1PcInstance pc)
  {
    pc.addHitModifierByArmor(_add);
  }

  public void cancelEffect(L1PcInstance pc)
  {
    pc.addHitModifierByArmor(-_add);
  }

  public int get_mode()
  {
    return _add;
  }
}