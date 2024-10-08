package com.github.puzzle;

import org.objectweb.asm.Opcodes;

public class Constants {
    public static final String COSMIC_REACH_REPO = "https://github.com/CRModders/CosmicArchive/raw/main/versions/pre-alpha";
    public static final String COSMIC_REACH_CLIENT_JAR_NAME = "/[revision]/[classifier]/Cosmic Reach-[revision].jar";
    public static final String COSMIC_REACH_SERVER_JAR_NAME = "/[revision]/[classifier]/Cosmic Reach-Server-[revision].jar";

    public static final String[] REPOS = new String[] {
            "https://jitpack.io", // Jitpack
            "https://maven.fabricmc.net/", // Fabric
            "https://repo.spongepowered.org/repository/maven-public/", // Sponge
            "https://libraries.minecraft.net", // Libraries
            "https://maven.google.com/" // Google
    };


    public static final int ASM_VERSION = Opcodes.ASM9;

    public static final class Tasks {
        public static final String GROUP = "puzzle";
    }
}
