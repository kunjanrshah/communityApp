package com.krs.community.model;


import com.krs.community.R;

/**
 * Created by Bowyer on 15/08/06.
 */
public class BottomSheet {

  public enum BottomSheetMenuType {
    EMAIL(R.drawable.ic_drafts_white_24dp, "Add Relative"), ACCOUNT(R.drawable.ic_account_circle_white_24dp,
        "Add Photo"), SETTING(R.drawable.ic_build_white_24dp, "Scan Photo");

    int resId;

    String name;

    BottomSheetMenuType(int resId, String name) {
      this.resId = resId;
      this.name = name;
    }

    public int getResId() {
      return resId;
    }

    public String getName() {
      return name;
    }
  }

  BottomSheetMenuType bottomSheetMenuType;

  public static BottomSheet to() {
    return new BottomSheet();
  }

  public BottomSheetMenuType getBottomSheetMenuType() {
    return bottomSheetMenuType;
  }

  public BottomSheet setBottomSheetMenuType(BottomSheetMenuType bottomSheetMenuType) {
    this.bottomSheetMenuType = bottomSheetMenuType;
    return this;
  }
}
