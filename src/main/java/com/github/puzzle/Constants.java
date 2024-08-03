package com.github.puzzle;

import org.objectweb.asm.Opcodes;

public class Constants {
    public static final String COSMIC_REACH_REPO = "https://github.com/CRModders/CosmicArchive/raw/main/";
    public static final String COSMIC_REACH_JAR_NAME = "Cosmic Reach-[revision].jar";

    public static final String[] REPOS = new String[] {
            "https://jitpack.io", // Jitpack
            "https://maven.quiltmc.org/repository/release", // Quilt
            "https://maven.fabricmc.net/", // Fabric
            "https://repo.spongepowered.org/maven/" // Sponge
//            "https://maven.crmodders.dev/releases" // CRModders
    };


    public static final int ASM_VERSION = Opcodes.ASM9;

    public static final class Tasks {
        public static final String GROUP = "puzzle";
    }
}
