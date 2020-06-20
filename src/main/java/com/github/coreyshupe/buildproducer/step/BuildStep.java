package com.github.coreyshupe.buildproducer.step;

import com.github.coreyshupe.buildproducer.script.BuildScriptTask;

public interface BuildStep {
    void reproduce(BuildScriptTask task);
}
