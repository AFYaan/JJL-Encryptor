package pl.afyaan.encryptor;

import pl.afyaan.encryptor.options.ArgsParser;
import pl.afyaan.encryptor.options.Option;

/**
 * @author AFYaan
 * @created 09.03.2021
 * @project JJL-Encryptor
 */

class App {

    public static void main(String[] args) {
        ArgsParser parser = new ArgsParser(args);
        Option pathOpt = new Option("-path", "jar file path");
        Option passwordOpt = new Option("-password", "encryption password");

        parser.add(pathOpt);
        parser.add(passwordOpt);
        parser.analyzeArgs();

        String path = parser.getValue("-path");
        String password = parser.getValue("-password");

        if(Util.isNull(path, password)){
            System.out.println("USAGE: -password <" + passwordOpt.getDescription() +
                    "> -path <" + pathOpt.getDescription() + ">");
            System.exit(0);
        }

        Encryptor encryptor = new Encryptor(path);
        encryptor.encrypt(password);
    }
}