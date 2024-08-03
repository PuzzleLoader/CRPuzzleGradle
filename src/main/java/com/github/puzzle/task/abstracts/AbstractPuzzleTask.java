package com.github.puzzle.task.abstracts;

import com.github.puzzle.Constants;
import org.gradle.api.DefaultTask;

public abstract class AbstractPuzzleTask extends DefaultTask {
    public AbstractPuzzleTask() {
        setGroup(Constants.Tasks.GROUP);
    }
}
