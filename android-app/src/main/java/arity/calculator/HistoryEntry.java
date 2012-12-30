// Copyright (C) 2009 Mihai Preda

package arity.calculator;

import java.io.*;

class HistoryEntry {
    String line, editLine, result;

    HistoryEntry(DataInputStream is) throws IOException {
        line = is.readUTF();
        editLine = is.readUTF();
        if (editLine.length() == 0) {
            editLine = line;
        }
        result = is.readUTF();
    }

    HistoryEntry(String text, String result) {
        line = text;
        editLine = text;
        this.result = result == null ? "" : result;
    }

    void save(DataOutputStream os) throws IOException {
        os.writeUTF(line);
        os.writeUTF(editLine.equals(line) ? "" : editLine);
        os.writeUTF(result);
    }

    void onEnter() {
        editLine = line;
    }
}
