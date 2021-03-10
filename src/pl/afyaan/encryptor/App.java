package pl.afyaan.encryptor;

import pl.afyaan.encryptor.options.ArgsParser;
import pl.afyaan.encryptor.options.Option;

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

class App {

    public static void main(String[] args) {
        ArgsParser parser = new ArgsParser(args);
        Option pathOpt = new Option("-path", "jar path");
        Option passwordOpt = new Option("-password", "encryption password");

        parser.add(pathOpt);
        parser.add(passwordOpt);
        parser.analyzeArgs();

        String path = parser.getValue("-path");
        String password = parser.getValue("-password");

        if(Utils.isNull(path)){
            System.out.println("USAGE: -password <" + passwordOpt.getDescription() +
                    "> -path <" + pathOpt.getDescription() + ">");
            System.exit(0);
        }

        Encryptor encryptor = new Encryptor(path);
        if(Utils.isNull(password)){
            encryptor.createRawDataFile();
        }else {
            encryptor.encrypt(password);
        }
    }
}