package pl.afyaan.encryptor.options;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author AFYaan
 * @created 09.03.2021
 * @project JJL-Encryptor
 *
 * Copyright 2021 AFYaan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License atv
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class ArgsParser {
    private String[] args;
    private final List<Option> options = new ArrayList<>();
    private final Map<String, String> values = new HashMap<>();

    public ArgsParser(String[] args) {
        this.args = args;
    }

    public boolean add(Option option) {
        return options.add(option);
    }

    public void analyzeArgs(){
        if(args.length == 0) return;
        for(int o = 0; o < options.size(); o++){
            Option opt = options.get(o);
            for(int i = 0; i < args.length; i++){
                if(args[i].equalsIgnoreCase(opt.getName())){
                    StringBuilder sb = new StringBuilder();
                    for(int v = i + 1; v < args.length; v++){
                        String arg = args[v];
                        if(options.stream().anyMatch(option -> arg.equalsIgnoreCase(option.getName()))) break;
                        sb.append(arg).append(" ");
                    }
                    if(!sb.toString().isEmpty())
                        values.put(opt.getName(), sb.substring(0, sb.toString().length() - 1));
                }
            }
        }
    }

    public String getValue(String optionName){
        return values.get(optionName);
    }

}
