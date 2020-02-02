package controller.jsonBody;

import java.io.File;

public class FileDecorator {
    private File file;
    private long chatId;
    public FileDecorator set(File file){
        this.file=file;
        return this;
    }
    public FileDecorator set(long chatId){
        this.chatId=chatId;
        return this;
    }

    public File getFile() {
        return file;
    }

    public long getChatId() {
        return chatId;
    }
}
