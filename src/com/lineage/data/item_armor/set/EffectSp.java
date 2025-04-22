package com.lineage.data.item_armor.set;

import com.lineage.server.model.Instance.L1PcInstance;

public class EffectSp
  implements ArmorSetEffect
{
  private final int _add;

  public EffectSp(int add)
  {
    _add = add;
  }

  public void giveEffect(L1PcInstance pc)
  {
    pc.addSp(_add);
  }

  public void cancelEffect(L1PcInstance pc)
  {
    pc.addSp(-_add);
  }

  public int get_mode()
  {
    return _add;
  }
}