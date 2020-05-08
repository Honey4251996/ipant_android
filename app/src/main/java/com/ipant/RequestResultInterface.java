package com.ipant;

import android.os.Bundle;

public interface RequestResultInterface {
    public void startNewActivity(Bundle bundle, Class newClass);
    public void onSucess(String eventType);
    public void onRequestRequirementFail(String failureMsg);


}
