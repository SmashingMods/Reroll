package com.smashingmods.reroll.util;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.smashingmods.reroll.handler.RerollHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class JsonWriter {

    private final ObjectMapper mapper = new ObjectMapper();
    private static String path;

    public JsonWriter(String path) {
        this.path = path;
    }

    public void writeJson(File file, Object obj) {
        try {
            mapper.writeValue(file, obj);
        } catch (FileNotFoundException e) {
            try {
                file.createNewFile();
                writeJson(file, obj);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeJson(String fileName, Object obj) {
        writeJson(new File(path, fileName), obj);
    }

    public RerollHandler.Rerolls readJson(String fileName, Class clazz) {
        try {
            return (RerollHandler.Rerolls) mapper.readValue(new File(path, fileName), clazz);
        } catch (FileNotFoundException e) {
            File file = new File(path, fileName);
            try {
                file.createNewFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
