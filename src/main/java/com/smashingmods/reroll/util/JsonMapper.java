package com.smashingmods.reroll.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class JsonMapper {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static File file;

    public JsonMapper() {}

    public <T> T readFile(File file, Class<T> clazz) {
        try {
            return mapper.readValue(file, clazz);
        } catch (FileNotFoundException e) {
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

    public <T> T readFile(String fileName, Class<T> clazz) {
        return readFile(new File(file, fileName), clazz);
    }

    public <T> T readFile(Class<T> clazz) {
        return readFile(file, clazz);
    }

    public void writeFile(File file, Object obj) {
        try {
            mapper.writeValue(file, obj);
        } catch (FileNotFoundException e) {
            try {
                file.createNewFile();
                writeFile(file, obj);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeFile(String fileName, Object obj) {
        writeFile(new File(file, fileName), obj);
    }

    public void writeFile(Object obj) {
        writeFile(file, obj);
    }

    public void setFile(File file) {
        this.file = file;
    }
}
