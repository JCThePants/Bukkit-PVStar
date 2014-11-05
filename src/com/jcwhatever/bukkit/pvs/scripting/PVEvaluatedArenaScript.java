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


package com.jcwhatever.bukkit.pvs.scripting;

import com.jcwhatever.bukkit.generic.scripting.IScript;
import com.jcwhatever.bukkit.generic.scripting.api.IScriptApi;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.api.scripting.EvaluatedScript;
import com.jcwhatever.bukkit.pvs.api.scripting.Script;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

/*
 * A script that has been evaluated for an arena.
 */
public class PVEvaluatedArenaScript implements EvaluatedScript {

    private final Arena _arena;
    private final ScriptEngine _engine;
    private final Script _parentScript;
    private final Map<String, IScriptApi> _scriptApis;

    /*
     * Constructor.
     */
    public PVEvaluatedArenaScript(Arena arena, ScriptEngine engine, Script parentScript, @Nullable Collection<? extends IScriptApi> apiCollection) {
        PreCon.notNull(arena);
        PreCon.notNull(engine);
        PreCon.notNull(parentScript);

        _arena = arena;
        _engine = engine;
        _parentScript = parentScript;
        _scriptApis = new HashMap<>(apiCollection == null ? 10 : apiCollection.size());

        if (apiCollection != null) {
            for (IScriptApi api : apiCollection) {
                _scriptApis.put(api.getVariableName(), api);
            }
        }
    }

    /*
     * Get the arena the script was evaluated for.
     */
    @Override
    public Arena getArena() {
        return _arena;
    }

    /*
     * Get the script that was evaluated.
     */
    @Override
    public Script getParentScript() {
        return _parentScript;
    }

    /*
     * Get the script engine used to evaluate.
     */
    @Override
    public ScriptEngine getScriptEngine() {
        return _engine;
    }

    /*
     * Get the api included in the evaluated script.
     */
    @Override
    public List<IScriptApi> getScriptApi() {
        return _scriptApis == null
                ? new ArrayList<IScriptApi>(0)
                : new ArrayList<>(_scriptApis.values());
    }

    @Override
    public void addScriptApi(IScriptApi scriptApi) {

        if (_scriptApis.containsKey(scriptApi.getVariableName()))
            return;

        _scriptApis.put(scriptApi.getVariableName(), scriptApi);

        _engine.put(scriptApi.getVariableName(), scriptApi.getApiObject(this));
    }

    /*
     * Invoke a script function in the evaluated script.
     */
    @Override
    @Nullable
    public Object invokeFunction(String functionName, Object... parameters) {
        Invocable inv = (Invocable)_engine;

        try {
            return inv.invokeFunction(functionName, parameters);
        }
        catch (NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
        catch (ScriptException e) {
            e.printStackTrace();
            return null;
        }
    }

    /*
     * Evaluate another script into this scripts engine.
     */
    @Override
    @Nullable
    public Object evaluate(IScript script) {

        try {
            return _engine.eval(script.getScript());
        }
        catch (ScriptException e) {
            e.printStackTrace();
            return null;
        }
    }

    /*
     * Reset the evaluated scripts api.
     */
    @Override
    public void resetApi() {

        if (_scriptApis == null)
            return;

        for (IScriptApi api : _scriptApis.values()) {
            api.reset();
        }
    }
}
