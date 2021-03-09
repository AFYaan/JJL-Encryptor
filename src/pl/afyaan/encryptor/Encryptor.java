package pl.afyaan.encryptor;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.*;
import java.util.jar.*;

/**
 * @author AFYaan
 * @created 09.03.2021
 * @project JJL-Encryptor
 */

public class Encryptor {
    private final Map<String, byte[]> files = new HashMap<>();
    private SecretKeySpec secretKey;
    private final String path;

    public Encryptor(String path) {
        if(path == null || path.isEmpty()){
            System.out.println("Path is empty");
            System.exit(0);
        }
        File file = new File(path);
        if(!file.exists()){
            System.out.println("File not found: " + file.getAbsolutePath());
            System.exit(0);
        }

        if(!file.getName().endsWith(".jar")){
            System.out.println("File is not jar: " + file.getName());
            System.exit(0);
        }

        this.path = path;
    }

    private void setKey(String key){
        if(key == null || key.isEmpty()) throw new RuntimeException("Key is empty");
        try {
            byte[] keyData = key.getBytes(StandardCharsets.UTF_8);
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            keyData = sha.digest(keyData);
            keyData = Arrays.copyOf(keyData, 16);
            this.secretKey = new SecretKeySpec(keyData, "AES");
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void encrypt(String key){
        System.out.println("Starting encryption...");
        setKey(key);
        long start = System.currentTimeMillis();
        getClasses();
        createTempJar();
        encryptTemp();
        createResFile();
        createRawDataFile();
        long end = System.currentTimeMillis();
        System.out.println("\nEncryption complete in: " + (end - start) + "ms");
    }

    private void getClasses(){
        File inputFile = new File(path);
        if(!inputFile.exists()){
            System.out.println("Input file not exists");
        }

        System.out.println("\nOriginal jar files:");

        try {
            InputStream is = new DataInputStream(new FileInputStream(inputFile));
            JarInputStream jis = new JarInputStream(is);
            JarEntry je;
            String entryName;

            while ((je = jis.getNextJarEntry()) != null){
                entryName = je.getName();
                byte[] classBytes = readClass(jis);
                files.put(entryName, classBytes);

                if(!entryName.endsWith("/"))
                    System.out.println("   " + entryName);
            }

            jis.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createTempJar(){
        System.out.println("\nCreating temp jar...");
        try {
            String path = this.path.replace(".jar", "-temp.jar");
            JarOutputStream jos = new JarOutputStream(new FileOutputStream(path));

            for(String entryName : files.keySet()){
                if(!entryName.endsWith(".class")) {
                    continue;
                }
                JarEntry entry = new JarEntry(entryName);
                jos.putNextEntry(entry);
                jos.write(files.get(entryName));
            }

            jos.close();
            System.out.println("Temp jar created");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void encryptTemp(){
        System.out.println("\nCreating encrypted jar...");
        try{
            String tempPath = this.path.replace(".jar", "-temp.jar");
            File inputFile = new File(tempPath);
            if(!inputFile.exists()){
                System.out.println("Input file not exists");
            }

            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            String encryptedPath = this.path.replace(".jar", "-encrypted.jar");
            FileOutputStream out = new FileOutputStream(encryptedPath);
            out.write(cipher.doFinal(Files.readAllBytes(inputFile.toPath())));
            out.close();

            System.out.println("Encrypted jar files:");
            InputStream is = new DataInputStream(new FileInputStream(inputFile));
            JarInputStream jis = new JarInputStream(is);
            JarEntry je;
            while ((je = jis.getNextJarEntry()) != null){
                System.out.println("   " + je.getName());
            }

            jis.close();
            is.close();
            System.out.println("Encrypted jar created");

            Files.delete(inputFile.toPath());
            System.out.println("\nTemp jar removed");
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void createResFile(){
        if(!hasResources()){
            System.out.println("\nResources jar not created (original jar don't have resources)");
            return;
        }

        System.out.println("\nCreating resources jar...");
        try {
            String resourcesPath = this.path.replace(".jar", "-resources.jar");
            JarOutputStream jos = new JarOutputStream(new FileOutputStream(resourcesPath));

            System.out.println("Resources jar files:");
            for(String entryName : files.keySet()){
                if(checkIfFileIsIgnored(entryName)) continue;

                JarEntry entry = new JarEntry(entryName);
                jos.putNextEntry(entry);
                jos.write(files.get(entryName));
                System.out.println("   " + entryName);
            }
            System.out.println("Resources jar created");
            jos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] readClass(InputStream stream) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        while(true){
            int qwe = stream.read();
            if(qwe == -1) break;
            baos.write(qwe);
        }
        return baos.toByteArray();
    }

    private void createRawDataFile(){
        System.out.println("\nCreating C++ resource jar...");
        String encryptedPath = this.path.replace(".jar", "-encrypted.jar");
        File inputFile = new File(encryptedPath);
        if(!inputFile.exists()){
            System.out.println("Encrypted jar not exists");
            System.exit(0);
        }
        try {
            byte[] data = Files.readAllBytes(inputFile.toPath());
            StringBuilder sb = new StringBuilder();
            sb.append("const unsigned char rawData[").append(data.length).append("] = {\n    ");

            int lineLength = 0;
            int maxLineLength = 18;
            for(byte b : data){
                if(lineLength == maxLineLength){
                    sb.append("\n    ");
                    lineLength = 0;
                }
                sb.append("0x").append(String.format("%02x", b).toUpperCase()).append(",");
                if(lineLength < maxLineLength - 1){
                    sb.append(" ");
                }
                lineLength++;
            }
            sb.delete(sb.toString().length() - 1, sb.toString().length()).append("\n");
            sb.append("};");

            String resHeaderPath = this.path.replace(".jar", "-resource.h");
            FileOutputStream out = new FileOutputStream(resHeaderPath);
            out.write(sb.toString().getBytes(StandardCharsets.UTF_8));
            out.close();
            System.out.println("C++ resource created");
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private boolean hasResources(){
        return files.keySet().stream().anyMatch(name -> !checkIfFileIsIgnored(name));
    }

    private boolean checkIfFileIsIgnored(String fileName){
        String[] ignored = {".class", "/", ".MF"};
        return Arrays.stream(ignored).anyMatch(fileName::endsWith);
    }
}
