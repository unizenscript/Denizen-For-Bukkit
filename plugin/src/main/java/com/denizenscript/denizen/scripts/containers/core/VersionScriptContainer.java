package com.denizenscript.denizen.scripts.containers.core;

import com.denizenscript.denizencore.scripts.containers.ScriptContainer;
import com.denizenscript.denizencore.utilities.Deprecations;
import com.denizenscript.denizencore.utilities.YamlConfiguration;

import java.util.ArrayList;
import java.util.List;

public class VersionScriptContainer extends ScriptContainer {

    // <--[language]
    // @name Version Script Containers
    // @group Script Container System
    // @description
    // Version script containers are used to identify a public script's version, author, and other basic information.
    //
    // <code>
    // Version_Script_Name:
    //
    //   type: version
    //   version: 0.7.3
    //   name: My Public Script
    //   author: Clearly, me
    //
    // </code>
    //
    // -->

    public static List<VersionScriptContainer> scripts = new ArrayList<>();

    public VersionScriptContainer(YamlConfiguration configurationSection, String scriptContainerName) {
        super(configurationSection, scriptContainerName);
        scripts.add(this);
    }
}
