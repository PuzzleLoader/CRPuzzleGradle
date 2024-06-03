package dev.crmodders.puzzle.task.abstracts;

import dev.crmodders.puzzle.Constants;
import org.gradle.api.DefaultTask;

public abstract class AbstractPuzzleTask extends DefaultTask {
    public AbstractPuzzleTask() {
        setGroup(Constants.Tasks.GROUP);
    }
}
