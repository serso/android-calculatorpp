// Copyright (C) 2009 Mihai Preda

package arity.calculator;

import android.content.Context;
import java.io.*;
import java.util.ArrayList;

class History extends FileHandler {
    private static final int SIZE_LIMIT = 30;
    ArrayList<HistoryEntry> entries = new ArrayList<HistoryEntry>();
    int pos;
    HistoryEntry aboveTop = new HistoryEntry("", "");

            
    History(Context context) {
	super(context, "history", 1);
	load();
    }

    void clear() {
        entries.clear();
        pos = 0;
    }

    int size() {
        return entries.size();
    }

    void doRead(DataInputStream is) throws IOException {
	aboveTop = new HistoryEntry(is);
	int loadSize = is.readInt();
	for (int i = 0; i < loadSize; ++i) {                
	    entries.add(new HistoryEntry(is));
	}
	pos = entries.size();
    }

    void doWrite(DataOutputStream os) throws IOException {
	aboveTop.save(os);
	os.writeInt(entries.size());
	for (HistoryEntry entry : entries) {
	    entry.save(os);
	}
    }
    
    private HistoryEntry currentEntry() {
        if (pos < entries.size()) {
            return entries.get(pos);
        } else {
            return aboveTop;
        }
    }

    int getListPos() {
        return entries.size() - 1 - pos;
    }

    boolean onEnter(String text, String result) {
	if (result == null) {
	    result = "";
	}
        currentEntry().onEnter();
        pos = entries.size();
        if (text.length() == 0) {
            return false;
        }
        if (entries.size() > 0) {
            HistoryEntry top = entries.get(entries.size()-1);
            if (text.equals(top.line) && result.equals(top.result)) {
                return false;
            }
        }
        if (entries.size() > SIZE_LIMIT) {
            entries.remove(0);
        }
        entries.add(new HistoryEntry(text, result));
        pos = entries.size();
        return true;
    }

    void moveToPos(int listPos, String text) {
	currentEntry().editLine = text;
	pos = entries.size() - listPos - 1;
    }

    void updateEdited(String text) {
        currentEntry().editLine = text;
    }

    boolean moveUp(String text) {
        updateEdited(text);
        if (pos >= entries.size()) {
            return false;
        }
        ++pos;
        return true;
    }
    
    boolean moveDown(String text) {
        updateEdited(text);
        if (pos <= 0) {
            return false;
        }
        --pos;
        return true;
    }

    String getText() {
        return currentEntry().editLine;
    }    
}
