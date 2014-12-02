/*
 * This file is part of PV-Star for Bukkit, licensed under the MIT License (MIT).
 *
 * Copyright (c) JCThePants (www.jcwhatever.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


package com.jcwhatever.bukkit.pvs.modules;

import com.jcwhatever.bukkit.generic.modules.IModuleInfo;
import com.jcwhatever.bukkit.generic.storage.YamlDataStorage;
import com.jcwhatever.bukkit.generic.utils.TextUtils;
import com.jcwhatever.bukkit.pvs.api.PVStarAPI;
import com.jcwhatever.bukkit.pvs.api.modules.ModuleInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/*
 * Loads and stores module info from module.yml file
 * in the module jar file.
 */
public class PVModuleInfo implements ModuleInfo, IModuleInfo {

    private String _name;
    private String _searchName;
    private String _version;
    private String _description;
    private long _logicalVersion;
    private List<String> _authors;
    private Set<String> _bukkitDepends;
    private Set<String> _bukkitSoftDepends;
    private Set<String> _moduleDepends;
    private Set<String> _moduleSoftDepends;
    private boolean _isLoaded;

    /*
     * Constructor. Pass in module.yml text string.
     */
    public PVModuleInfo(String moduleInfoString) {
        _isLoaded = load(moduleInfoString);
    }

    /*
     * Determine if the module information was
     * successfully loaded in the constructor.
     */
    public boolean isLoaded() {
        return _isLoaded;
    }

    /*
     * Get the name of the module
     */
    @Override
    public String getName() {
        return _name;
    }

    /*
     * Get the name of the module in lower case.
     */
    @Override
    public String getSearchName() {
        return _searchName;
    }

    /*
     * Get the modules display version.
     */
    @Override
    public String getVersion() {
        return _version;
    }

    /*
     * Get the module description.
     */
    @Override
    public String getDescription() {
        return _description;
    }

    /**
     * Get the modules author names.
     */
    @Override
    public List<String> getAuthors() {
        return _authors;
    }

    /*
     * Get the modules logical version used to solve
     * issues with loading two conflicting versions.
     */
    @Override
    public long getLogicalVersion() {
        return _logicalVersion;
    }

    /*
     * Get the names of Bukkit plugins the module
     * depends on.
     */
    @Override
    public Set<String> getBukkitDepends() {
        return _bukkitDepends;
    }

    /*
     * Get the names of Bukkit plugins the module
     * has a soft dependency on.
     */
    @Override
    public Set<String> getBukkitSoftDepends() {
        return _bukkitSoftDepends;
    }

    /*
     * Get the names of PV-Star modules the module
     * depends on.
     */
    @Override
    public Set<String> getModuleDepends() {
        return _moduleDepends;
    }

    /*
     * Get the names of PV-Star modules tje module
     * has a soft dependency on.
     */
    @Override
    public Set<String> getModuleSoftDepends() {
        return _moduleSoftDepends;
    }

    /*
     * Load module info from yaml string.
     */
    private boolean load(String moduleInfoString) {

        // Load yaml string into data node.
        YamlDataStorage moduleNode = new YamlDataStorage(PVStarAPI.getPlugin(), moduleInfoString);
        if (!moduleNode.load())
            return false;

        // get the required name of the module
        _name = moduleNode.getString("name");
        if (_name == null)
            return false;

        // convert the name to lower case.
        _searchName = _name.toLowerCase();

        // get the required logical version
        _logicalVersion = moduleNode.getLong("logical-version", -1);
        if (_logicalVersion < 0)
            return false;

        // get the optional display version
        _version = moduleNode.getString("display-version", "v" + _logicalVersion);

        // get the optional description
        _description = moduleNode.getString("description", "");

        // get module authors
        String rawAuthors = moduleNode.getString("authors");

        if (rawAuthors == null) {
            _authors = Collections.unmodifiableList(new ArrayList<String>(0));
        }
        else {
            String[] authorArray = TextUtils.PATTERN_COMMA.split(rawAuthors);
            ArrayList<String> authors = new ArrayList<>(authorArray.length);

            for (String author : authorArray) {
                authors.add(author.trim());
            }

            _authors = Collections.unmodifiableList(authors);
        }

        // get Bukkit dependencies
        List<String> bukkitDepends = moduleNode.getStringList("bukkit-depends",
                null /* null to prevent unnecessary object creation*/);

        if (bukkitDepends == null)
            bukkitDepends = new ArrayList<>(0);

        _bukkitDepends = Collections.unmodifiableSet(new HashSet<>(bukkitDepends));

        // get Bukkit soft dependencies
        List<String> bukkitSoftDepends = moduleNode.getStringList("bukkit-soft-depends",
                null /* null to prevent unnecessary object creation*/);

        if (bukkitSoftDepends == null)
            bukkitSoftDepends = new ArrayList<>(0);

        _bukkitSoftDepends = Collections.unmodifiableSet(new HashSet<>(bukkitSoftDepends));

        // get PV-Star module dependencies
        List<String> moduleDepends = moduleNode.getStringList("depends",
                null /* null to prevent unnecessary object creation*/);
        if (moduleDepends == null)
            moduleDepends = new ArrayList<>(0);

        _moduleDepends = Collections.unmodifiableSet(new HashSet<>(moduleDepends));

        // get PV-Star module soft dependencies
        List<String> moduleSoftDepends = moduleNode.getStringList("soft-depends",
                null /* null to prevent unnecessary object creation*/);

        if (moduleSoftDepends == null)
            moduleSoftDepends = new ArrayList<>(0);

        _moduleSoftDepends = Collections.unmodifiableSet(new HashSet<>(moduleSoftDepends));

        return true;
    }
}
